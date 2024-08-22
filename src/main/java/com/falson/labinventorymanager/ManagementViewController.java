package com.falson.labinventorymanager;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

public class ManagementViewController implements Initializable {
    @FXML
    ChoiceBox activeDatabaseSelector;
    @FXML
    Button managementViewSaveCloseButton;
    @FXML
    TextField newDatabaseNameField;
    public void initialize(URL url, ResourceBundle resourceBundle){
        BuildDatabaseSelectorChoices();
    }
    public void BuildDatabaseSelectorChoices(){
        Set<String> availableDatabases = LabManagerMain.instance.EnumerateDatabases("./Databases");
        for(String availableDB: availableDatabases){
            var temp = availableDB.replace(".sqlite","");
            activeDatabaseSelector.getItems().add(temp);
        }
        String activeDB = LabManagerMain.db_url.replace(LabManagerMain.db_url_base,"").replace(".sqlite","");
        activeDatabaseSelector.getSelectionModel().select(activeDB);
    }
    @FXML
    private void OnSaveCloseAction(){
        var selectedDB = activeDatabaseSelector.getSelectionModel().getSelectedItem();
        var newURL = LabManagerMain.db_url_base + selectedDB + ".sqlite";
        var debug = LabManagerMain.db_url;
        if (newURL != LabManagerMain.db_url) {
            LabManagerMain.db_url = newURL;
            HomeViewController controller = HomeViewController.getInstance();
            controller.BuildAll();
        }
        Stage stage = (Stage) managementViewSaveCloseButton.getScene().getWindow();
        stage.close();
    }
    @FXML
    private void OnSetDefaultAction(){
        if(LabManagerMain.db_url.contains("default_")){
            return;
        }
        Set<String> availableDatabases = LabManagerMain.instance.EnumerateDatabases("./Databases");
        String oldDefault = availableDatabases.stream().filter(s -> s.contains("default_")).findFirst().get();
        File oldFile = new File("./Databases/" + oldDefault);
        String newName = oldDefault.replaceFirst("default_","");
        File newFile = new File(oldFile.getParent(),newName);
        boolean renamed = oldFile.renameTo(newFile);
        //Old file no longer prefixed with default_

       String newDefault = LabManagerMain.db_url.replace(LabManagerMain.db_url_base, "");
       oldFile = new File("./Databases/" + newDefault);
       newName = "default_" + newDefault;
       newFile = new File(oldFile.getParent(),newName);
       renamed = oldFile.renameTo(newFile);
       //Active Database is now the default
        LabManagerMain.db_url = LabManagerMain.db_url_base + newName;
        BuildDatabaseSelectorChoices();
    }
    @FXML
    private void OnCreateNewDBAction(){
        var rawInput = newDatabaseNameField.getText();
        String newDBName = rawInput.strip().replace("\\","")
                .replace("/","")
                .replace(":","");
        DatabaseGenerator generator = new DatabaseGenerator();
        generator.CreateDatabase(false,newDBName);
        //
    }
}
