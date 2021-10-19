package ua.leirimnad.lab2;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import org.controlsfx.control.SegmentedButton;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;


/**
 * A controller for window in which user is setting up a new clock.
 */
public class AddTimerController {
    private InputTime inputTimer, inputAlarm, selectedInput;
    private TimeZone alarmTimeZone = null;
    private List<SetClockGroup> availableGroups;
    private SetClockGroup selectedGroup;

    @FXML
    private TextField alarmHourInput, alarmMinuteInput;

    @FXML
    private SegmentedButton segmentedButtonField;

    @FXML
    private TextField hourInput, minuteInput, secondInput;

    @FXML
    private Label expectedEndTime, expectedDuration, timezoneLabel;

    @FXML
    private AnchorPane timerPane, alarmPane;

    @FXML
    private Button startButton, addButton;

    @FXML
    private ComboBox<SetClockGroup> groupChooseBox;

    @FXML
    void initialize() {
        alarmTimeZone = TimeZone.getTimeZone(ZoneId.systemDefault());
        // InputTime classes for Alarm and Timer are created
        this.inputTimer = new InputTime(hourInput, minuteInput, secondInput, false);
        this.inputAlarm = new InputTime(alarmHourInput, alarmMinuteInput, null, true);

        // Set up segmentable button
        SegmentableButton toggleTimer = new SegmentableButton("таймер");
        SegmentableButton toggleAlarm = new SegmentableButton("будильник");
        segmentedButtonField.getButtons().addAll(toggleTimer, toggleAlarm);

        // when timer button is pressed, show timer pane
        toggleTimer.setOnAction(e->{
            timerPane.setVisible(true);
            alarmPane.setVisible(false);
            selectedInput = inputTimer;
        });

        // when alarm button is pressed, show alarm pane
        toggleAlarm.setOnAction(e->{
            int setHour = LocalDateTime.now().getHour();
            if (setHour == 23) setHour = 0;
            else setHour++;
            alarmHourInput.setText(Integer.toString(setHour));
            alarmMinuteInput.setText(Integer.toString(LocalDateTime.now().getMinute()));

            timerPane.setVisible(false);
            alarmPane.setVisible(true);
            selectedInput = inputAlarm;
        });

        addButton.setOnMouseClicked(e->this.addSetClock(false));
        startButton.setOnMouseClicked(e->this.addSetClock(true));

        toggleTimer.fire();
        updateEndTime();

        // Timeline that updates end time every second
        Timeline updateEndTime = new Timeline( new KeyFrame(Duration.seconds(1), event -> updateEndTime()));
        updateEndTime.setCycleCount(Timeline.INDEFINITE);
        updateEndTime.play();

        timezoneLabel.setOnMouseClicked(e->{
            try{
                // Timezone window is created
                FXMLLoader loader = new FXMLLoader(TimerApplication.class.getResource("selectTimezone-view.fxml"));
                Parent selectTimezoneRoot = loader.load();
                Stage selectTimezoneStage = new Stage();
                selectTimezoneStage.setTitle("Вибрати часову зону");
                selectTimezoneStage.setScene(new Scene(selectTimezoneRoot));
                selectTimezoneStage.setResizable(false);
                selectTimezoneStage.getIcons().add(new Image(
                        Objects.requireNonNull(TimerApplication.class.getResourceAsStream("timeZoneIcon.png"))
                ));
                SelectTimezoneController controller = loader.getController();
                controller.initData(alarmTimeZone);
                selectTimezoneStage.show();
                selectTimezoneStage.setOnCloseRequest(ev->{
                    // When timezone window is closed, set the chosen timezone
                    this.alarmTimeZone = (TimeZone) selectTimezoneStage.getUserData();
                    this.inputAlarm.setTimeZone(alarmTimeZone);
                    timezoneLabel.setText("Часова зона " + alarmTimeZone.getDisplayName(Locale.ENGLISH));
                    updateEndTime();
                });
            } catch (java.io.IOException er) {
                er.printStackTrace();
            }
        });

    }

    /**
     * Updates end time label.
     */
    private void updateEndTime(){
        if(this.inputTimer.totalSeconds() > 0)
            expectedEndTime.setText(Timer.getEndTimeString(this.inputTimer.totalSeconds(), FormatStyle.MEDIUM));
        else
            expectedEndTime.setText("iнший раз");

        expectedDuration.setText(Alarm.getEndDurationString(this.inputAlarm.secondsUntil()));
    }

    /**
     * When user submits a clock, the new Clock instance is created, transferred to the main window, and this window is closed.
     * @param set will clock be set after adding
     */
    private void addSetClock(boolean set){
        Stage window = (Stage) startButton.getScene().getWindow();

        SetClock newSetClock;

        selectedGroup = groupChooseBox.getConverter().fromString(groupChooseBox.getEditor().getText());
        if (selectedGroup == null) {
            System.out.println("For some reason not a single group was selected");
            selectedGroup = availableGroups.get(0);
        }

        if(selectedInput == inputTimer){
            if (inputTimer.totalSeconds() == 0) return;
            newSetClock = new Timer(inputTimer.totalSeconds(), selectedGroup);
        }
        else if (selectedInput == inputAlarm) {
            newSetClock = new Alarm(inputAlarm.h(), inputAlarm.m(), selectedGroup, alarmTimeZone);
        }
        else newSetClock = null;

        if(newSetClock != null) newSetClock.set(set);

        window.setUserData(newSetClock);
        window.getOnCloseRequest().handle(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
        window.close();
    }


    /**
     * Class representing a widget user inputs time to.
     */
    private class InputTime {
        private TimeZone tz = null;
        private final TextField h;
        private final TextField m;
        private TextField s;
        private final boolean zeros;

        /**
         * Constructs an input of time.
         * @param h text field with hours
         * @param m text field with minutes
         * @param s text field with seconds
         * @param zeros if <code>true</code>, string representations will have zeros before one-digit values
         */
        public InputTime(TextField h, TextField m, TextField s, boolean zeros){
            this.zeros = zeros;
            addNumericInputControl(h, 23);
            addNumericInputControl(m, 59);
            if(s != null) addNumericInputControl(s, 59);
            this.h = h;
            this.m = m;
            if(s != null) this.s = s;
            h.textProperty().addListener((observable, oldValue, newValue) -> updateEndTime());
            m.textProperty().addListener((observable, oldValue, newValue) -> updateEndTime());
            if(s != null) s.textProperty().addListener((observable, oldValue, newValue) -> updateEndTime());
        }

        /**
         * Sets a timezone to an input
         * @param tz timezone
         */
        public void setTimeZone(TimeZone tz){
            this.tz = tz;
        }

        /**
         * @return total amount of seconds in current input
         */
        public long totalSeconds(){
            return Integer.parseInt(h.getText())* 3600L +
                    Integer.parseInt(m.getText())* 60L +
                    (s != null ? Integer.parseInt(secondInput.getText()) : 0);
        }

        /**
         * @return {@link Alarm#secondsUntil(int, int, TimeZone) seconds until}  currently entered time
         */
        public long secondsUntil(){
            return Alarm.secondsUntil(h(), m(), tz);
        }

        /**
         * Adds control to the text field, so that user couldn't write there anything but positive integers smaller than <code>max</code>.
         * @param textField node to add control to
         * @param max max value
         */
        private void addNumericInputControl(TextField textField, int max){
            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    newValue = newValue.replaceAll("[^\\d]", "");
                }
                if (newValue.isEmpty()) newValue = "0";
                if (newValue.matches("0+[0-9]+"))
                    newValue = (new IntegerStringConverter().fromString(newValue)).toString();
                if (newValue.matches("[1-9][0-9]*"))
                    while (Integer.parseInt(newValue) > max)
                        newValue = newValue.substring(0, newValue.length()-1);

                if (this.zeros && newValue.length() == 1) newValue = "0" + newValue;
                textField.setText(newValue);
            });
        }

        /**
         * @return hours in input
         */
        public int h() {
            return Integer.parseInt(this.h.getText());
        }

        /**
         * @return minutes in input
         */
        public int m() {
            return Integer.parseInt(this.m.getText());
        }

    }

    /**
     * Segmentable button that does not unset when clicked twice.
     */
    private static class SegmentableButton extends ToggleButton {
        @Override
        public void fire() {
            if (getToggleGroup() == null || !isSelected()) {
                super.fire();
            }
        }
        public SegmentableButton() {
            super();
        }
        public SegmentableButton(String text, Node graphic) {
            super(text, graphic);
        }
        public SegmentableButton(String text) {
            super(text);
        }
    }


    /**
     * Called by the main window to transfer the list of all the groups and initial group.
     * @param groups all the groups
     * @param initialGroup default group to add clocks to
     */
    public void initData(List<SetClockGroup> groups, SetClockGroup initialGroup){
        availableGroups = groups;
        SetClockGroup initialGroup1 = selectedGroup = initialGroup;
        groupChooseBox.setItems(FXCollections.observableArrayList(availableGroups));

        // Converter that converts Strings to Groups and backwards
        groupChooseBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(SetClockGroup object) {
                if(object == null) return "";
                return object.toString();
            }

            @Override
            public SetClockGroup fromString(String string) {
                string = string.trim();
                if (string.isEmpty())
                    return initialGroup;
                String finalString = string;
                return groupChooseBox.getItems().stream().filter(gr ->
                        gr.toString().equals(finalString)).findFirst().orElse(new SetClockGroup(string));
            }
        });


    }
}

