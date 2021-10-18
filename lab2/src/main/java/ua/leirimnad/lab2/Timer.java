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


public class Timer extends SetClock {
    private boolean paused;
    private long totalDuration;
    private long currentDuration;
    private Instant timeSet;
    private Label restLabel;
    private Timeline timeline;
    private Button playButton, stopButton, pauseButton;
    private VBox timeBox;


    public Timer(int hours, int minutes, int seconds, SetClockGroup group){
        this(hours * 3600L + minutes * 60L + seconds, group);
    }

    public Timer(long seconds, SetClockGroup group) {
        super(group);
        totalDuration = currentDuration = seconds;
        set(false);
        updateWidget();
    }

    public void set(){
        set(true);
    }

    @Override
    public void set(boolean to) {
        if(set == to) return;
        set = to;
        paused = false;

        if(to) {
            if(pauseButton != null) pauseButton.setDisable(false);
            timeSet = Instant.now();
            currentDuration = totalDuration;
            timeBox.getChildren().add(restLabel);

            timeline = new Timeline( new KeyFrame(Duration.seconds(1), event -> tick()));
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();
        } else {
            if(wentOff) turnOff();
            if(timeLabel != null) timeLabel.setTextFill(Color.BLACK);
            timeSet = null;
            currentDuration = totalDuration;
            timeBox.getChildren().remove(restLabel);
            if (timeline != null) timeline.stop();
            updateWidget();
            if(onUnset != null) onUnset.handle(new ActionEvent());
        }

        if(playButton != null) playButton.setVisible(!to);
        if(pauseButton != null) pauseButton.setVisible(to);
        if(stopButton != null) stopButton.setVisible(to);
        if(restLabel != null) restLabel.setVisible(to);
        updateWidget();
    }

    public void pause(){
        pause(true);
    }

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

    @Override
    public long getTimeLeft(){
        return currentDuration*1000;
    }

    @Override
    public long getTotalDuration() {
        return totalDuration*1000;
    }

    public boolean wentOff(){
        return wentOff;
    }

    public static String getEndTimeString(long sec, FormatStyle formatStyle){
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedTime( formatStyle )
                .withZone( ZoneId.systemDefault() );
        Instant time = Instant.now().plusSeconds(sec);
        return formatter.format(time);
    }

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

    @Override
    protected void doTick(){
        if(currentDuration > 0) currentDuration -= 1;

        if (!wentOff && currentDuration == 0){
            pauseButton.setDisable(true);
            goOff();
        }

        updateWidget();
    }

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
        pauseButton.setPrefSize(40, 30);
        pauseButton.setLayoutX(186);
        pauseButton.setLayoutY(32);
        pauseButton.setOnMouseClicked(e->pause(!paused));
        widget.getChildren().add(pauseButton);

        stopButton = new Button("⏹");
        stopButton.setFont(Font.font("System", 9));
        stopButton.setPrefSize(40, 30);
        stopButton.setLayoutX(186);
        stopButton.setLayoutY(63);
        stopButton.setOnMouseClicked(e->{
            if(set) set(false);
        });
        widget.getChildren().add(stopButton);

        playButton = new Button("⏵");
        playButton.setPadding(new Insets(-1, -1, -1, -1));
        playButton.setFont(Font.font("System", 20));
        playButton.setPrefSize(40, 62);
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

    @Override
    protected void showNotification() {
        Notifications.create()
                .title("Таймер")
                .text("Спрацював таймер\nУсього часу: "+Timer.getDurationString(totalDuration))
                .showWarning();
    }


}
