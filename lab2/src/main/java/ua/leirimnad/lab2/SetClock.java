package ua.leirimnad.lab2;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.Serializable;
import java.time.Instant;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.WindowEvent;


public abstract class SetClock {
    protected Instant timeCreated;
    private SetClockGroup group;
    protected AnchorPane widget;
    protected Label timeLabel;
    protected boolean wentOff = false;
    protected boolean set;
    private EventHandler<ActionEvent> onDelete;

    private String soundPath = "timerSound.mp3";
    private MediaPlayer soundPlayer;

    public SetClock(SetClockGroup group){
        this.timeCreated = Instant.now();
        this.group = group;
        createWidget();
        setupContextMenu();
        group.add(this);
    }

    public void delete(){
        set(false);
        this.group.remove(this);
        onDelete.handle(new ActionEvent());
    }

    public void setOnDelete(EventHandler<ActionEvent> handler) {
        onDelete = handler;
    }

    public void goOff(){
        if(wentOff) return;

        wentOff = true;

        timeLabel.getStyleClass().add("wentOffLabel");
        widget.getStyleClass().add("wentOffWidget");

        Media sound = new Media(TimerApplication.class.getResource(soundPath).toExternalForm());
        soundPlayer = new MediaPlayer(sound);
        soundPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        soundPlayer.play();
    }

    public void turnOff(){
        if(!wentOff) return;

        widget.getStyleClass().remove("wentOffWidget");
        timeLabel.getStyleClass().remove("wentOffLabel");
        soundPlayer.stop();
        wentOff = false;
    }

    public void changeGroup(SetClockGroup to){
        this.group.remove(this);
        to.add(this);
    }

    public SetClockGroup getGroup() {
        return group;
    }


    public AnchorPane getWidget(){
        if (widget == null) createWidget();
        updateWidget();
        return widget;
    }

    private void setupContextMenu(){
        final ContextMenu contextMenu = new ContextMenu();
        MenuItem it1 = new MenuItem("Видалити");
        contextMenu.getItems().addAll(it1);

        it1.setOnAction(event -> delete());

        widget.setOnMousePressed(event -> {
            if (event.isSecondaryButtonDown()) {
                contextMenu.show(widget, event.getScreenX(), event.getScreenY());
            }
        });
    }

    public boolean isSet(){
        return set;
    }

    protected abstract void updateWidget();
    protected abstract void createWidget();
    public abstract long getTotalDuration();
    public abstract long getTimeLeft();
    public abstract void set(boolean to);

}
