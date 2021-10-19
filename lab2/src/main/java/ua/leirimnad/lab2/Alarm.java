package ua.leirimnad.lab2;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.ToggleSwitch;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;


/**
 * Class representing an alarms which goes off at a certain time.
 */
public class Alarm extends SetClock implements Serializable {
    private final int h, m;
    private final TimeZone timeZone;
    private Instant setTo, setAt;

    private VBox timeBox;
    private Label restLabel, timezoneLabel;
    private ToggleSwitch toggleSwitch;

    /**
     * Creates an alarm with time from 00:00 to 23:59
     * @param h hour
     * @param m minute
     * @param group group to be added to
     */
    public Alarm(int h, int m, SetClockGroup group) {
        this(h, m, group, null);
    }

    /**
     * Creates an alarm with time from 00:00 to 23:59 in the specified timezone
     * @param h hour
     * @param m minute
     * @param group group to be added to
     * @param tz timezone of an alarm
     */
    public Alarm(int h, int m, SetClockGroup group, TimeZone tz) {
        super(group);

        if(h < 0 || h > 23) throw new IllegalArgumentException();
        if(m < 0 || m > 59) throw new IllegalArgumentException();

        this.h = h;
        this.m = m;
        this.timeZone = tz;

        set(false);
        updateWidget();
    }

    /**
     * Sets or unsets an alarm.
     * @param to true for setting, false for unsetting
     */
    @Override
    public void set(boolean to){
        if(set == to) return;
        set = to;

        if(to){
            setTo = nextInstant(h, m, timeZone);
            setAt = Instant.now();
        } else {
            setAt = null;
            if(wentOff) turnOff();
            if(onUnset != null) onUnset.handle(new ActionEvent());
        }

        if(toggleSwitch != null) toggleSwitch.setSelected(to);
        updateWidget();
    }


    /**
     * Returns an amount of seconds to be passed for this clock to go off, if it was set.
     * @return seconds to wait
     */
    private long secondsUntilGoingOff(){
        if(!set) return Instant.now().until(nextInstant(h, m, timeZone), ChronoUnit.SECONDS);
        else return Instant.now().until(setTo, ChronoUnit.SECONDS);
    }

    /**
     * Returns an instant of the next moment when it is the selected time in the selected timezone
     * @param h hours
     * @param m minutes
     * @param tz timezone
     * @return Instant instance
     */
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


    /**
     * Returns an amount of seconds to pass until {@link #nextInstant(int, int, TimeZone) next instant} of this time.
     * @param h hours
     * @param m minutes
     * @param tz timezone
     * @return seconds
     */
    static public long secondsUntil(int h, int m, TimeZone tz){
        return ChronoUnit.SECONDS.between(new GregorianCalendar().toInstant(), nextInstant(h, m, tz));
    }


    /**
     * Does a tick logic for an alarm.
     * Checks if it has to go off and updates the widget.
     */
    @Override
    protected void doTick(){
        if (set && Instant.now().isAfter(setTo) && !wentOff){
            goOff();
        }

        updateWidget();
    }

    /**
     * Updates the alarm's widget.
     * Sets time label, time left label, timezone label (if timezone is not local).
     */
    @Override
    protected void updateWidget(){
        timeLabel.setText(getTimeString(h, m));
        if(hasNonlocalTimeZone()) {
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


    /**
     * Returns true if the alarm has a non-local timezone.
     * @return has non-local timezone
     */
    private boolean hasNonlocalTimeZone(){
        return timeZone != null && !timeZone.equals(TimeZone.getTimeZone(ZoneId.systemDefault()));
    }

    /**
     * Returns a string representing time in 24-hour format.<br>
     * Example:
     * <ul>
     *     <li><i>(12, 0)</i> => <i>12:00</i></li>
     *     <li><i>(2, 23)</i> => <i>02:23</i></li>
     *     <li><i>(9, 8)</i> => <i>09:08</i></li>
     * </ul>
     * @param h hours
     * @param m minutes
     * @return string representation
     */
    static public String getTimeString(int h, int m){
        if(h < 0 || h > 23) throw new IllegalArgumentException();
        if(m < 0 || m > 59) throw new IllegalArgumentException();

        return (h < 10 ? "0" : "") + h + ":" + (m < 10 ? "0" : "") + m;
    }


    /**
     * Returns a string representing time left.<br>
     *     Example:
     *     <ul>
     *         <li><i>45</i> => <i>45 сек</i></li>
     *         <li><i>91</i> => <i>1 хв</i></li>
     *         <li><i>3599</i> => <i>59 хв</i></li>
     *         <li><i>3600</i> => <i>1 год</i></li>
     *         <li><i>144012</i> => <i>40 год</i></li>
     *     </ul>
     * @param sec seconds
     * @return string representation
     */
    static public String getEndDurationString(long sec){
        String timeLeft;
        if(sec < 60) timeLeft = sec + " сек";
        else if(sec/60 < 60) timeLeft = sec/60 + " хв";
        else timeLeft = sec/3600 + " год";
        return timeLeft;
    }

    /**
     * Creates a widget for an alarm.
     */
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
        timezoneLabel.getStyleClass().add("timeZoneLabel");
        timezoneLabel.setVisible(false);
        timezoneLabel.setLayoutX(14);
        timezoneLabel.setLayoutY(12);
        widget.getChildren().add(timezoneLabel);

        if(hasNonlocalTimeZone()){
            timezoneLabel.setText(timeZone.getDisplayName(Locale.ENGLISH));
            timezoneLabel.setVisible(true);
        }

        timeLabel = new Label();
        timeLabel.getStyleClass().add("alarmTimeLabel");
        timeLabel.setText(getTimeString(h, m));
        timeBox.getChildren().add(timeLabel);

        restLabel = new Label();
        restLabel.getStyleClass().add("alarmRestLabel");
        timeBox.getChildren().add(restLabel);

    }

    /**
     * Shows user a notification with details about the alarm.
     */
    @Override
    protected void showNotification() {
        String text = "Спрацював будильник на "+Alarm.getTimeString(h, m);
        if(hasNonlocalTimeZone())
            text += "\n(" + timeZone.getDisplayName(Locale.ENGLISH) + ")";
        Notifications.create()
                .title("Будильник")
                .text(text)
                .showWarning();
    }

    /**
     * Returns an instant when an alarm will go off if it was on.
     * @return instant of potential going off time
     */
    public Instant nextTrigger(){
        return nextInstant(h, m, timeZone);
    }

    /**
     * Returns amount of milliseconds to wait until alarm goes off, even if it is not set.
     * @return milliseconds
     */
    @Override
    public long getTotalDuration() {
        if(!set) return Instant.now().until(nextTrigger(), ChronoUnit.MILLIS);
        return setAt.until(setTo, ChronoUnit.MILLIS);
    }

    /**
     * Returns amount of milliseconds to wait until alarm goes off, or -1 if it is not set.
     * @return milliseconds or -1
     */
    @Override
    public long getTimeLeft() {
        if(!set) return -1;
        return Instant.now().until(setTo, ChronoUnit.MILLIS);
    }


    /**
     * Returns a string representation of the alarm.
     * @return string representation
     */
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
