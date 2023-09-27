package com.falson.labinventorymanager;

import java.io.IOException;
import java.net.URL;
import java.sql.*;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class DatabaseController implements Initializable {
    String url = "jdbc:sqlite:LabInventory.sqlite";
    private Connection connection;
    @FXML
    private TextField addEntry_Name;
    @FXML
    private TextField addEntry_Description;
    @FXML
    private TextField addEntry_Category;
    @FXML
    private ChoiceBox<String> addEntry_Location;
    @FXML
    private DatePicker addEntry_Date;
    @FXML
    private TextField addLocation_Name;
    @FXML
    private Button addEntry_SubmitButton;
    @FXML
    private void onSubmitLocationButtonClick(){
        String locationName = addLocation_Name.getText() ;
        int parentID = 0;
        //testing commit tracking
        try{
            //TODO Redesign to grab location from TreeView, rather than having user specify ParentID. User should only be prompted to enter name for location
            connection = DriverManager.getConnection(url);
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO locations_index(Name, ParentID) VALUES(?,?)");
            preparedStatement.setString(1,locationName);
            preparedStatement.setInt(2,parentID);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void onSubmitEntryButtonClick(){
        String locationName = HomeViewController.currentLocation.getName();
        String Name = addEntry_Name.getText();
        String Category = addEntry_Category.getText();
        String Description = addEntry_Description.getText();
        Integer LocationID = HomeViewController.currentLocation.getID();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        try {
            LocalDate Date = addEntry_Date.getValue();
            String DateString = Date.format(formatter);
            String url = "jdbc:sqlite:LabInventory.sqlite";
            connection = DriverManager.getConnection(url);
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Item_Locations(Name,Category,Description,LocationName,DateReceived,LocationID) VALUES(?,?,?,?,?,?)");
            preparedStatement.setString(1,Name);
            preparedStatement.setString(2,Category);
            preparedStatement.setString(3,Description);
            preparedStatement.setString(4,locationName);
            preparedStatement.setString(5,DateString);
            preparedStatement.setInt(6,LocationID);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
            HomeViewController instance = HomeViewController.getInstance();
            instance.UpdateTableView();
            Stage stage = (Stage) addEntry_SubmitButton.getScene().getWindow();
            stage.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void onCancelEntryButtonClick(){}
    @FXML
    private void onCancelLocationButtonClick(){}

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (this.addEntry_Date != null) {
            addEntry_Date.setValue(LocalDate.now());
        }
    }
}
