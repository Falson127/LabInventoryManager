package com.falson.labinventorymanager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.*;
import java.util.Properties;
import java.util.ResourceBundle;

public class TableViewController implements Initializable {
    private static TableViewController instance;
    String url = "jdbc:sqlite:LabInventory.sqlite";
    private Connection connection;
    @FXML
    TableView<Item> itemSummaryTable;
    @FXML
    TableColumn<Item, String> itemSummaryTableLocation;
    @FXML
    TableColumn<Item, String> itemSummaryTableName;
    @FXML
    TableColumn<Item, String> itemSummaryTableDescription;
    public void FillTable(){
        //location, name, description
        Location currentLocation = HomeViewController.currentLocation;

        try {
            if(itemSummaryTable != null){
                itemSummaryTable.getItems().clear();
            }
            connection = DriverManager.getConnection(url);
            PreparedStatement retreiveResults = connection.prepareStatement("SELECT LocationName,Name,Description from Item_Locations WHERE LocationID = ?");
            retreiveResults.setInt(1,currentLocation.getID());
            ResultSet inventory = retreiveResults.executeQuery();
            while(inventory.next()){
                Item item = new Item(inventory.getString("Name"),inventory.getString("LocationName"),inventory.getString("Description"));
                itemSummaryTable.getItems().add(item);
                itemSummaryTable.refresh();
            }
            retreiveResults.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        instance = this;
        itemSummaryTableLocation.setCellValueFactory(new PropertyValueFactory<>("locationName"));
        itemSummaryTableName.setCellValueFactory(new PropertyValueFactory<>("Name"));
        itemSummaryTableDescription.setCellValueFactory(new PropertyValueFactory<>("Description"));
        FillTable();
    }
    public static TableViewController getInstance(){
        return instance;
    }
}
