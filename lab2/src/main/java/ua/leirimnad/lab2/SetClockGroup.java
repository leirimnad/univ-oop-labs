package ua.leirimnad.lab2;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.*;

/**
 * Class representing a group of {@link SetClock#SetClock SetClocks}.<br>
 * Has its widget and logic.
 */
public class SetClockGroup {
    private final String name;
    private final List<SetClock> setClocks = new ArrayList<>();
    private VBox widget;
    private FlowPane flowPane;
    private String styleId;
    private EventHandler<ActionEvent> onClockListChange;

    /**
     * Constructs a SetClockGroup with a given name and adds all the given clocks to it.
     * @param name name
     * @param clocks clocks to add
     */
    public SetClockGroup(String name, SetClock ...clocks){
        this.name = name;
        for (SetClock clock : clocks){
            add(clock);
        }
    }

    /**
     * Adds a clock to a group.
     * @param setClock clock to add
     */
    public void add(SetClock setClock){
        setClocks.add(setClock);
        if(widget != null) updateWidget();
        if(onClockListChange != null) onClockListChange.handle(new ActionEvent());
    }

    /**
     * Removes a clock from a group.
     * @param setClock clock to remove
     */
    public void remove(SetClock setClock){
        setClocks.remove(setClock);
        if(widget != null) updateWidget();
        if(onClockListChange != null) onClockListChange.handle(new ActionEvent());
    }

    /**
     * Replaces a clock in position <code>pos</code> with the <code>setClock</code>.
     * @param setClock clock to be added
     * @param pos pos of the clock to be replaced
     */
    public void replaceClock(SetClock setClock, int pos) {
        if(!setClocks.contains(setClock))
            throw new NoSuchElementException("There is no such clock in the group to be replaced");
        if(pos < 0 || pos > setClocks.size()-1)
            throw new IndexOutOfBoundsException("Incorrect position argument");

        setClocks.remove(setClock);
        setClocks.add(pos, setClock);
        setClock.setIndicateEffect(300);
        if(widget != null) updateWidget();
        if(onClockListChange != null) onClockListChange.handle(new ActionEvent());
    }

    /**
     * Returns the index of a given clock.
     * @param setClock clock
     * @return index of a clock
     */
    public int indexOf(SetClock setClock){
        return setClocks.indexOf(setClock);
    }

    /**
     * Updates a widget.<br>
     * If the clock widget is already in its position, does not touch it.
     */
    public void updateWidget(){
        if(widget == null) return;

        ObservableList<Node> children = flowPane.getChildren();
        for (int i=0; i < Math.max(children.size(), setClocks.size()); i++){
            Node child = (children.size() - 1 < i ? null : children.get(i));
            Node widget = (setClocks.size() - 1 < i ? null : setClocks.get(i).getWidget());

            if(i > setClocks.size() - 1){
                children.remove(i, children.size()-1);
                break;
            }
            if(i > children.size() - 1){
                children.add(widget);
                continue;
            }

            if(child != null && child.equals(widget)){
                continue;
            } else {
                if(children.contains(widget)) {
                    Node nullNode = new Separator();
                    children.set(children.indexOf(widget), nullNode);
                }
                children.set(i, widget);
            }
        }
        this.widget.setId(styleId);
    }

    /**
     * Returns the group's widget.
     * @return group's widget
     */
    public VBox getWidget(){
        if(widget == null) createWidget();
        updateWidget();
        return widget;
    }


    /**
     * Returns a list of all the clocks in the group.
     * @return list of clocks
     */
    public List<SetClock> getSetClocks() {
        return setClocks;
    }

    /**
     * Sets CSS id for the group's widget.
     * @param id id
     */
    public void setStyleId(String id){
        this.styleId = id;
    }


    /**
     * Sets a function to be called when the group's clock list is changed.
     * @param onClockListChange function to be called
     */
    public void setOnClockListChange(EventHandler<ActionEvent> onClockListChange) {
        this.onClockListChange = onClockListChange;
    }

    /**
     * Removes group's widget from pane.
     * @param pane pane
     */
    public void removeWidgetFrom(Pane pane){
        pane.getChildren().remove(this.widget);
    }

    /**
     * Adds group's widget to pane.
     * @param pane pane
     */
    public void addWidgetTo(Pane pane){
        pane.getChildren().add(getWidget());
    }

    /**
     * Checks if the group's widget is a child of a <code>pane</code>.
     * @param pane pane
     * @return is group's widget a child of pane
     */
    public boolean isInsideOf(Pane pane){
        if(this.widget == null) return false;
        return pane.getChildren().contains(this.widget);
    }

    /**
     * Creates a widget for a group.
     */
    private void createWidget(){
        widget = new VBox();
        widget.getStyleClass().add("groupBox");

        widget.setStyle("-fx-background-color:  rgb("+getRandomNumber(180, 240)+","
                +getRandomNumber(180, 240)+","
                +getRandomNumber(180, 240)+")"); // TODO: 17.10.2021 random color

        HBox hBox = new HBox();
        hBox.getStyleClass().add("groupControlsBox");
        widget.getChildren().add(hBox);

        Label nameLabel = new Label();
        nameLabel.setText(name);
        nameLabel.getStyleClass().add("groupNameLabel");
        hBox.getChildren().add(nameLabel);

        Button startButton = new Button("▶ Запустити");
        startButton.getStyleClass().addAll("groupControlsButton", "groupStartButton");
        hBox.getChildren().add(startButton);
        startButton.setOnMouseClicked(e->{
            for (SetClock setClock : setClocks){
                setClock.set(true);
            }
        });

        Button stopButton = new Button("■ Зупинити");
        stopButton.getStyleClass().addAll("groupControlsButton", "groupStopButton");
        hBox.getChildren().add(stopButton);
        stopButton.setOnMouseClicked(e->{
            List<SetClock> toBeStopped = new ArrayList<>(setClocks);
            for (SetClock setClock : toBeStopped){
                setClock.set(false);
            }
        });

        flowPane = new FlowPane();
        flowPane.getStyleClass().add("clockFlowPane");
        widget.getChildren().add(flowPane);
    }

    /**
     * @return amount of clocks in the group
     */
    public int size(){
        return setClocks.size();
    }

    /**
     * Sorts clocks using a clock comparator.
     * Sets an effect on clocks that have changed their position.
     * @param comparator clock comparator
     */
    public void sortClocks(Comparator<SetClock> comparator){
        List<SetClock> prevClocks = new ArrayList<>(setClocks);
        this.setClocks.sort(comparator);
        for (int i=0; i < setClocks.size(); i++){
            if(!setClocks.get(i).equals(prevClocks.get(i)))
                setClocks.get(i).setIndicateEffect(300);
        }
        updateWidget();
    }

    /**
     * @return name of the group
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Returns a random int from <code>min</code> to <code>max</code>.
     * @param min min value
     * @param max max value
     * @return random int
     */
    private int getRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

}
