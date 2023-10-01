package com.falson.labinventorymanager;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


public class HomeViewController implements Initializable {
    private static final Logger logger = Logger.getLogger(TableViewController.class.getName());
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
    private TextField homeSearchBar;
    @FXML
    private Button addEntryButton;
    private BooleanProperty shiftPressed = new SimpleBooleanProperty(false);
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
            Parent root = loader.load(); //controller instance is made here
            TableViewController controller = loader.getController();
            tableInstance = controller.getInstance();
            tableInstance.setCallingMethod("Standard");
            tableInstance.FillTable();//required now that FillTable is no longer run auto by initialization
            mainDynamicPanel.getChildren().clear();
            mainDynamicPanel.getChildren().add(root);
        } catch (IOException e) {
            logger.log(Level.WARNING,"IOException being caused by UpdateTableView method call");
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

    @FXML
    private void onSearchBarAction(){
        try {
            String userInput = homeSearchBar.getText();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Items-Summary-View.fxml"));
            Parent root = loader.load(); //controller instance is made here
            TableViewController controller = loader.getController();
            tableInstance = controller.getInstance();
            tableInstance.setCallingMethod("Search");//required to direct FillTable down correct path
            tableInstance.setUserInput(userInput);//required to pass input to instance for database access
            tableInstance.FillTable();//required now that FillTable is no longer run auto by initialization
            mainDynamicPanel.getChildren().clear();
            mainDynamicPanel.getChildren().add(root);
        } catch (IOException e) {
            logger.log(Level.WARNING,"IOException caused by onSearchBarAction method");
        }
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
    private void onBulkEntryMenuItem() throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(LabManagerMain.class.getResource("Add-Entry-Bulk.fxml"));
        Scene scene = new Scene(fxmlLoader.load(),600,400);
        Stage stage = new Stage();
        stage.setTitle("Add Entry - Bulk");
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    private void onAddEntryButtonClick() throws IOException{
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
    private void onDeleteEntryButton(){
        Item currentItem = tableInstance.itemSummaryTable.getSelectionModel().getSelectedItem();
        DatabaseController controller = new DatabaseController();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setContentText(String.format("Warning! You are about to delete the Item: '%s'. This action cannot be undone. Do you wish to continue?",currentItem.getName()));
        ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);

        if (result == ButtonType.OK) {
            controller.DeleteEntry(currentItem.getID());
            UpdateTableView();
        }

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