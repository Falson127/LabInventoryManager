package com.falson.labinventorymanager;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import java.sql.*;

public class TableViewController {
    String url = "jdbc:sqlite:LabInventory.sqlite";
    private Connection connection;
    @FXML
    TableView<Object> itemSummaryTable;
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
            retreiveResults.close();
            connection.close();
            while(inventory.next()){
                itemSummaryTable.getItems().add(new Object[]{
                        inventory.getString("LocationName"),
                        inventory.getString("Name"),
                        inventory.getString("Description")
                });
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
}
