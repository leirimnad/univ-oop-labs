package ua.leirimnad.lab2;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import org.controlsfx.control.ToggleSwitch;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;


public class Alarm extends SetClock{
    private TimeZone timeZone;
    private int h, m;
    private Instant setTo;
    private boolean set, wentOff;
    private Label timeLabel, restLabel;
    private ToggleSwitch toggleSwitch;

    public Alarm(int h, int m) {
        this(h, m, null);
    }

    public Alarm(int h, int m, TimeZone tz) {
        super();
        this.h = h;
        this.m = m;
        this.timeZone = tz;
        widget = createWidget();
        set(false);
    }

    @Override
    public void set(boolean to){
        set = to;
        wentOff = false;

        if(to){
            setTo = nextInstant(h, m, timeZone);
        }

        toggleSwitch.setSelected(to);
        timeLabel.setTextFill(Color.BLACK);
        updateWidget();
    }

    public void set(){
        set(true);
    }

    private void goOff(){
        wentOff = true;
        timeLabel.setTextFill(Color.RED);
    }

    private long secondsUntilGoingOff(){
        if(!set) return Instant.now().until(nextInstant(h, m, timeZone), ChronoUnit.SECONDS);
        else return Instant.now().until(setTo, ChronoUnit.SECONDS);
    }

    static public Instant nextInstant(int h, int m, TimeZone tz){
        Calendar calendar;
        if(tz != null) calendar = new GregorianCalendar(tz);
        else calendar = new GregorianCalendar();
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, m);
        calendar.set(Calendar.HOUR_OF_DAY, h);

        if(calendar.toInstant().isBefore(Instant.now())){
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return calendar.toInstant();
    }

    static public long secondsUntil(int h, int m, TimeZone tz){
        return ChronoUnit.SECONDS.between(new GregorianCalendar().toInstant(), nextInstant(h, m, tz));
    }

    static public long secondsUntil(int h, int m){
        return secondsUntil(h, m, null);
    }

    public void tick(){
        if (set && Instant.now().isAfter(setTo)){
            goOff();
        }

        updateWidget();
    }

    public void updateWidget(){
        if (!wentOff)
            restLabel.setText("через "+getEndDurationString(secondsUntilGoingOff()));
        if (wentOff)
            restLabel.setText("зараз");

    }

    static public String getTimeString(int h, int m){
        return (h < 10 ? "0" : "") + h + ":" + (m < 10 ? "0" : "") + m;
    }

    static public String getEndDurationString(long sec){
        String timeLeft;
        if(sec < 60) timeLeft = sec + " сек";
        else if(sec/60 < 60) timeLeft = sec/60 + " хв";
        else timeLeft = sec/3600 + " год";
        return timeLeft;
    }

    public AnchorPane getWidget(){
        return widget;
    }

    public AnchorPane createWidget(){
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getStyleClass().add("timerPane");
        anchorPane.setMinWidth(250);

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setLayoutX(15);
        vBox.setLayoutY(0);
        vBox.setPrefWidth(140);
        vBox.setPrefHeight(125);
        anchorPane.getChildren().add(vBox);

        toggleSwitch = new ToggleSwitch();
        toggleSwitch.setPrefSize(50, 25);
        toggleSwitch.setLayoutX(167);
        toggleSwitch.setLayoutY(45);
        toggleSwitch.setOnMouseClicked(e->{
            set(!set);
        });
        anchorPane.getChildren().add(toggleSwitch);

        if(timeZone != null && !timeZone.equals(TimeZone.getTimeZone(ZoneId.systemDefault()))){
            Label timezoneLabel = new Label();
            timezoneLabel.setText(timeZone.getDisplayName(Locale.ENGLISH));
            timezoneLabel.setFont(Font.font("Segoe UI", 16));
            timezoneLabel.setTextFill(Color.web("#6f6f6f"));
            timezoneLabel.setAlignment(Pos.CENTER);
            timezoneLabel.setPadding(new Insets(0, 0, -7, 0));
            timezoneLabel.setPrefSize(130, 15);
            vBox.getChildren().add(timezoneLabel);
        }

        timeLabel = new Label();
        timeLabel.setText(getTimeString(h, m));
        timeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 52));
        timeLabel.setAlignment(Pos.CENTER);

        vBox.getChildren().add(timeLabel);

        restLabel = new Label();
        restLabel.setFont(Font.font("Segoe UI", 16));
        restLabel.setTextFill(Color.web("#6f6f6f"));
        restLabel.setAlignment(Pos.CENTER);
        restLabel.setPrefSize(130, 15);
        restLabel.setPadding(new Insets(-7, 0, 0, 0));
        vBox.getChildren().add(restLabel);

        return anchorPane;
    }
}
