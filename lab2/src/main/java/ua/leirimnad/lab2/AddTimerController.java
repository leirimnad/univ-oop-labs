package ua.leirimnad.lab2;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;
import org.controlsfx.control.SegmentedButton;


public class AddTimerController {
    private InputTime inputTimer;
    private InputTime inputAlarm;
    private TimeZone alarmTimeZone;

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
    void initialize() {
        alarmTimeZone = TimeZone.getTimeZone(ZoneId.systemDefault());
        this.inputTimer = new InputTime(hourInput, minuteInput, secondInput, false);
        this.inputAlarm = new InputTime(alarmHourInput, alarmMinuteInput, null, true);


        int setHour = LocalDateTime.now().getHour();
        if (setHour == 23) setHour = 0;
        else setHour++;
        alarmHourInput.setText(Integer.toString(setHour));
        alarmMinuteInput.setText(Integer.toString(LocalDateTime.now().getMinute()));
        SegmentableButton toggleTimer = new SegmentableButton("таймер");
        toggleTimer.fire();
        SegmentableButton toggleAlarm = new SegmentableButton("будильник");
        segmentedButtonField.getButtons().addAll(toggleTimer, toggleAlarm);
        toggleTimer.setOnAction(e->{
            timerPane.setVisible(true);
            alarmPane.setVisible(false);
        });
        toggleAlarm.setOnAction(e->{
            timerPane.setVisible(false);
            alarmPane.setVisible(true);
        });

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
                    timezoneLabel.setText("Часова зона " + alarmTimeZone.getDisplayName(Locale.ENGLISH));
                });
            } catch (java.io.IOException er) {
                er.printStackTrace();
            }
        });

    }

    private void updateEndTime(){
        long secTimer = this.inputTimer.totalSeconds();
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedTime( FormatStyle.MEDIUM )
                .withZone( ZoneId.systemDefault() );
        Instant time = Instant.now().plusSeconds(secTimer);
        expectedEndTime.setText(formatter.format(time));

        // TODO: 15.10.2021 change secAlarm to give an amount of time to wait 
        long secAlarm = this.inputAlarm.totalSeconds();
        String timeLeft;
        if(secAlarm < 60) timeLeft = secAlarm + " сек";
        else if(secAlarm/60 < 60) timeLeft = secAlarm/60 + " хв";
        else timeLeft = secAlarm/3600 + " год";
        expectedDuration.setText(timeLeft);
    }

    private class InputTime {
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

        public long totalSeconds(){
            return Integer.parseInt(h.getText())* 3600L +
                    Integer.parseInt(m.getText())* 60L +
                    (s != null ? Integer.parseInt(secondInput.getText()) : 0);
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
}

