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
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseController implements Initializable {
    static String url = "jdbc:sqlite:LabInventory.sqlite";
    private int currentItem;
    private static final Logger logger = Logger.getLogger(TableViewController.class.getName());
    private Connection connection;
    @FXML
    private TextField addEntry_Name;
    @FXML
    private TextField addEntry_Description;
    @FXML
    private TextField addEntry_Category;
    @FXML
    private TextField addEntry_Quantity;
    @FXML
    private Button addEntry_CancelButton;
    @FXML
    private Button addLocation_CancelButton;
    @FXML
    private Button editEntry_CancelButton;
    @FXML
    private DatePicker addEntry_Date;
    @FXML
    private TextField addLocation_Name;
    @FXML
    private Button addEntry_SubmitButton;
    @FXML
    private AnchorPane editEntry_Anchor;
    @FXML
    private Label editEntry_IDLabel;
    @FXML
    private TextField editEntry_Name;
    @FXML
    private TextField editEntry_Description;
    @FXML
    private TextField editEntry_Category;
    @FXML
    private TextField editEntry_Quantity;
    @FXML
    private DatePicker editEntry_Date;
    @FXML
    private Button editEntry_SubmitButton;
    @FXML
    private Button addLocation_SubmitButton;
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
    private TextArea bulkTextArea;
    @FXML
    private Button bulkCancel;
    @FXML
    private Button bulkSubmit;
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
        String Quantity = addEntry_Quantity.getText();
        Integer LocationID = HomeViewController.currentLocation.getID();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        try {
            LocalDate Date = addEntry_Date.getValue();
            String DateString = Date.format(formatter);
            String url = "jdbc:sqlite:LabInventory.sqlite";
            connection = DriverManager.getConnection(url);
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Item_Locations(Name,Category,Description,LocationName,DateReceived,LocationID,Quantity) VALUES(?,?,?,?,?,?,?)");
            preparedStatement.setString(1,Name);
            preparedStatement.setString(2,Category);
            preparedStatement.setString(3,Description);
            preparedStatement.setString(4,locationName);
            preparedStatement.setString(5,DateString);
            preparedStatement.setInt(6,LocationID);
            preparedStatement.setString(7,Quantity);
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
    private void onEditSubmitButtonClick(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        try {
            connection = DriverManager.getConnection(url);
            PreparedStatement statement = connection.prepareStatement("UPDATE Item_Locations SET Name=?,Category=?,Description=?,DateReceived=?,Quantity=? WHERE ID = ?");
            statement.setString(1,editEntry_Name.getText());
            statement.setString(2,editEntry_Category.getText());
            statement.setString(3,editEntry_Description.getText());
            statement.setString(4,formatter.format(editEntry_Date.getValue()));
            statement.setString(5,editEntry_Quantity.getText());
            statement.setInt(6,Integer.parseInt(editEntry_IDLabel.getText()));
            statement.executeUpdate();
            statement.close();
            connection.close();
            HomeViewController instance = HomeViewController.getInstance();
            instance.UpdateTableView();
            Stage stage = (Stage) editEntry_SubmitButton.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    private void onEditCancelButtonClick(){
        Stage stage = (Stage) editEntry_CancelButton.getScene().getWindow();
        stage.close();
    }
    @FXML
    private void onCancelLocationButtonClick(){
        Stage stage = (Stage) addLocation_CancelButton.getScene().getWindow();
        stage.close();
    }
    @FXML
    private void onCancelEntryButtonClick(){
        Stage stage = (Stage) addEntry_CancelButton.getScene().getWindow();
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
    public void DeleteEntry(int itemID){
        try {
            connection = DriverManager.getConnection(url);
            PreparedStatement statement = connection.prepareStatement("DELETE FROM Item_Locations WHERE ID = ?");
            statement.setInt(1,itemID);
            statement.execute();
            statement.close();
            connection.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void setCurrentItemID(int id){currentItem = id;}
    public void fetchDetailViewData(){
        try {
            connection = DriverManager.getConnection(url);
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Item_Locations WHERE ID = ?");
            statement.setInt(1,currentItem);
            PopulateDetailView(statement.executeQuery());
            statement.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void fetchEditViewData(){
        try {
            connection = DriverManager.getConnection(url);
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Item_Locations WHERE ID = ?");
            statement.setInt(1,currentItem);
            PopulateEditView(statement.executeQuery());
            statement.close();
            connection.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    private void CancelBulkSubmit(){
        Stage stage = (Stage) bulkCancel.getScene().getWindow();
        stage.close();
    }
    @FXML
    private void BulkSubmit(){
        //Bulk Add Syntax: LocationID, Name, Category, Description, LocationName, Quantity, Date Received
        String rawText = bulkTextArea.getText();
        List<String> lines = new ArrayList<>();
        lines.addAll(Arrays.asList(rawText.split("\\n"))); //split text area into individual lines
        for (String line: lines) {
            List<String> variables = new ArrayList<>();
            variables.addAll(Arrays.asList(line.split(",")));//split each line on comma to get individual variables for SQL query
            try{
                connection = DriverManager.getConnection(url);
                PreparedStatement statement = connection.prepareStatement("INSERT INTO Item_Locations(Name,Category,Description,LocationName,LocationID,DateReceived,Quantity) Values(?,?,?,?,?,?,?)");
                statement.setString(1,variables.get(1));
                statement.setString(2,variables.get(2));
                statement.setString(3, variables.get(3));
                statement.setString(4,variables.get(4));
                statement.setInt(5,Integer.parseInt(variables.get(0)));
                statement.setString(6,variables.get(6));
                statement.setString(7,variables.get(5));
                statement.execute();
                statement.close();
                connection.close();
            }catch(SQLException e){
                logger.log(Level.WARNING,"Error with SQL prepared statement. Likely an entry was formatted incorrectly on one or more lines.");
                throw new RuntimeException();
            }
        }
        //runs after all entries have finished
        HomeViewController instance = HomeViewController.getInstance();
        instance.UpdateTableView();
        Stage stage = (Stage) bulkSubmit.getScene().getWindow();
        stage.close();
    }
    private void PopulateEditView(ResultSet qR) throws SQLException{
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        Item currentItem = new Item(qR.getInt("ID"),qR.getString("Name"),qR.getString("Category"),qR.getString("Description"),qR.getString("LocationName"),qR.getString("DateReceived"),qR.getInt("LocationID"),qR.getString("Quantity"));
        editEntry_Name.setText(currentItem.getName());
        editEntry_Description.setText(currentItem.getDescription());
        editEntry_Date.setValue(LocalDate.parse(currentItem.getDateReceived(),formatter));
        editEntry_Category.setText(currentItem.getCategory());
        editEntry_Quantity.setText(currentItem.getQuantity());
        editEntry_IDLabel.setText(currentItem.getID().toString());
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
    }
}
