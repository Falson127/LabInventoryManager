package com.falson.labinventorymanager;

import javafx.scene.Parent;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import java.util.Optional;

public class TreeViewFactory {
    String url = "jdbc:sqlite:LabInventory.sqlite";
    //To achieve our goal, let's iterate through and build out layers with recursion. First we'll look for entries with "null" parentID, as root entries
    //These are placed into a list of Layer0. We then look for entries whose parentID is in that list and add them to the next layer.
    //We'll need to create something to store the children of each entry as well. This may require a custom data structure that we load into for easier scaling

    private List<Location> GetLocationsList(){
        try {
            Connection connection = DriverManager.getConnection(url);
            PreparedStatement locationRetriever = connection.prepareStatement("SELECT (Name, ID, ParentID) from locations_index");
            ResultSet unsortedLocationsIndex = locationRetriever.executeQuery();
            List<Location> pairedIDList = new ArrayList<>();
            while(unsortedLocationsIndex.next())
            {
                int ID = unsortedLocationsIndex.getInt("ID");
                int ParentID = unsortedLocationsIndex.getInt("ParentID");
                String Name = unsortedLocationsIndex.getString("Name");
                Location location = new Location(ID, Name, ParentID);
                pairedIDList.add(location);
            }
            unsortedLocationsIndex.close();
            locationRetriever.close();
            connection.close();
            return pairedIDList;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Location> BuildChildrenLists(){
        List<Location> locationsList = GetLocationsList();
        for (int i = 0; i < locationsList.size(); i++){
            int currentLocationID = locationsList.get(i).getID();
            for (Location location: locationsList //iterate through all locations, check parent against upper loop ID. If they match, add sub-location to children list of parent location
                 ) {
                if (location.getParentID() == currentLocationID){
                    locationsList.get(i).addChild(location);
                }
            }
        }
        return locationsList;
    }

    public Tuple<TreeView<Location>,List<Location>> BuildTreeView(List<Location> locationsList){
        TreeView<Location> treeView = new TreeView<Location>();
        for (Location location: locationsList
             ) {
            if (location.getID() == 0){
                TreeItem<Location> rootItem = new TreeItem<>(location);
                treeView.setRoot(rootItem);
            }
        }//find and set the root item
        for (Location location: locationsList
             ) {
            if(location.getID() != 0){
                TreeItem<Location> treeItem = new TreeItem<>(location);
                treeView.getRoot().getChildren().add(treeItem);
            }
        }//load all locations that aren't set as ID = 0 for root into the children of the root item
        //pass loaded treeView to next method for sorting the relationships with locationList in Tuple
        Tuple<TreeView<Location>,List<Location>> unsortedTuple = new Tuple<>(treeView,locationsList);
        return unsortedTuple;
    }

    public TreeView<Location> SortTreeView(){
        Tuple<TreeView<Location>,List<Location>> unsortedTuple = BuildTreeView(BuildChildrenLists());
        TreeView<Location> unsortedTreeView = unsortedTuple.first();
        List<Location> locationsList = unsortedTuple.second();
        TreeView<Location> sortedView = unsortedTreeView;
        List<TreeItem<Location>> listOfItems = sortedView.getRoot().getChildren();
        outerLoop:
        for (TreeItem<Location> treeItem: listOfItems
             ) {
            int parentID = treeItem.getValue().getParentID();
            for (TreeItem<Location> potentialParent: listOfItems
                 ) {
                int currentID = potentialParent.getValue().getID();
                if(parentID == currentID){ //if the parentID of the current object matches the ID of the test object, add current Object to test Object's children list
                    potentialParent.getChildren().add(treeItem);
                    break outerLoop;
                }
            }
        }

        return sortedView;
    }
}
