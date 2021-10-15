package ua.leirimnad.lab2;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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
        anchorPane.setMinWidth(250);

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setLayoutX(15);
        vBox.setLayoutY(0);
        vBox.setPrefWidth(140);
        vBox.setPrefHeight(125);
        anchorPane.getChildren().add(vBox);

        ToggleSwitch toggleSwitch = new ToggleSwitch();
        toggleSwitch.setPrefSize(50, 25);
        toggleSwitch.setLayoutX(167);
        toggleSwitch.setLayoutY(45);
        anchorPane.getChildren().add(toggleSwitch);

        Label timeLabel = new Label();
        timeLabel.setText("12:20");
        timeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 52));
        timeLabel.setAlignment(Pos.CENTER);
        timeLabel.setPadding(new Insets(0, 0, -7, 0));
        vBox.getChildren().add(timeLabel);

        Label restLabel = new Label();
        restLabel.setText("через 20 хв");
        restLabel.setFont(Font.font("Segoe UI", 16));
        restLabel.setTextFill(Color.web("#6f6f6f"));
        restLabel.setAlignment(Pos.CENTER);
        restLabel.setPrefSize(130, 15);
        vBox.getChildren().add(restLabel);

        return anchorPane;
    }
}
