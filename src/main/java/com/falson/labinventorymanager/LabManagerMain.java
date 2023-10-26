package com.falson.labinventorymanager;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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
        HomeViewController instance = HomeViewController.getInstance();
        scene.widthProperty().addListener((obs, oldVal, newVal) -> Platform.runLater(() -> {
            instance.tableInstance.itemSummaryScrollPane.setFitToWidth(!(670 > instance.mainDynamicPanel.getWidth()));
        }));
        stage.maximizedProperty().addListener((observableValue, aBoolean, t1) -> Platform.runLater(() -> {
            Node node = scene.getFocusOwner();
            if (node != instance.tableInstance.itemSummaryTable) {
                instance.tableInstance.itemSummaryTable.requestFocus();
            } else {
                scene.getRoot().getChildrenUnmodifiable().get(0).requestFocus();
            }
        }));
    }
    public static void main(String[] args) {
        launch();
    }
}