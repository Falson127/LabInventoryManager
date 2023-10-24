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
import java.util.logging.Level;
import java.util.logging.Logger;

public class LabManagerMain extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LabManagerMain.class.getResource("Home-View.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 592, 581);
        stage.setTitle("Laboratory Inventory");
        stage.setScene(scene);
        stage.show();
        HomeViewController instance = HomeViewController.getInstance();
        TableViewController tableInstance = instance.tableInstance;
        var psuedoPrefWidth = (tableInstance.itemSummaryTableLocation.getPrefWidth()+tableInstance.itemSummaryTableName.getPrefWidth()+tableInstance.itemSummaryTableDescription.getPrefWidth());
        //option1
        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> {
                if (psuedoPrefWidth > instance.mainDynamicPanel.getWidth()) {
                    tableInstance.itemSummaryScrollPane.setFitToWidth(false);
                } else {
                    tableInstance.itemSummaryScrollPane.setFitToWidth(true);
                }
            });
        });
        //option 2
        stage.maximizedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                Platform.runLater(() ->{
                    Node node = scene.getFocusOwner();
                    if (node != tableInstance.itemSummaryTable) {
                        tableInstance.itemSummaryTable.requestFocus();
                    } else {
                        scene.getRoot().getChildrenUnmodifiable().get(0).requestFocus();
                    }
                });
            }
        });
    }
    public static void main(String[] args) {
        launch();
    }
}