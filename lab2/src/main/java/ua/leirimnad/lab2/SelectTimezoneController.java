package ua.leirimnad.lab2;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

/**
 * A controller for a window in which user chooses a timezone.
 */
public class SelectTimezoneController {
    @FXML
    private Slider zoneSlider;

    @FXML
    private Label timezoneId, timezoneName;

    @FXML
    private Button submitButton;

    @FXML
    void initialize() {
        setTimezoneNames();
        zoneSlider.setOnMouseReleased(e->setTimezoneNames());
        submitButton.setOnMouseClicked(e->{

            // When submit button is clicked, transfer the timezone to the previous window
            int hourOffset = (int) Math.round(zoneSlider.getValue());
            Stage window = (Stage) submitButton.getScene().getWindow();
            window.setUserData(TimeZone.getTimeZone("GMT"+(hourOffset >= 0 ? "+" : "")+hourOffset));
            window.getOnCloseRequest().handle(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
            window.close();

        });
    }

    /**
     * Sets timezoneName's text to contain cities of the chosen timezone.
     */
    private void setTimezoneNames(){
        int hourOffset = (int) Math.round(zoneSlider.getValue());

        List<String> names = new ArrayList<>(List.of(TimeZone.getAvailableIDs(hourOffset * 3600000)));
        List<String> filteredNames = new ArrayList<>();

        // Deletes all the system names
        for (String name : names){
            name = name.replaceAll("_", " ");
            if(name.lastIndexOf("/") != -1) name = name.substring(name.lastIndexOf("/") + 1);
            if (name.length() > 3 && !name.toUpperCase().equals(name)) filteredNames.add(name);
        }

        Collections.shuffle(filteredNames);
        timezoneName.setText(String.join(", ", filteredNames.subList(0, Math.min(4, filteredNames.size()))));
        String t = "";
        if (hourOffset != 0) {
            if (hourOffset > 0) t += "+";
            t += hourOffset;
        }
        timezoneId.setText("UTC"+t);
    }


    /**
     * Sets the default timezone to timezone that was chosen previously.
     * @param timeZone timezone that was chosen previously
     */
    public void initData(TimeZone timeZone){
        Stage window = (Stage) zoneSlider.getScene().getWindow();
        window.setUserData(timeZone);
        zoneSlider.setValue(timeZone.getOffset(Instant.now().toEpochMilli())/3600000.0);
        setTimezoneNames();
    }

}
