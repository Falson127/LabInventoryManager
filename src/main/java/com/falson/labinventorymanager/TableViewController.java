package com.falson.labinventorymanager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TableViewController implements Initializable {
    private static final Logger logger = Logger.getLogger(TableViewController.class.getName());
    private static TableViewController instance;
    private String callingMethod;
    private String userInput;

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
    @FXML
    AnchorPane tableAnchorPane;
    @SuppressWarnings("Duplicates")
    public void FillTable(){
        if(itemSummaryTable != null){
            itemSummaryTable.getItems().clear();
        }
        if (callingMethod.equals("Standard")) {
            Location currentLocation = HomeViewController.currentLocation;
            try {
                connection = DriverManager.getConnection(url);
                PreparedStatement retreiveResults = connection.prepareStatement("SELECT ID,LocationName,Name,Description,LocationID from Item_Locations WHERE LocationID = ?");
                retreiveResults.setInt(1,currentLocation.getID());
                ResultSet inventory = retreiveResults.executeQuery();
                while(inventory.next()){
                    Item item = new Item(inventory.getInt("ID"),inventory.getString("Name"),inventory.getString("LocationName"),inventory.getString("Description"),inventory.getInt("LocationID"));
                    itemSummaryTable.getItems().add(item);
                    itemSummaryTable.refresh();
                }
                retreiveResults.close();
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }else{
            try{
                connection = DriverManager.getConnection(url);
                PreparedStatement retreiveResults = connection.prepareStatement("SELECT ID, LocationName,Name,Description,LocationID FROM Item_Locations WHERE Name LIKE ?");
                String searchTerm = '%' + userInput + '%';
                retreiveResults.setString(1,searchTerm);
                ResultSet inventory = retreiveResults.executeQuery();
                while(inventory.next()){
                    Item item = new Item(inventory.getInt("ID"),inventory.getString("Name"),inventory.getString("LocationName"),inventory.getString("Description"),inventory.getInt("LocationID"));
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
    }
    public TableViewController getInstance(){
        return instance;
    }
}
