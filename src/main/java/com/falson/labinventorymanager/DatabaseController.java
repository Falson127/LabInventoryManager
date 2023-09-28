package com.falson.labinventorymanager;

import java.io.IOException;
import java.net.URL;
import java.sql.*;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DatabaseController implements Initializable {
    static String url = "jdbc:sqlite:LabInventory.sqlite";
    private int detailedItemID;
    private Connection connection;
    @FXML
    private TextField addEntry_Name;
    @FXML
    private TextField addEntry_Description;
    @FXML
    private TextField addEntry_Category;
    @FXML
    private Button addEntry_CancelButton;
    @FXML
    private Button addLocation_CancelButton;
    @FXML
    private DatePicker addEntry_Date;
    @FXML
    private TextField addLocation_Name;
    @FXML
    private Button addEntry_SubmitButton;
    @FXML
    private Button addLocation_SubmitButton;
    @FXML
    private AnchorPane detailView_Anchor;
    @FXML
    private Label detailView_Name;
    @FXML
    private Label detailView_ID;
    @FXML
    private Label detailView_LocationAndID;
    @FXML
    private Label detailView_Category;
    @FXML
    private Label detailView_DateReceived;
    @FXML
    private Label detailView_Description;
    @FXML
    private Label detailView_Quantity;
    @FXML
    private void onSubmitLocationButtonClick(){
        String locationName = addLocation_Name.getText() ;
        int parentID = HomeViewController.currentLocation.getID();

        try{

            connection = DriverManager.getConnection(url);
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO locations_index(Name, ParentID) VALUES(?,?)");
            preparedStatement.setString(1,locationName);
            preparedStatement.setInt(2,parentID);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
            HomeViewController instance = HomeViewController.getInstance();
            Stage stage = (Stage) addLocation_SubmitButton.getScene().getWindow();
            stage.close();
            instance.RebuildTree();
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
    private void onCancelEntryButtonClick(){
        Stage stage = (Stage) addEntry_CancelButton.getScene().getWindow();
        stage.close();
    }
    @FXML
    private void onCancelLocationButtonClick(){
        Stage stage = (Stage) addLocation_CancelButton.getScene().getWindow();
        stage.close();
    }
    public static void DeleteLocation(int LocationID){
        TreeViewFactory factory = new TreeViewFactory();
        List<Location> allLocations = factory.GetLocationsList();
        for (Location location: allLocations) {
            int parentID = location.getParentID();
            if (LocationID == parentID){
                DeleteLocation(location.getID());
            }
        }
            try {
                Connection connection = DriverManager.getConnection(url);
                PreparedStatement statement = connection.prepareStatement("DELETE FROM locations_index WHERE ID = ?");
                statement.setInt(1,LocationID);
                PreparedStatement statement2 = connection.prepareStatement("DELETE FROM Item_Locations WHERE LocationID = ?");
                statement2.setInt(1,LocationID);
                statement.execute();
                statement2.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
    }
    public void setDetailItemID(int id){
        detailedItemID = id;
        try {
            connection = DriverManager.getConnection(url);
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Item_Locations WHERE ID = ?");
            statement.setInt(1,detailedItemID);
            PopulateDetailView(statement.executeQuery());
            statement.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void PopulateDetailView(ResultSet queryResult) throws java.sql.SQLException{
        Item item = new Item(queryResult.getInt("ID"),queryResult.getString("Name"),queryResult.getString("Category"),queryResult.getString("Description"),
                queryResult.getString("LocationName"),queryResult.getString("DateReceived"),queryResult.getInt("LocationID"),queryResult.getString("Quantity"));
        detailView_ID.setText(item.getID().toString());
        detailView_Name.setText(item.getName());
        detailView_LocationAndID.setText(String.format("%s (%d)",item.getLocationName(),item.getLocationID()));
        detailView_Category.setText(item.getCategory());
        detailView_DateReceived.setText(item.getDateReceived());
        detailView_Description.setText(item.getDescription());
        detailView_Quantity.setText(item.getQuantity());
    }
    @Override
    public void initialize(URL urlparam, ResourceBundle resourceBundle) {
        if (this.addEntry_Date != null) {
            addEntry_Date.setValue(LocalDate.now());
        }
        if(detailView_Anchor != null){

        }
    }
}
