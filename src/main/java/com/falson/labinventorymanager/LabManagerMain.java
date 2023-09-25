package com.falson.labinventorymanager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LabManagerMain extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LabManagerMain.class.getResource("Home-View.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 592, 581);
        stage.setTitle("Laboratory Inventory");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();


    }
}