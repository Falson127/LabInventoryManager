package com.falson.labinventorymanager;

import java.sql.*;
import javafx.scene.control.*;
import javafx.fxml.FXML;
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
    private TextField addLocation_Parent;
    @FXML
    private TextField addLocation_Room;

    @FXML
    private void onSubmitLocationButtonClick(){
        String locationName = addLocation_Name.getText() ;
        String parent = addLocation_Parent.getText();
        String room = addLocation_Room.getText();
        try{

            connection = DriverManager.getConnection(url);
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO locations_index(Name, Parent, Room) VALUES(?,?,?)");
            preparedStatement.setString(1,locationName);
            preparedStatement.setString(2,parent);
            preparedStatement.setString(3,room);
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


}
