package com.falson.labinventorymanager;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.List;
import java.sql.*;


public class HomeViewController {

    TreeView<String> locationSelector = new TreeView<>();
    @FXML
    private Pane mainDynamicPanel;
    @FXML
    private Label dynamicLocationLabel;
    private void RetrieveInventoryAtLocation(){
        locationSelector.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null){
                //code to be executed when item selection is changed
                //this will generate a query to the database and pull the resulting table into the Items-Summary view
                //This view must then be loaded into the mainDynamicPanel
                String selectedLocation = newValue.getValue();
                dynamicLocationLabel.setText(selectedLocation); //set Location Label

                ResultSet inventory = GetTableData(selectedLocation);

            }
        });
    }

    private List<Tuple<String,String>> BuildLocationsTree(){
        return null;
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