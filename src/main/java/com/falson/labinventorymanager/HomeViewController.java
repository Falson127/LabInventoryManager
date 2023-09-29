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
    public static Location currentLocation;
    private TableViewController tableInstance;
    private static HomeViewController instance;
    @FXML
    TreeView<Location> locationSelector;
    @FXML
    private Pane mainDynamicPanel;
    @FXML
    private Label dynamicLocationLabel;
    @FXML
    private Button buttonDeleteLocation;
    @FXML
    private Button buttonEditEntry;

    public void initialize(URL url, ResourceBundle resourceBundle){
        RebuildTree();

        UpdateTableView();
        SetTreeEventWatcher();
        dynamicLocationLabel.setText(currentLocation.getName());
        instance = this;
    }
    public void RebuildTree(){
        TreeViewFactory factory = new TreeViewFactory();
        factory.GetSortedTreeView(this.locationSelector);
        locationSelector.setCellFactory(tree -> new LocationTreeCell());
        currentLocation = locationSelector.getRoot().getValue();
        expandAllTreeItems(locationSelector.getRoot());

    }
    private void expandAllTreeItems(TreeItem<Location> item) {
        if (item != null && !item.isLeaf()) {
            item.setExpanded(true);
            for (TreeItem<Location> child : item.getChildren()) {
                expandAllTreeItems(child);
            }
        }
    }
    public void UpdateTableView(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Items-Summary-View.fxml"));
            Parent root = loader.load();
            TableViewController controller = loader.getController();
            tableInstance = controller.getInstance();
            mainDynamicPanel.getChildren().clear();
            mainDynamicPanel.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void SetTreeEventWatcher() {
        locationSelector.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null){
                //code to be executed when item selection is changed
                //this will generate a query to the database and pull the resulting table into the Items-Summary view
                //This view must then be loaded into the mainDynamicPanel
                currentLocation = newValue.getValue(); //set current location
                dynamicLocationLabel.setText(currentLocation.getName()); //set Location Label
                UpdateTableView();
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
    private void onSearchBarAction(){

    }
    @FXML
    private void onAddLocationButtonClick() throws IOException {
        //Location currentLocation = locationSelector.getSelectionModel().getSelectedItem().getValue();
        FXMLLoader fxmlLoader = new FXMLLoader(LabManagerMain.class.getResource("Add-Location-Dialogue.fxml"));
        Scene scene = new Scene(fxmlLoader.load(),282,121);
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
    @FXML
    private void onDeleteLocationButton(){
        int id = currentLocation.getID();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setContentText(String.format("Warning! You are about to delete the location: '%s'. This action will remove all sub-locations and entries that fall under this location.\nAre you sure you want to continue?",currentLocation.getName()));
        ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);

        if (result == ButtonType.OK){
            DatabaseController.DeleteLocation(id);
        }
        RebuildTree();
    }
    @FXML
    private void onEditEntryButton() throws IOException{
        Item currentItem = tableInstance.itemSummaryTable.getSelectionModel().getSelectedItem();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Item-Edit-View.fxml"));
        Parent root = loader.load();
        DatabaseController controller = loader.getController();
        controller.setCurrentItemID(currentItem.getID());
        controller.fetchEditViewData();
        Scene scene = new Scene(root,511,149);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Edit Entry");
        stage.show();
    }
    public static HomeViewController getInstance(){
        return instance;
    }

}