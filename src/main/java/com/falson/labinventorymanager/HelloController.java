package com.falson.labinventorymanager;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class HelloController {
    @FXML
    private Label welcomeText;
    @FXML
    private Button buttonAddEntry;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
    @FXML
    private void onAddLocationButtonClick() {

    }
}