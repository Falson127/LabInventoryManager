package com.falson.labinventorymanager;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.sql.*;


public class HomeViewController {

//TODO Change AddLocation to pull parentID from current selected location in tree view, so user doesn't have to keep track of location IDs
//TODO Change AddEntry to pull location from currently selected location in tree view, to prevent location ambiguity

    TreeView<Location> locationSelector = new TreeView<>();
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
                Location selectedLocation = newValue.getValue();
                dynamicLocationLabel.setText(selectedLocation.getName()); //set Location Label
                ItemSummaryTable.getItems().clear(); //clear items on location change, then load new items below
                ResultSet inventory = GetTableData(selectedLocation.getName());
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
        Location currentLocation = locationSelector.getSelectionModel().getSelectedItem().getValue();
    }
    @FXML
    private void onAddEntryButtonClick(){
    }


}