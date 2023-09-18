package com.falson.labinventorymanager;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.List;
import java.sql.*;


public class HomeViewController {
//TODO Create TreeView factory
//TODO Determine system ensuring parent field for location refers to a UID rather than a non-unique name

    TreeView<String> locationSelector = new TreeView<>();
    @FXML
    private Pane mainDynamicPanel;
    @FXML
    private Label dynamicLocationLabel;
    @FXML
    private TableView<Object> ItemSummaryTable;

    private void RetrieveInventoryAtLocation() {
        locationSelector.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null){
                //code to be executed when item selection is changed
                //this will generate a query to the database and pull the resulting table into the Items-Summary view
                //This view must then be loaded into the mainDynamicPanel
                String selectedLocation = newValue.getValue();
                dynamicLocationLabel.setText(selectedLocation); //set Location Label
                ItemSummaryTable.getItems().clear(); //clear items on location change, then load new items below
                ResultSet inventory = GetTableData(selectedLocation);
                try {
                    while(inventory.next()) {
                        ItemSummaryTable.getItems().add(new Object[]{
                                inventory.getInt("ID"),
                                inventory.getString("Name"),
                                inventory.getString("Description")
                        });
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }


    private ResultSet GetTableData(String location){
        try {
            String url = "jdbc:sqlite:LabInventory.sqlite";
            Connection connection = DriverManager.getConnection(url);
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT (ID,Name, Description) FROM Item_Locations WHERE LocationName = ?");
            preparedStatement.setString(1,location);
            return preparedStatement.executeQuery();
        }
        catch(SQLException e){
            e.printStackTrace();
            return null;
        }
    }
    @FXML
    private void onAddLocationButtonClick() {
    }
    @FXML
    private void onAddEntryButtonClick(){
    }


}