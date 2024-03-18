package com.falson.labinventorymanager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class TableViewController implements Initializable {
    private static TableViewController instance;
    private String callingMethod;
    private String userInput = "";
    final String url = "jdbc:sqlite:LabInventory.sqlite";
    @FXML
    TableView<Item> itemSummaryTable;
    @FXML
    TableColumn<Item, String> itemSummaryTableLocation;
    @FXML
    TableColumn<Item, String> itemSummaryTableName;
    @FXML
    TableColumn<Item, String> itemSummaryTableDescription;
    @FXML
    ScrollPane itemSummaryScrollPane;

    @SuppressWarnings("Duplicates")
    public void FillTable(){
        if(itemSummaryTable != null){
            itemSummaryTable.getItems().clear();
        }
        Connection connection;
        if (callingMethod.equals("Standard") || userInput.isEmpty()) {
            Location currentLocation = HomeViewController.currentLocation;
            try {
                connection = DriverManager.getConnection(url);
                PreparedStatement retrieveResults = connection.prepareStatement("SELECT ID,LocationName,Name,Description,LocationID,Quantity,Threshold from Item_Locations WHERE LocationID = ?");
                retrieveResults.setInt(1,currentLocation.getID());
                ResultSet inventory = retrieveResults.executeQuery();
                while(inventory.next()){
                    Item item = new Item(inventory.getInt("ID"),inventory.getString("Name"),inventory.getString("LocationName"),inventory.getString("Description"),inventory.getInt("LocationID"),inventory.getFloat("Quantity"),inventory.getFloat("Threshold"));
                    itemSummaryTable.getItems().add(item);
                    itemSummaryTable.refresh();
                }
                retrieveResults.close();
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }else{
            try{
                connection = DriverManager.getConnection(url);
                PreparedStatement retreiveResults = connection.prepareStatement("SELECT ID, LocationName,Name,Description,LocationID,Quantity,Threshold FROM Item_Locations WHERE Name LIKE ? OR Description LIKE ?");
                String searchTerm = '%' + userInput + '%';
                retreiveResults.setString(1,searchTerm);
                retreiveResults.setString(2,searchTerm);
                ResultSet inventory = retreiveResults.executeQuery();
                while(inventory.next()){
                    Item item = new Item(inventory.getInt("ID"),inventory.getString("Name"),inventory.getString("LocationName"),inventory.getString("Description"),inventory.getInt("LocationID"),inventory.getFloat("Quantity"),inventory.getFloat("Threshold"));
                    itemSummaryTable.getItems().add(item);
                    itemSummaryTable.refresh();
                }
                retreiveResults.close();
                connection.close();
            }catch(SQLException e){
                throw new RuntimeException(e);
            }
        }
    }
    public void setCallingMethod(String method){
        callingMethod = method;
    }
    public void setUserInput(String input){
        userInput = input;
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        instance = this;
        itemSummaryTable.setOnMouseClicked(event ->{
            if (event.getClickCount() == 2){
                try {
                    Item selectedItem = itemSummaryTable.getSelectionModel().getSelectedItem();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("Item-Detail-View.fxml"));
                    Parent root = loader.load();
                    DatabaseController controller = loader.getController();
                    controller.setCurrentItemID(selectedItem.getID());
                    controller.fetchDetailViewData();
                    Scene scene = new Scene(root);
                    Stage stage = new Stage();
                    stage.sizeToScene();
                    stage.setScene(scene);
                    stage.setTitle("Detail View");
                    stage.show();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        itemSummaryTableLocation.setCellValueFactory(new PropertyValueFactory<>("locationName"));
        itemSummaryTableName.setCellValueFactory(new PropertyValueFactory<>("Name"));
        itemSummaryTableDescription.setCellValueFactory(new PropertyValueFactory<>("Description"));
        itemSummaryTable.setRowFactory(tv -> {
            TableRow<Item> row = new TableRow<>();
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null) {
                    if (newItem.getQuantity() < newItem.getThreshold()) {
                        row.setStyle("-fx-background-color: rgba(255,255,0,0.6)");
                    } else if (newItem.getQuantity() == 0) {
                        row.setStyle("-fx-background-color: rgba(255,0,0,0.6)");
                    } else {
                        row.setStyle("");
                    }
                }
            });
            return row;
        });
    }
    public TableViewController getInstance(){
        return instance;
    }
}
