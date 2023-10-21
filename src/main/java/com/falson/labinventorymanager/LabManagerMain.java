package com.falson.labinventorymanager;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LabManagerMain extends Application {
    private static final Logger logger = Logger.getLogger(TableViewController.class.getName());
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LabManagerMain.class.getResource("Home-View.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 592, 581);
        stage.setTitle("Laboratory Inventory");
        stage.setScene(scene);
        stage.show();
        HomeViewController instance = HomeViewController.getInstance();
        TableViewController tableInstance = instance.tableInstance;
        AtomicBoolean waitForRescale = new AtomicBoolean(false);
        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> {
                if (tableInstance.itemSummaryTable.getPrefWidth() > instance.mainDynamicPanel.getWidth()) {
                    tableInstance.itemSummaryScrollPane.setFitToWidth(false);
                    logger.log(Level.WARNING, "Setting FitToWidth = false");
                } else {
                    tableInstance.itemSummaryScrollPane.setFitToWidth(true);
                    logger.log(Level.WARNING, "Setting FitToWidth = true");
                    waitForRescale.set(true);
                }
                Node node = scene.getFocusOwner();
                var oldWidth = tableInstance.itemSummaryTable.getWidth();
                tableInstance.itemSummaryTable.requestFocus();
                logger.log(Level.WARNING, String.format("TableWidth = %s\nScrollPaneWidth = %s", tableInstance.itemSummaryTable.getWidth(), tableInstance.itemSummaryScrollPane.getWidth()));

            });
        });
    }

    public static void main(String[] args) {
        launch();


    }
}