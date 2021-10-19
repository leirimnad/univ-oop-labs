package ua.leirimnad.lab2;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.time.Instant;

/**
 * Abstract class for a clock with widget, group, set, tick and going off function.
 */
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
    private EventHandler<ActionEvent> onTurnOff;

    private String soundPath = "timerSound.mp3";
    private MediaPlayer soundPlayer;

    /**
     * Constructs a SetClock, it's widget, and assigns in to a group
     * @param group group to be added to
     */
    public SetClock(SetClockGroup group){
        this.timeCreated = Instant.now();
        this.group = group;
        createWidget();
        setupContextMenu();
        setupDragEvents();
        group.add(this);
        setIndicateEffect(300);
    }

    /**
     * Deletes a SetClock, resetting it and deleting from group.
     */
    public void delete(){
        set(false);
        this.group.remove(this);
        if(onDelete != null) onDelete.handle(new ActionEvent());
    }

    /**
     * Sets a function to be called when clock is deleted
     * @param handler function to be called
     */
    public void setOnDelete(EventHandler<ActionEvent> handler) {
        onDelete = handler;
    }

    /**
     * Sets a function to be called when clock is set to "false".
     * Function is also called when clock is deleted.
     * @param handler function to be called
     */
    public void setOnUnset(EventHandler<ActionEvent> handler) {
        onUnset = handler;
    }

    /**
     * Sets a function to be called when clock goes off.
     * @param handler function to be called
     */
    public void setOnGoingOff(EventHandler<ActionEvent> handler) {
        onGoingOff = handler;
    }

    /**
     * Sets a function to be called when clock is unset after it went off.
     * @param handler function to be called
     */
    public void setOnTurnOff(EventHandler<ActionEvent> handler) {
        onTurnOff = handler;
    }

    /**
     * Sets a function to be called on clock tick.
     * For example, for timer this function is called every second if timer is set.
     * @param handler function to be called
     */
    public void setOnTick(EventHandler<ActionEvent> handler) {
        onTick = handler;
    }

    /**
     * Sets a function to be called when clock's widget was dragged.
     * @param handler function to be called
     */
    public void setOnDrag(EventHandler<DragEvent> handler) {
        onDrag = handler;
    }


    /**
     * Sets clock as went off.
     * Notifies the user.
     * Sets the styles that indicate that clock has gone off.
     * Plays the beeping sound.
     */
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

    /**
     * Function to be called when clock has gone off and the user wants to turn it off.
     */
    public void turnOff(){
        if(!wentOff) return;

        widget.getStyleClass().remove("wentOffWidget");
        timeLabel.getStyleClass().remove("wentOffLabel");
        soundPlayer.stop();
        wentOff = false;
        if(onTurnOff != null) {
            onTurnOff.handle(new ActionEvent());
        }
    }


    /**
     * Returns clock's group.
     * @return group instance
     */
    public SetClockGroup getGroup() {
        return group;
    }

    /**
     * Changes clock's group.
     * @param to new group
     */
    public void changeGroup(SetClockGroup to){
        this.group.remove(this);
        to.add(this);
        this.group = to;
    }


    /**
     * Returns a widget of a clock.
     * If clock doesn't have one, creates it.
     * @return clock's widget
     */
    public AnchorPane getWidget(){
        if (widget == null) createWidget();
        updateWidget();
        return widget;
    }


    /**
     * Setups clock's context menu with delete button.
     * Should be called once, during clock's creation.
     */
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


    /**
     * Setups clock's reaction on different drag events.
     * Should be called once, during clock's creation.
     */
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


    /**
     * Does the clock tick.
     * For timers, one second is subtracted from the time that's left.
     * For clock's, checks if the set time was reached.
     */
    public void tick(){
        doTick();
        if(onTick != null) onTick.handle(new ActionEvent());
    }


    /**
     * Sets an effect on clock's widget that is based on opacity change.
     * @param ms effect duration in milliseconds
     */
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

    /**
     * Returns true if the clock is set at the moment, else returns false.
     * @return is clock set
     */
    public boolean isSet(){
        return set;
    }

    /**
     * Sets or unsets a clock.
     * @param to true for setting, false for unsetting
     */
    public abstract void set(boolean to);

    /**
     * Updates clock's widget, changes all the labels.
     */
    protected abstract void updateWidget();

    /**
     * Creates a widget for the clock, based on clock's class.
     */
    protected abstract void createWidget();

    /**
     * Shows a notification for a user that clock has gone off.
     */
    protected abstract void showNotification();

    /**
     * Tick function that does all the logic for a tick, based on clock's class.
     */
    protected abstract void doTick();

    /**
     * Returns the total amount of time to wait for potential clock's going off,
     * considering that clock is not set at the moment.
     * @return seconds until potential going off
     */
    public abstract long getTotalDuration();

    /**
     * Returns the total amount of time to wait for potential clock's going off,
     * based on if clock is set.
     * @return seconds until potential going off
     */
    public abstract long getTimeLeft();



}
