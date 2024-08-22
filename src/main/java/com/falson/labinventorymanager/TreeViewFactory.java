package com.falson.labinventorymanager;


import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.sql.*;
import java.util.Map;


public class TreeViewFactory {

    public List<Location> GetLocationsList(){
        try {
            System.out.println(LabManagerMain.db_url);
            Connection connection = DriverManager.getConnection(LabManagerMain.db_url);
            PreparedStatement locationRetriever = connection.prepareStatement("SELECT Name, ID, ParentID from locations_index");
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
    public TreeView<Location> BuildTreeView(List<Location> locationsList,TreeView<Location> fxmlTreeView){
        for (Location location: locationsList
             ) {
            if (location.getID() == -1){
                TreeItem<Location> rootItem = new TreeItem<>(location);
                fxmlTreeView.setRoot(rootItem);
            }
        }//find and set the root item
        for (Location location: locationsList
             ) {
            if(location.getID() != -1){
                TreeItem<Location> treeItem = new TreeItem<>(location);
                fxmlTreeView.getRoot().getChildren().add(treeItem);
            }
        }//load all locations that aren't set as ID = 0 for root into the children of the root item
        //pass loaded treeView to next method for sorting the relationships with locationList in Tuple
        return fxmlTreeView;
    }

    public void GetSortedTreeView(TreeView<Location> fxmlTreeView){
        TreeView<Location> sortedView = BuildTreeView(GetLocationsList(), fxmlTreeView);
        Map<Integer, TreeItem<Location>> parentMap = new HashMap<>();
        parentMap.put(sortedView.getRoot().getValue().getID(),sortedView.getRoot());
        for (TreeItem<Location> treeItem: sortedView.getRoot().getChildren()) {
            Location location = treeItem.getValue();
            parentMap.put(location.getID(),treeItem);
        }
        sortedView.getRoot().getChildren().clear();
        for (Location location: GetLocationsList()) {
            if (location.getID() != -1) {
                int parentID = location.getParentID();
                TreeItem<Location> childItem = parentMap.get(location.getID());
                TreeItem<Location> parentItem = parentMap.get(parentID);
                parentItem.getChildren().add(childItem);
            }
        }
    }
}
