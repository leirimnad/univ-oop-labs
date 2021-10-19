package ua.leirimnad.lab2;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;


/**
 * Class representing a timer that goes off after a certain time.
 */
public class Timer extends SetClock {
    private boolean paused;
    private final long totalDuration;
    private long currentDuration;

    private Label restLabel;
    private Timeline timeline;
    private Button playButton, stopButton, pauseButton;
    private VBox timeBox;


    /**
     * Creates a timer with set amount of seconds and a group.
     * @param seconds timer's total duration
     * @param group timer's group
     */
    public Timer(long seconds, SetClockGroup group) {
        super(group);
        totalDuration = currentDuration = seconds;
        set(false);
        updateWidget();
    }

    /**
     * Sets or unsets a timer.
     * @param to true for setting, false for unsetting
     */
    @Override
    public void set(boolean to) {
        if(set == to) return;
        set = to;
        paused = false;

        if(to) {
            if(pauseButton != null) pauseButton.setDisable(false);
            currentDuration = totalDuration;
            timeBox.getChildren().add(restLabel);

            timeline = new Timeline( new KeyFrame(Duration.seconds(1), event -> tick()));
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();
        } else {
            if(timeLabel != null) timeLabel.setTextFill(Color.BLACK);
            currentDuration = totalDuration;
            timeBox.getChildren().remove(restLabel);
            if (timeline != null) timeline.stop();
            if(wentOff) turnOff();
            updateWidget();
            if(onUnset != null) onUnset.handle(new ActionEvent());
        }

        if(playButton != null) playButton.setVisible(!to);
        if(pauseButton != null) pauseButton.setVisible(to);
        if(stopButton != null) stopButton.setVisible(to);
        if(restLabel != null) restLabel.setVisible(to);
        updateWidget();
    }


    /**
     * Pauses or unpauses a timer. While paused, timer is not subtracting seconds from it's time left.
     * @param to <code>true</code> for pause, <code>false</code> for unpause
     */
    public void pause(boolean to){
        paused = to;
        if(to){
            timeline.pause();
            pauseButton.setText(playButton.getText());
        } else {
            timeline.play();
            pauseButton.setText("⏸");
        }
        updateWidget();
    }

    /**
     * Returns time that left, in milliseconds.
     * @return milliseconds left
     */
    @Override
    public long getTimeLeft(){
        return currentDuration*1000;
    }

    /**
     * Returns total timer time, in milliseconds.
     * @return milliseconds total
     */
    @Override
    public long getTotalDuration() {
        return totalDuration*1000;
    }


    /**
     * Returns a string representing time after sec in 24-hour format.
     * @param sec seconds
     * @param formatStyle short, medium, long or full
     * @return string representation
     */
    public static String getEndTimeString(long sec, FormatStyle formatStyle){
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedTime( formatStyle )
                .withZone( ZoneId.systemDefault() );
        Instant time = Instant.now().plusSeconds(sec);
        return formatter.format(time);
    }

    /**
     * Returns a string representing duration of sec <code>[h:]m:s</code> format.
     * @param sec duration in seconds
     * @return string representation
     */
    public static String getDurationString(long sec){
        String res = "";
        long hours = sec / 3600;
        long minutes = (sec - hours * 3600) / 60;
        long seconds = sec - hours * 3600 - minutes * 60;

        if (hours > 0){
            res += hours + ":";
            if (minutes < 10) res += "0";
        }
        res += minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
        return res;
    }

    /**
     * Subtracts a second from timer and checks if it has to go off.
     * For code use {@link #tick()}.
     */
    @Override
    protected void doTick(){
        if(currentDuration > 0) currentDuration -= 1;

        if (!wentOff && currentDuration == 0){
            pauseButton.setDisable(true);
            goOff();
        }

        updateWidget();
    }

    /**
     * Updates time and time left labels of a widget.
     */
    @Override
    protected void updateWidget(){
        timeLabel.setText(getDurationString(currentDuration));
        if(!paused)
            restLabel.setText("кiнець в "+Timer.getEndTimeString(currentDuration, FormatStyle.SHORT));
        else
            restLabel.setText("призупинено");
        if (currentDuration == 0)
            restLabel.setText("час сплинув");

    }

    /**
     * Creates a widget for timer.
     */
    @Override
    protected void createWidget(){
        if(widget != null) return;
        widget = new AnchorPane();
        widget.getStyleClass().add("alarmPane");
        widget.setMinWidth(250);

        timeBox = new VBox();
        timeBox.setAlignment(Pos.CENTER);
        timeBox.setLayoutX(15);
        timeBox.setLayoutY(0);
        timeBox.setPrefWidth(140);
        timeBox.setPrefHeight(125);
        timeBox.setFillWidth(true);
        widget.getChildren().add(timeBox);

        pauseButton = new Button("⏸");
        pauseButton.getStyleClass().addAll("pauseTimerButton", "timerControlButton");
        pauseButton.setLayoutX(186);
        pauseButton.setLayoutY(32);
        pauseButton.setOnMouseClicked(e->pause(!paused));
        widget.getChildren().add(pauseButton);

        stopButton = new Button("⏹");
        stopButton.getStyleClass().addAll("stopTimerButton", "timerControlButton");
        stopButton.setLayoutX(186);
        stopButton.setLayoutY(63);
        stopButton.setOnMouseClicked(e->{
            if(set) set(false);
        });
        widget.getChildren().add(stopButton);

        playButton = new Button("⏵");
        playButton.getStyleClass().addAll("startTimerButton", "timerControlButton");
        playButton.setLayoutX(186);
        playButton.setLayoutY(32);
        playButton.setOnMouseClicked(e->{
            if(!set) set(true);
        });
        widget.getChildren().add(playButton);

        timeLabel = new Label();
        timeLabel.setAlignment(Pos.CENTER);
        timeBox.getChildren().add(timeLabel);
        timeLabel.textProperty().addListener((observable, oldValue, newValue)->{
            if (timeLabel.getText().length() > 5) {
                timeLabel.setStyle("-fx-font: italic 35 'Segoe UI';");
            } else {
                timeLabel.setStyle("-fx-font: italic 49 'Segoe UI';");
            }
        });

        restLabel = new Label();
        restLabel.setFont(Font.font("Segoe UI", 16));
        restLabel.setTextFill(Color.web("#6f6f6f"));
        restLabel.setAlignment(Pos.CENTER);
        restLabel.setPrefSize(130, 15);
        restLabel.setPadding(new Insets(-7, 0, 0, 0));
    }

    /**
     * Shows user a notification with details about the timer.
     */
    @Override
    protected void showNotification() {
        Notifications.create()
                .title("Таймер")
                .text("Спрацював таймер\nУсього часу: "+Timer.getDurationString(totalDuration))
                .showWarning();
    }


}
