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
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.ToggleSwitch;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;


public class Alarm extends SetClock implements Serializable {
    private TimeZone timeZone;
    private int h, m;
    private Instant setTo;
    private Instant setAt;
    private Label restLabel, timezoneLabel;
    private ToggleSwitch toggleSwitch;
    private VBox timeBox;

    public Alarm(int h, int m, SetClockGroup group) {
        this(h, m, group, null);
    }

    public Alarm(int h, int m, SetClockGroup group, TimeZone tz) {
        super(group);
        this.h = h;
        this.m = m;
        this.timeZone = tz;
        set(false);
        updateWidget();
    }

    @Override
    public void set(boolean to){
        if(set == to) return;
        set = to;

        if(to){
            setTo = nextInstant(h, m, timeZone);
            setAt = Instant.now();
        } else {
            if(wentOff) turnOff();
            setAt = null;
            if(onUnset != null) onUnset.handle(new ActionEvent());
        }

        if(toggleSwitch != null) toggleSwitch.setSelected(to);
        if(timeLabel != null) timeLabel.setTextFill(Color.BLACK);
        updateWidget();
    }

    public void set(){
        set(true);
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

    @Override
    protected void doTick(){
        if (set && Instant.now().isAfter(setTo) && !wentOff){
            goOff();
        }

        updateWidget();
    }

    @Override
    protected void updateWidget(){
        timeLabel.setText(getTimeString(h, m));
        if(!hasLocalTimeZone()) {
            timezoneLabel.setText(timeZone.getDisplayName(Locale.ENGLISH));
            timezoneLabel.setVisible(true);
        } else timeBox.getChildren().remove(timezoneLabel);
        if(restLabel != null) {
            if (!wentOff)
                restLabel.setText("через " + getEndDurationString(secondsUntilGoingOff()));
            if (wentOff)
                restLabel.setText("зараз");
        }
    }

    private boolean hasLocalTimeZone(){
        return timeZone == null || timeZone.equals(TimeZone.getTimeZone(ZoneId.systemDefault()));
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

    @Override
    protected void createWidget(){
        if(widget != null) return;
        widget = new AnchorPane();
        widget.getStyleClass().add("timerPane");
        widget.setMinWidth(250);

        timeBox = new VBox();
        timeBox.setAlignment(Pos.CENTER);
        timeBox.setLayoutX(15);
        timeBox.setLayoutY(0);
        timeBox.setPrefWidth(140);
        timeBox.setPrefHeight(125);
        widget.getChildren().add(timeBox);

        toggleSwitch = new ToggleSwitch();
        toggleSwitch.setPrefSize(50, 25);
        toggleSwitch.setLayoutX(167);
        toggleSwitch.setLayoutY(45);
        toggleSwitch.setOnMouseClicked(e->{
            set(!set);
        });
        widget.getChildren().add(toggleSwitch);

        timezoneLabel = new Label();
        timezoneLabel.setFont(Font.font("Segoe UI", 16));
        timezoneLabel.setTextFill(Color.web("#6f6f6f"));
        timezoneLabel.setAlignment(Pos.CENTER);
        timezoneLabel.setPrefSize(140, 15);
        timezoneLabel.setVisible(false);
        timeBox.setLayoutX(15);
        timeBox.setLayoutY(0);
        widget.getChildren().add(timezoneLabel);

        if(!hasLocalTimeZone()){
            timezoneLabel.setText(timeZone.getDisplayName(Locale.ENGLISH));
            timezoneLabel.setVisible(true);
        }

        timeLabel = new Label();
        timeLabel.setText(getTimeString(h, m));
        timeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 52));
        timeLabel.setAlignment(Pos.CENTER);
        timeBox.getChildren().add(timeLabel);

        restLabel = new Label();
        restLabel.setFont(Font.font("Segoe UI", 16));
        restLabel.setTextFill(Color.web("#6f6f6f"));
        restLabel.setAlignment(Pos.CENTER);
        restLabel.setPrefSize(130, 15);
        restLabel.setPadding(new Insets(-7, 0, 0, 0));
        timeBox.getChildren().add(restLabel);

    }

    @Override
    protected void showNotification() {
        String text = "Спрацював будильник на "+Alarm.getTimeString(h, m);
        if(!hasLocalTimeZone())
            text += "\n(" + timeZone.getDisplayName(Locale.ENGLISH) + ")";
        Notifications.create()
                .title("Будильник")
                .text(text)
                .showWarning();
    }

    public Instant nextTrigger(){
        return nextInstant(h, m, timeZone);
    }

    @Override
    public long getTotalDuration() {
        if(!set) return Instant.now().until(nextTrigger(), ChronoUnit.MILLIS);
        return setAt.until(setTo, ChronoUnit.MILLIS);
    }

    @Override
    public long getTimeLeft() {
        if(!set) return -1;
        return Instant.now().until(setTo, ChronoUnit.MILLIS);
    }

    @Override
    public String toString() {
        return "Alarm{" +
                "h=" + h +
                ", m=" + m +
                ", set=" + set +
                ", wentOff=" + wentOff +
                '}';
    }
}
