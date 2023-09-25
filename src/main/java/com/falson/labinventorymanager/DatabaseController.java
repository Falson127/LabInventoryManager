package com.falson.labinventorymanager;

import java.io.IOException;
import java.sql.*;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DatabaseController {
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
        String locationName = addEntry_Location.getValue();
        String Name = addEntry_Name.getText();
        String Category = addEntry_Category.getText();
        String Description = addEntry_Description.getText();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        try {
            LocalDate Date = addEntry_Date.getValue();
            String DateString = Date.format(formatter);
            String url = "jdbc:sqlite:LabInventory.sqlite";
            connection = DriverManager.getConnection(url);
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Item_Locations(Name,Category,Description,LocationName,DateReceived) VALUES(?,?,?,?,?)");
            preparedStatement.setString(1,Name);
            preparedStatement.setString(2,Category);
            preparedStatement.setString(3,Description);
            preparedStatement.setString(4,locationName);
            preparedStatement.setString(5,DateString);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void onCancelEntryButtonClick(){}
    @FXML
    private void onCancelLocationButtonClick(){}

}
