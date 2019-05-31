package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.network.Protocol;
import it.polimi.ingsw.network.client.Gui;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.*;
import javafx.stage.Stage;

import java.awt.*;
import java.util.List;

public class LoginWindow extends GridPane {

    private static final String ERROR_STYLE = "-fx-font: normal bold 12px 'sans-serif'; -fx-text-fill: #FF0000;";
    private static final String BUTTON_STYLE = "-fx-background-color: #222222; -fx-text-fill: white;";
    static ComboBox<String> comboBox;

    public LoginWindow(Stage stage, Gui gui, String message, List<String> answers, String defaultOption, boolean isAnswerRequired) {
        Text text = new Text(message);
        DropShadow shadow = new DropShadow();
        Text wait = new Text("Attendi...");
        TextField textField = new TextField();
        Button buttonNext = new Button("Next");
        Button buttonQuit = new Button("Quit");

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth() / 2;
        int height = gd.getDisplayMode().getHeight() / 2;
        double scaleFactor = (double) width / 960; //1 for a 1920x1080

        shadow.setSpread(0.6);
        shadow.setColor(Color.BLACK);
        text.setTextAlignment(TextAlignment.CENTER);
        text.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.ITALIC, 12 * scaleFactor));
        text.setFill(Color.WHITE);
        text.setEffect(shadow);

        Platform.setImplicitExit(false);

        comboBox = new ComboBox<>();
        setHalignment(comboBox, HPos.CENTER);

        wait.setStyle(ERROR_STYLE);
        wait.setVisible(false);

        buttonNext.setStyle(BUTTON_STYLE);
        buttonNext.setMinWidth(100 * scaleFactor);
        buttonQuit.setStyle(BUTTON_STYLE);
        buttonQuit.setMinWidth(100 * scaleFactor);

        setMinSize(width, height);

        setPadding(new Insets(10 * scaleFactor, 10 * scaleFactor, 10 * scaleFactor, 10 * scaleFactor));

        setVgap(10 * scaleFactor);
        setHgap(100 * scaleFactor);

        add(text, 0, 0);
        if (isAnswerRequired) {
            if (answers == null)
                add(textField, 0, 1);
            else {
                comboBox.getItems().addAll(answers);
                comboBox.setValue(defaultOption);
                add(comboBox, 0, 1);
            }
        }
        add(wait, 0, 2);
        add(buttonNext, 0, 3);
        add(buttonQuit, 0, 4);

        buttonNext.setOnAction(e -> {
            if (!isAnswerRequired)
                gui.setAnswer(Protocol.ack);
            else if (answers == null)
                gui.setAnswer(textField.getText());
            else
                gui.setAnswer(comboBox.getValue());
            gui.setInputReady(true);
            comboBox.setVisible(false);
            textField.setVisible(false);
            buttonNext.setVisible(false);
            wait.setVisible(true);
        });

        buttonQuit.setOnAction(e -> System.exit(1));

        setAlignment(Pos.CENTER);
        setHalignment(text, HPos.CENTER);
        setHalignment(buttonNext, HPos.CENTER);
        setHalignment(buttonQuit, HPos.CENTER);

        setStyle("-fx-background-image: url('file:src/resources/images/other/loginscreen.jpg')");
    }

    public void setMessage(String string) {
        //message = string;
    }
}
