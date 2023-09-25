package com.falson.labinventorymanager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;


public class HomeViewController implements Initializable {

//TODO Change AddLocation to pull parentID from current selected location in tree view, so user doesn't have to keep track of location IDs
//TODO Change AddEntry to pull location from currently selected location in tree view, to prevent location ambiguity


    @FXML
    TreeView<Location> locationSelector;
    @FXML
    private Pane mainDynamicPanel;
    @FXML
    private Label dynamicLocationLabel;
    @FXML
    private TableView<Object> itemSummaryTable;





    public void initialize(URL url, ResourceBundle resourceBundle){
        TreeViewFactory factory = new TreeViewFactory();
        factory.GetSortedTreeView(this.locationSelector);
        locationSelector.setCellFactory(tree -> new LocationTreeCell());
        UpdateTableView();
    }
    public void UpdateTableView(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Items-Summary-View.fxml"));
            Parent root = loader.load();
            mainDynamicPanel.getChildren().clear();
            mainDynamicPanel.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void RetrieveInventoryAtLocation() {
        locationSelector.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null){
                //code to be executed when item selection is changed
                //this will generate a query to the database and pull the resulting table into the Items-Summary view
                //This view must then be loaded into the mainDynamicPanel
                Location selectedLocation = newValue.getValue();
                dynamicLocationLabel.setText(selectedLocation.getName()); //set Location Label
                itemSummaryTable.getItems().clear(); //clear items on location change, then load new items below
                ResultSet inventory = GetTableData(selectedLocation.getName());
                try {
                    while(inventory.next()) {
                        itemSummaryTable.getItems().add(new Object[]{
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
    private void onAddLocationButtonClick() throws IOException {
        //Location currentLocation = locationSelector.getSelectionModel().getSelectedItem().getValue();
        FXMLLoader fxmlLoader = new FXMLLoader(LabManagerMain.class.getResource("Add-Location-Dialogue.fxml"));
        Scene scene = new Scene(fxmlLoader.load(),511,149);
        Stage stage = new Stage();
        stage.setTitle("Add Location");
        stage.setScene(scene);
        stage.show();

    }
    @FXML
    private void onAddEntryButtonClick() throws IOException{
        //Location currentLocation = locationSelector.getSelectionModel().getSelectedItem().getValue();
        FXMLLoader fxmlLoader = new FXMLLoader(LabManagerMain.class.getResource("Add-Entry-Dialogue.fxml"));
        Scene scene = new Scene(fxmlLoader.load(),511,149);
        Stage stage = new Stage();
        stage.setTitle("Add Entry");
        stage.setScene(scene);
        stage.show();
    }


}