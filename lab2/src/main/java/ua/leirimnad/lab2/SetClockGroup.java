package ua.leirimnad.lab2;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

public class SetClockGroup {
    private String name;
    private List<SetClock> setClocks = new ArrayList<>();
    private VBox widget;
    private FlowPane flowPane;
    private Label nameLabel;
    private String styleId;

    public SetClockGroup(String name, SetClock ...clocks){
        this.name = name;
        for (SetClock clock : clocks){
            add(clock);
        }
    }

    public void add(SetClock setClock){
        setClocks.add(setClock);
        if(widget != null) updateWidget();
    }

    public void remove(SetClock setClock){
        setClocks.remove(setClock);
        if(widget != null) updateWidget();
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
    }

    public int indexOf(SetClock setClock){
        return setClocks.indexOf(setClock);
    }

    public void updateWidget(){
        if(widget == null) return;
        flowPane.getChildren().clear();
        for (int i = 0; i < setClocks.size(); i++) {

            SetClock clock = setClocks.get(i);
            flowPane.getChildren().add(clock.getWidget());
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
        // widget.setStyle("-fx-background-color:  #cff0ff"); // TODO: 17.10.2021 random color

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
}