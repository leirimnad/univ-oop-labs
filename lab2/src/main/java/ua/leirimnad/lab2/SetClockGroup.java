package ua.leirimnad.lab2;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.util.*;
import java.util.List;

public class SetClockGroup {
    private String name;
    private List<SetClock> setClocks = new ArrayList<>();
    private VBox widget;
    private FlowPane flowPane;
    private Label nameLabel;
    private String styleId;
    private EventHandler<ActionEvent> onClockListChange;

    public SetClockGroup(String name, SetClock ...clocks){
        this.name = name;
        for (SetClock clock : clocks){
            add(clock);
        }
    }

    public void add(SetClock setClock){
        setClocks.add(setClock);
        if(widget != null) updateWidget();
        if(onClockListChange != null) onClockListChange.handle(new ActionEvent());
    }

    public void remove(SetClock setClock){
        setClocks.remove(setClock);
        if(widget != null) updateWidget();
        if(onClockListChange != null) onClockListChange.handle(new ActionEvent());
    }

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

    public int indexOf(SetClock setClock){
        return setClocks.indexOf(setClock);
    }

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

    public VBox getWidget(){
        if(widget == null) createWidget();
        updateWidget();
        return widget;
    }

    public List<SetClock> getSetClocks() {
        return setClocks;
    }

    public void setStyleId(String id){
        this.styleId = id;
    }

    public void setOnClockListChange(EventHandler<ActionEvent> onClockListChange) {
        this.onClockListChange = onClockListChange;
    }

    public void removeWidgetFrom(Pane pane){
        pane.getChildren().remove(this.widget);
    }

    public void addWidgetTo(Pane pane){
        pane.getChildren().add(getWidget());
    }

    public boolean isInsideOf(Pane pane){
        if(this.widget == null) return false;
        return pane.getChildren().contains(this.widget);
    }

    private void createWidget(){
        widget = new VBox();
        widget.getStyleClass().add("groupBox");

        widget.setStyle("-fx-background-color:  rgb("+getRandomNumber(180, 240)+","
                +getRandomNumber(180, 240)+","
                +getRandomNumber(180, 240)+")"); // TODO: 17.10.2021 random color

        HBox hBox = new HBox();
        hBox.getStyleClass().add("groupControlsBox");
        widget.getChildren().add(hBox);

        nameLabel = new Label();
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
            for (SetClock setClock : setClocks){
                setClock.set(false);
            }
        });

        flowPane = new FlowPane();
        flowPane.getStyleClass().add("clockFlowPane");
        widget.getChildren().add(flowPane);
    }

    public int size(){
        return setClocks.size();
    }

    public void sortClocks(Comparator<SetClock> comparator){
        List<SetClock> prevClocks = new ArrayList<>(setClocks);
        this.setClocks.sort(comparator);
        for (int i=0; i < setClocks.size(); i++){
            if(!setClocks.get(i).equals(prevClocks.get(i)))
                setClocks.get(i).setIndicateEffect(300);
        }
        updateWidget();
    }

    @Override
    public String toString() {
        return name;
    }

    private int getRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

}
