package ua.leirimnad.lab2;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import org.controlsfx.control.SegmentedButton;


public class AddTimerController {
    private InputTime inputTimer;
    private InputTime inputAlarm;
    private TimeZone alarmTimeZone = null;
    private InputTime selectedInput;
    private List<SetClockGroup> availableGroups;
    private SetClockGroup selectedGroup;
    private SetClockGroup initialGroup;

    @FXML
    private TextField alarmHourInput;

    @FXML
    private TextField alarmMinuteInput;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private SegmentedButton segmentedButtonField;

    @FXML
    private TextField hourInput;

    @FXML
    private TextField minuteInput;

    @FXML
    private TextField secondInput;

    @FXML
    private Label expectedEndTime, expectedDuration;

    @FXML
    private AnchorPane timerPane, alarmPane;

    @FXML
    private Label timezoneLabel;

    @FXML
    private Button startButton, addButton;

    @FXML
    private ComboBox<SetClockGroup> groupChooseBox;

    @FXML
    private AnchorPane rootPane;

    @FXML
    void initialize() {
        alarmTimeZone = TimeZone.getTimeZone(ZoneId.systemDefault());
        this.inputTimer = new InputTime(hourInput, minuteInput, secondInput, false);
        this.inputAlarm = new InputTime(alarmHourInput, alarmMinuteInput, null, true);


        SegmentableButton toggleTimer = new SegmentableButton("таймер");
        SegmentableButton toggleAlarm = new SegmentableButton("будильник");
        segmentedButtonField.getButtons().addAll(toggleTimer, toggleAlarm);

        toggleTimer.setOnAction(e->{
            timerPane.setVisible(true);
            alarmPane.setVisible(false);
            selectedInput = inputTimer;
        });
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

        Timeline updateEndTime = new Timeline( new KeyFrame(Duration.seconds(1), event -> updateEndTime()));
        updateEndTime.setCycleCount(Timeline.INDEFINITE);
        updateEndTime.play();

        timezoneLabel.setOnMouseClicked(e->{
            try{
                FXMLLoader loader = new FXMLLoader(TimerApplication.class.getResource("selectTimezone-view.fxml"));
                Parent selectTimezoneRoot = loader.load();
                Stage selectTimezoneStage = new Stage();
                selectTimezoneStage.setTitle("Вибрати часову зону");
                selectTimezoneStage.setScene(new Scene(selectTimezoneRoot));
                selectTimezoneStage.setResizable(false);
                SelectTimezoneController controller = loader.getController();
                controller.initData(alarmTimeZone);
                selectTimezoneStage.show();
                selectTimezoneStage.setOnCloseRequest(ev->{
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

    private void updateEndTime(){
        if(this.inputTimer.totalSeconds() > 0)
            expectedEndTime.setText(Timer.getEndTimeString(this.inputTimer.totalSeconds(), FormatStyle.MEDIUM));
        else
            expectedEndTime.setText("iнший раз");

        expectedDuration.setText(Alarm.getEndDurationString(this.inputAlarm.secondsUntil()));
    }

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

    private void addSetClock(){
        addSetClock(true);
    }



    private class InputTime {
        private TimeZone tz = null;
        private TextField h, m, s;
        private boolean zeros;

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

        public void setTimeZone(TimeZone tz){
            this.tz = tz;
        }

        public long totalSeconds(){
            return Integer.parseInt(h.getText())* 3600L +
                    Integer.parseInt(m.getText())* 60L +
                    (s != null ? Integer.parseInt(secondInput.getText()) : 0);
        }

        public long secondsUntil(){
            return Alarm.secondsUntil(h(), m(), tz);
        }

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

        public int h() {
            return Integer.parseInt(this.h.getText());
        }

        public int m() {
            return Integer.parseInt(this.m.getText());
        }

        public int s() {
            if(s == null) return 0;
            return Integer.parseInt(this.s.getText());
        }


    }

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
    public void initData(List<SetClockGroup> groups, SetClockGroup initialGroup){
        availableGroups = groups;
        this.initialGroup = selectedGroup = initialGroup;
        groupChooseBox.setItems(FXCollections.observableArrayList(availableGroups));
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

