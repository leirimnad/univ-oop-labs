package ua.leirimnad.lab2;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.imageio.ImageIO;

public class TimerController {
    Timer a;
    List<Alarm> alarms = new ArrayList<>();
    List<SetClockGroup> groups = new ArrayList<>();
    SetClockGroup initialClockGroup;
    SetClock nearestClock;
    NamedSetClockComparator selectedComparator;
    NamedSetClockComparator customOrderComparator = new NamedSetClockComparator("☝ Свiй порядок", (o1, o2) -> 0);

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button addButton;

    @FXML
    private Button sortButton;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox scrollVBox;

    @FXML
    private Label placeholderLabel;

    @FXML
    void initialize() {
        Timeline timeline = new Timeline( new KeyFrame(Duration.millis(500), event -> {
            for (Alarm alarm : alarms){
                alarm.tick();
            }

            // sortClocks();
            updateNearestClock();
            float progress;
            if(nearestClock == null) progress = -1;
            else progress = 1-((float) nearestClock.getTimeLeft())/nearestClock.getTotalDuration();
            updateTaskBarProgress(progress);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();



        final ContextMenu sortContextMenu = new ContextMenu();

        sortContextMenu.getItems().add(new NamedSetClockComparator("☆ Новi",
                (o1, o2) -> Long.signum(o1.timeCreated.until(o2.timeCreated, ChronoUnit.MILLIS))).createMenuItem());
        sortContextMenu.getItems().add(new NamedSetClockComparator("☾ Старi",
                (o1, o2) -> -Long.signum(o1.timeCreated.until(o2.timeCreated, ChronoUnit.MILLIS))).createMenuItem());
        sortContextMenu.getItems().add(new NamedSetClockComparator("↓ За часом спрацювання",
                (o1, o2) -> -Long.signum(o1.getTimeLeft() - o2.getTimeLeft())).createMenuItem());
        sortContextMenu.getItems().add(new NamedSetClockComparator("↑ За часом спрацювання",
                (o1, o2) -> Long.signum(o1.getTimeLeft() - o2.getTimeLeft())).createMenuItem());
        sortContextMenu.getItems().add(new NamedSetClockComparator("↓ За тривалiстю",
                (o1, o2) -> -Long.signum(o1.getTotalDuration() - o2.getTotalDuration())).createMenuItem());
        sortContextMenu.getItems().add(new NamedSetClockComparator("↑ За тривалiстю",
                (o1, o2) -> Long.signum(o1.getTotalDuration() - o2.getTotalDuration())).createMenuItem());


        sortButton.setOnMouseClicked(event -> sortContextMenu.show(sortButton, event.getScreenX(), event.getScreenY()));


        setClockSortingComparator(new NamedSetClockComparator("↓ Сортувати...",
                (o1, o2) -> -Long.signum(o1.timeCreated.until(o2.timeCreated, ChronoUnit.MILLIS))));

        initialClockGroup = new SetClockGroup("Без групи");
        initialClockGroup.setStyleId("initialGroupBox");
        addGroup(initialClockGroup);

        addButton.setOnMouseClicked(event -> {
            try{
                FXMLLoader loader = new FXMLLoader(TimerApplication.class.getResource("addTimer-view.fxml"));

                Parent  addTimerRoot = loader.load();
                AddTimerController controller = loader.getController();
                controller.initData(groups, initialClockGroup);

                Stage addTimerStage = new Stage();
                addTimerStage.setTitle("Створити таймер");
                addTimerStage.setScene(new Scene(addTimerRoot));
                addTimerStage.setResizable(false);

                addTimerStage.setOnCloseRequest(e->{
                    if(addTimerStage.getUserData() != null){
                        SetClock newSetClock = (SetClock) addTimerStage.getUserData();
                        newSetClock.setOnDelete(ev-> {
                            updateGroups();
                            updateNearestClock();
                        });
                        newSetClock.setOnTick(ev->sortClocks());
                        newSetClock.setOnDelete(ev->sortClocks());
                        newSetClock.setOnUnset(ev->sortClocks());
                        if (addTimerStage.getUserData().getClass().equals(Alarm.class)){
                            alarms.add((Alarm) addTimerStage.getUserData());
                        }
                        if(!groups.contains(newSetClock.getGroup())){
                            addGroup(newSetClock.getGroup());
                        }
                        newSetClock.setOnDrag(dragEvent->{
                            setClockSortingComparator(customOrderComparator);
                            System.out.println(dragEvent.getGestureSource().getClass().getName());
                            AnchorPane sourceWidget = (AnchorPane) dragEvent.getGestureSource();

                            System.out.println("Type is "+dragEvent.getGestureTarget().getClass().getName());
                            AnchorPane targetWidget = (AnchorPane) dragEvent.getGestureTarget();

                            SetClock sourceClock = getSetClockFromWidget(sourceWidget);
                            SetClock targetClock = getSetClockFromWidget(targetWidget);
                            assert sourceClock != null;
                            assert targetClock != null;
                            if(!sourceClock.getGroup().equals(targetClock.getGroup())) return;
                            targetClock.getGroup().replaceClock(
                                    sourceClock, targetClock.getGroup().indexOf(targetClock));
                            System.out.println("DRAG "+sourceClock+"\nON "+targetClock);
                            sortClocks();
                        });

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

    private void addGroup(SetClockGroup group){
        groups.add(group);
        updateGroups();
        updateNearestClock();
    }

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
        if(total > 0) scrollVBox.getChildren().remove(placeholderLabel);
        else if(!scrollVBox.getChildren().contains(placeholderLabel))
            scrollVBox.getChildren().add(0, placeholderLabel);
    }

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

    private void updateTaskBarProgress(float progress) {
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

    private void setClockSortingComparator(NamedSetClockComparator comparator){
        selectedComparator = comparator;
        sortButton.setText(comparator.getName());
        sortClocks();
    }

    private void sortClocks(){
        for (SetClockGroup group : groups){
            group.sortClocks(selectedComparator.getComparator());
        }
    }

    private class NamedSetClockComparator {
        String name;
        Comparator<SetClock> comparator;

        public NamedSetClockComparator(String name, Comparator<SetClock> comparator){
            this.name = name;
            this.comparator = comparator;
        }

        public String getName() {
            return name;
        }

        public Comparator<SetClock> getComparator() {
            return comparator;
        }

        public MenuItem createMenuItem(){
            MenuItem it = new MenuItem(name);
            it.setOnAction(e->setClockSortingComparator(this));
            return it;
        }
    }

    private SetClock getSetClockFromWidget(AnchorPane widget){
        for (SetClockGroup group : groups){
            for (SetClock clock : group.getSetClocks())
                if(clock.getWidget().equals(widget))
                    return clock;
        }
        return null;
    }



}
