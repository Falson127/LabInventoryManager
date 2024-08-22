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
import java.io.File;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LabManagerMain extends Application {
    public static LabManagerMain instance;
    public static final String db_url_base = "jdbc:sqlite:./Databases/";
    public static String db_url = "";
    public Set<String> EnumerateDatabases(String dir){
        return Stream.of(new File(dir).listFiles())
                .filter(file -> !file.isDirectory() && file.getName().toLowerCase().endsWith(".sqlite"))
                .map(File::getName)
                .collect(Collectors.toSet());
    }
    @Override
    public void start(Stage stage) throws IOException {
        instance = this;
        //Locate the correct database first
        var availableDatabases = EnumerateDatabases("./Databases");
        if(!availableDatabases.isEmpty()) {
            for (var database : availableDatabases) {
                if (database.startsWith("default_")) {
                    var url = db_url_base + database;
                    db_url = url;
                }
            }
        }
        else{
            DatabaseGenerator generator = new DatabaseGenerator();
            generator.CreateDatabase(true,"");
        }
        //then initialize the UI
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
    public static void main(String[] args) { launch(); }
}