package ua.leirimnad.lab2;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.*;

/**
 * Controller that controls the main window of the app.
 */
public class TimerController {
    List<Alarm> alarms = new ArrayList<>();
    List<SetClockGroup> groups = new ArrayList<>();
    SetClockGroup initialClockGroup;
    SetClock nearestClock;
    NamedSetClockComparator selectedComparator;
    NamedSetClockComparator customOrderComparator = new NamedSetClockComparator("☝ Свiй порядок", (o1, o2) -> 0);

    @FXML
    private Button addButton, sortButton;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox scrollVBox;

    @FXML
    private Label placeholderLabel;

    @FXML
    void initialize() {

        // Timeline for:
        // - all the alarms, so they go off simultaneously
        // - updating task bar icon
        Timeline timeline = new Timeline( new KeyFrame(Duration.millis(500), event -> {
            for (Alarm alarm : alarms){
                alarm.tick();
            }

            updateNearestClock();
            float progress;
            if(nearestClock == null) progress = -1;
            else progress = 1-((float) nearestClock.getTimeLeft())/nearestClock.getTotalDuration();
            updateTaskBarProgress(progress);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // Setting up the sort menu
        final ContextMenu sortContextMenu = new ContextMenu();

        sortContextMenu.getItems().addAll(
                new NamedSetClockComparator("☆ Новi",
                (o1, o2) -> Long.signum(o1.timeCreated.until(o2.timeCreated, ChronoUnit.MILLIS))).createMenuItem(),
                new NamedSetClockComparator("☾ Старi",
                (o1, o2) -> -Long.signum(o1.timeCreated.until(o2.timeCreated, ChronoUnit.MILLIS))).createMenuItem(),
                new NamedSetClockComparator("↑ За часом спрацювання",
                (o1, o2) -> Long.signum(o1.getTimeLeft() - o2.getTimeLeft())).createMenuItem(),
                new NamedSetClockComparator("↓ За часом спрацювання",
                (o1, o2) -> -Long.signum(o1.getTimeLeft() - o2.getTimeLeft())).createMenuItem(),
                new NamedSetClockComparator("↑ За тривалiстю",
                (o1, o2) -> Long.signum(o1.getTotalDuration() - o2.getTotalDuration())).createMenuItem(),
                new NamedSetClockComparator("↓ За тривалiстю",
                (o1, o2) -> -Long.signum(o1.getTotalDuration() - o2.getTotalDuration())).createMenuItem()
        );
        sortButton.setOnMouseClicked(event -> sortContextMenu.show(sortButton, event.getScreenX(), event.getScreenY()));
        setClockSortingComparator(new NamedSetClockComparator("↓ Сортувати...",
                (o1, o2) -> -Long.signum(o1.timeCreated.until(o2.timeCreated, ChronoUnit.MILLIS))));


        // Setting up the default group
        initialClockGroup = new SetClockGroup("Без групи");
        initialClockGroup.setStyleId("initialGroupBox");
        initialClockGroup.getWidget().setStyle("");
        addGroup(initialClockGroup);


        addButton.setOnMouseClicked(event -> {
            try{
                // When add button is pressed, create a new window
                FXMLLoader loader = new FXMLLoader(TimerApplication.class.getResource("addTimer-view.fxml"));

                Parent  addTimerRoot = loader.load();
                AddTimerController controller = loader.getController();
                controller.initData(groups, initialClockGroup);

                Stage addTimerStage = new Stage();
                addTimerStage.setTitle("Створити таймер");
                addTimerStage.setScene(new Scene(addTimerRoot));
                addTimerStage.setResizable(false);
                addTimerStage.getIcons().add(new Image(
                        Objects.requireNonNull(TimerApplication.class.getResourceAsStream("addIcon.png"))
                ));

                // when clock adding window is closed, get the new clock and add it
                addTimerStage.setOnCloseRequest(e->{
                    if(addTimerStage.getUserData() != null){
                        SetClock newSetClock = (SetClock) addTimerStage.getUserData();

                        // Set event handlers
                        newSetClock.setOnTick(ev->sortClocks());
                        newSetClock.setOnDelete(ev-> {
                            updateNearestClock();
                            sortClocks();
                        });
                        newSetClock.setOnTurnOff(ev->sortClocks());
                        newSetClock.setOnDrag(this::handleDrag);

                        // if the new Clock is an Alarm, add it to alarms list
                        if (addTimerStage.getUserData().getClass().equals(Alarm.class)){
                            alarms.add((Alarm) addTimerStage.getUserData());
                        }

                        // add the group to the window if it is the new one
                        if(!groups.contains(newSetClock.getGroup()))
                            addGroup(newSetClock.getGroup());

                        // update everything
                        updateGroups();
                        updateNearestClock();
                        sortClocks();
                    }
                });
                addTimerStage.show();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        });

    }

    /**
     * Does the logic of adding the new group to the window.
     * @param group new group
     */
    private void addGroup(SetClockGroup group){
        groups.add(group);
        group.setOnClockListChange(e->updateGroups());
        updateGroups();
        updateNearestClock();
    }

    /**
     * Deletes a group if it is empty.<br>
     * If there are no groups, shows a placeholder.<br>
     */
    private void updateGroups(){
        int total = 0;
        try {
            for (SetClockGroup group : groups){
                if (group.size() > 0){
                    if(!group.isInsideOf(scrollVBox))
                        group.addWidgetTo(scrollVBox);
                    total += group.size();
                } else {
                    group.removeWidgetFrom(scrollVBox);
                    if (!group.equals(initialClockGroup)){
                        groups.remove(group);
                    }
                }
            }
        } catch (ConcurrentModificationException ignored){

        }
        if(total > 0) {
            scrollVBox.setAlignment(Pos.TOP_LEFT);
            scrollVBox.getChildren().remove(placeholderLabel);
        }
        else if(!scrollVBox.getChildren().contains(placeholderLabel)) {
            scrollVBox.setAlignment(Pos.CENTER);
            scrollVBox.getChildren().add(0, placeholderLabel);
        }
    }

    /**
     * Sets {@link #nearestClock} when some clocks are changed.
     */
    private void updateNearestClock(){
        nearestClock = null;
        for (SetClockGroup group : groups){
            for(SetClock clock : group.getSetClocks()){
                if(!clock.isSet()) continue;
                if(nearestClock == null || clock.getTimeLeft() < nearestClock.getTimeLeft())
                    nearestClock = clock;
            }
        }
    }

    /**
     * Produces and sets a task bar image indicating the progress.<br>
     * <code>progress == -1</code> indicates no progress processes.
     * @param progress progress from 0.0 to 1.0 or -1
     */
    private void updateTaskBarProgress(float progress) {
        if((progress < 0 && progress != -1) || progress > 1) throw new IllegalArgumentException();
        int size = 256;

        BufferedImage bi = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        float progressLeft = 1-progress;

        Graphics2D icon = bi.createGraphics();

        icon.setColor(new Color(250, 250, 250));
        icon.fillRect(0, 0, size, size);
        icon.setColor(new Color(42, 42, 42));
        icon.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        Font font = new Font("Segoe UI", Font.BOLD, 70);

        icon.setFont(font);
        icon.drawString("Таймер",0,size-110);

        // fill with paint if there is some progress
        if(progress != -1){
            if(progress == 1){
                icon.setColor(new Color(255, 0, 0, 192));
                icon.fillRect(0, 0, size, size);
            } else {
                icon.setColor(new Color(54, 71, 255, 180));
                int rectSize = Math.round(progressLeft*size);
                icon.fillRect(0, size-rectSize, size, rectSize);
            }
        }


        Stage curStage = (Stage) scrollPane.getScene().getWindow();

        // Set the image
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(bi, "png", os);
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream is = new ByteArrayInputStream(os.toByteArray());
        if (curStage.getIcons().size() == 0) curStage.getIcons().add(new Image(is));
        else {
            Image prev = curStage.getIcons().get(0);
            curStage.getIcons().add(new Image(is));
            curStage.getIcons().remove(prev);
        }

    }

    /**
     * Sets a comparator to be used when sorting clocks.
     * @param comparator comparator
     */
    private void setClockSortingComparator(NamedSetClockComparator comparator){
        selectedComparator = comparator;
        sortButton.setText(comparator.getName());
        sortClocks();
    }

    /**
     * Sorts all the groups of clocks with the {@link #selectedComparator}.
     */
    private void sortClocks(){
        for (SetClockGroup group : groups){
            group.sortClocks(selectedComparator.getComparator());
        }
    }


    /**
     * Clock comparator with a name to be used in the main window.
     */
    private class NamedSetClockComparator {
        String name;
        Comparator<SetClock> comparator;

        /**
         * Constructs a named comparator with a given name and Comparator
         * @param name name of sort
         * @param comparator comparator
         */
        public NamedSetClockComparator(String name, Comparator<SetClock> comparator){
            this.name = name;
            this.comparator = comparator;
        }

        /**
         * @return name of the comparator
         */
        public String getName() {
            return name;
        }

        /**
         * @return comparator
         */
        public Comparator<SetClock> getComparator() {
            return comparator;
        }

        /**
         * Creates a MenuItem for a context menu with name as a label.
         * @return named menu item
         */
        public MenuItem createMenuItem(){
            MenuItem it = new MenuItem(name);
            it.setOnAction(e->setClockSortingComparator(this));
            return it;
        }
    }

    /**
     * Gets a {@link SetClock#SetClock SetClock} object by a given {@link SetClock#SetClock SetClock} widget.
     * @param widget widget of a clock
     * @return clock
     */
    private SetClock getSetClockFromWidget(AnchorPane widget){
        for (SetClockGroup group : groups){
            for (SetClock clock : group.getSetClocks())
                if(clock.getWidget().equals(widget))
                    return clock;
        }
        return null;
    }

    /**
     * Handles an event in which a clock is dragged somewhere.
     * @param dragEvent event
     */
    private void handleDrag(DragEvent dragEvent){
        setClockSortingComparator(customOrderComparator);

        AnchorPane sourceWidget = (AnchorPane) dragEvent.getGestureSource();
        AnchorPane targetWidget = (AnchorPane) dragEvent.getGestureTarget();

        SetClock sourceClock = getSetClockFromWidget(sourceWidget);
        SetClock targetClock = getSetClockFromWidget(targetWidget);

        if(sourceClock == null || targetClock == null) return;

        // if group needs to ber changed
        if(!sourceClock.getGroup().equals(targetClock.getGroup())) {
            sourceClock.changeGroup(targetClock.getGroup());
        }

        // replace clock
        targetClock.getGroup().replaceClock(
                sourceClock, targetClock.getGroup().indexOf(targetClock));

        sortClocks();
    }

}
