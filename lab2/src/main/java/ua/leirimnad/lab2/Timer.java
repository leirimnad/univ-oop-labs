package ua.leirimnad.lab2;

import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.ToggleSwitch;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

public class Timer extends SetClock{

    public Timer(long seconds) {
        super(Instant.now().plusSeconds(seconds));
    }

    AnchorPane createWidget(){
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getStyleClass().add("timerPane");
        anchorPane.setPrefWidth(250);

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setLayoutX(15);
        vBox.setLayoutY(0);
        vBox.setPrefWidth(140);
        vBox.setPrefHeight(125);
        anchorPane.getChildren().add(vBox);

        ToggleSwitch toggleSwitch = new ToggleSwitch();

        return anchorPane;

    }
}
