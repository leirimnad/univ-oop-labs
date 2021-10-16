package ua.leirimnad.lab2;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import org.controlsfx.control.ToggleSwitch;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;


public class Timer extends SetClock{
    private boolean set, paused;
    private long totalDuration;
    private long currentDuration;
    private Instant timeSet;
    private Label durationLabel, restLabel;
    private Timeline timeline;
    private Button playButton, stopButton, pauseButton;
    private VBox timeBox;


    public Timer(int hours, int minutes, int seconds){
        this(hours * 3600L + minutes * 60L + seconds);
    }

    public Timer(long seconds) {
        super();
        totalDuration = currentDuration = seconds;
        this.widget = createWidget();
        set(false);
    }

    public void set(){
        set(true);
    }

    @Override
    public void set(boolean to) {
        set = to;
        paused = false;

        if(to) {
            pauseButton.setDisable(false);
            timeSet = Instant.now();
            currentDuration = totalDuration;
            timeBox.getChildren().add(restLabel);

            timeline = new Timeline( new KeyFrame(Duration.seconds(1), event -> secondTick()));
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();
        } else {
            durationLabel.setTextFill(Color.BLACK);
            timeSet = null;
            currentDuration = totalDuration;
            timeBox.getChildren().remove(restLabel);
            if (timeline != null) timeline.stop();
            updateWidget();
        }

        if(playButton != null) playButton.setVisible(!to);
        if(pauseButton != null) pauseButton.setVisible(to);
        if(stopButton != null) stopButton.setVisible(to);
        restLabel.setVisible(to);
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

    public long getTimeLeft(){
        return currentDuration;
    }

    public boolean hasGoneOff(){
        return currentDuration <= 0;
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

    private void secondTick(){
        if(currentDuration > 0) currentDuration -= 1;

        if (currentDuration == 0){
            durationLabel.setTextFill(Color.RED);
            pauseButton.setDisable(true);
        }

        updateWidget();
    }

    private void updateWidget(){
        durationLabel.setText(getDurationString(currentDuration));
        if(!paused)
            restLabel.setText("кiнець в "+Timer.getEndTimeString(currentDuration, FormatStyle.SHORT));
        else
            restLabel.setText("призупинено");
        if (currentDuration == 0)
            restLabel.setText("час сплинув");

    }

    @Override
    public AnchorPane getWidget() {
        return widget;
    }

    private AnchorPane createWidget(){
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getStyleClass().add("alarmPane");
        anchorPane.setMinWidth(250);

        timeBox = new VBox();
        timeBox.setAlignment(Pos.CENTER);
        timeBox.setLayoutX(15);
        timeBox.setLayoutY(0);
        timeBox.setPrefWidth(140);
        timeBox.setPrefHeight(125);
        timeBox.setFillWidth(true);
        anchorPane.getChildren().add(timeBox);

        pauseButton = new Button("⏸");
        pauseButton.setPrefSize(40, 30);
        pauseButton.setLayoutX(186);
        pauseButton.setLayoutY(32);
        pauseButton.setOnMouseClicked(e->pause(!paused));
        anchorPane.getChildren().add(pauseButton);

        stopButton = new Button("⏹");
        stopButton.setFont(Font.font("System", 9));
        stopButton.setPrefSize(40, 30);
        stopButton.setLayoutX(186);
        stopButton.setLayoutY(63);
        stopButton.setOnMouseClicked(e->{
            if(set) set(false);
        });
        anchorPane.getChildren().add(stopButton);

        playButton = new Button("⏵");
        playButton.setPadding(new Insets(-1, -1, -1, -1));
        playButton.setFont(Font.font("System", 20));
        playButton.setPrefSize(40, 62);
        playButton.setLayoutX(186);
        playButton.setLayoutY(32);
        playButton.setOnMouseClicked(e->{
            if(!set) set(true);
        });
        anchorPane.getChildren().add(playButton);

        durationLabel = new Label();
        durationLabel.setAlignment(Pos.CENTER);
        timeBox.getChildren().add(durationLabel);
        durationLabel.textProperty().addListener((observable, oldValue, newValue)->{
            if (durationLabel.getText().length() > 5) {
                durationLabel.setStyle("-fx-font: italic 35 'Segoe UI';");
            } else {
                durationLabel.setStyle("-fx-font: italic 49 'Segoe UI';");
            }
        });

        restLabel = new Label();
        restLabel.setFont(Font.font("Segoe UI", 16));
        restLabel.setTextFill(Color.web("#6f6f6f"));
        restLabel.setAlignment(Pos.CENTER);
        restLabel.setPrefSize(130, 15);
        restLabel.setPadding(new Insets(-7, 0, 0, 0));

        return anchorPane;
    }
}
