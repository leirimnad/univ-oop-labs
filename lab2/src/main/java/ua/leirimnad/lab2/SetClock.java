package ua.leirimnad.lab2;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.Effect;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;

import java.io.Serializable;
import java.time.Instant;
import java.util.Comparator;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;


public abstract class SetClock {
    protected Instant timeCreated;
    private SetClockGroup group;
    protected AnchorPane widget;
    protected Label timeLabel;
    protected boolean wentOff = false;
    protected boolean set;
    private EventHandler<ActionEvent> onDelete;
    private EventHandler<ActionEvent> onTick;
    protected EventHandler<ActionEvent> onUnset;
    private EventHandler<DragEvent> onDrag;
    private EventHandler<ActionEvent> onGoingOff;

    private String soundPath = "timerSound.mp3";
    private MediaPlayer soundPlayer;

    public SetClock(SetClockGroup group){
        this.timeCreated = Instant.now();
        this.group = group;
        createWidget();
        setupContextMenu();
        setupDragEvents();
        group.add(this);
        setIndicateEffect(300);
    }

    public void delete(){
        set(false);
        this.group.remove(this);
        if(onDelete != null) onDelete.handle(new ActionEvent());
    }

    public void setOnDelete(EventHandler<ActionEvent> handler) {
        onDelete = handler;
    }
    public void setOnUnset(EventHandler<ActionEvent> handler) {
        onUnset = handler;
    }
    public void setOnGoingOff(EventHandler<ActionEvent> handler) {
        onGoingOff = handler;
    }
    public void setOnTick(EventHandler<ActionEvent> handler) {
        onTick = handler;
    }
    public void setOnDrag(EventHandler<DragEvent> handler) {
        onDrag = handler;
    }


    public void goOff(){
        if(wentOff) return;

        wentOff = true;

        showNotification();

        timeLabel.getStyleClass().add("wentOffLabel");
        widget.getStyleClass().add("wentOffWidget");

        Media sound = new Media(TimerApplication.class.getResource(soundPath).toExternalForm());
        soundPlayer = new MediaPlayer(sound);
        soundPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        soundPlayer.play();

        if(onGoingOff != null) onGoingOff.handle(new ActionEvent());
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
        this.group = to;
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

    private void setupDragEvents(){
        widget.setOnDragDetected(e-> {
            Dragboard db = widget.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            content.putString("a");
            db.setContent(content);
            e.consume();
        });

        widget.setOnDragOver(e->{
            if (e.getGestureSource() != widget) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
            e.consume();
        });
        widget.setOnDragDropped(e->{
            if(onDrag != null) this.onDrag.handle(e);
            e.consume();
        });
    }

    public void tick(){
        doTick();
        if(onTick != null) onTick.handle(new ActionEvent());
    }

    public void setIndicateEffect(int ms){
        widget.setOpacity(0);
        Timeline timeline = new Timeline( new KeyFrame(Duration.millis(20), event -> {
            double opacity = widget.getOpacity()+20.0/ms;
            if(opacity > 1) opacity = 1;
            widget.setOpacity(opacity);
        }));
        timeline.setCycleCount(ms/20);
        timeline.play();
    }

    public boolean isSet(){
        return set;
    }

    protected abstract void updateWidget();
    protected abstract void createWidget();
    protected abstract void showNotification();
    protected abstract void doTick();
    public abstract long getTotalDuration();
    public abstract long getTimeLeft();
    public abstract void set(boolean to);


}
