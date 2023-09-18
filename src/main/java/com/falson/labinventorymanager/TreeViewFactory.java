package com.falson.labinventorymanager;

import javafx.scene.Parent;
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
            for (Location location: locationsList //iterate through all locations, check parent against upper loop ID. If they match, add sublocation to children list of parent location
                 ) {
                if (location.getParentID() == currentLocationID){
                    locationsList.get(i).addChild(location);
                }
            }
        }
        return locationsList;
    }

    public TreeView<Location> BuildTreeView(List<Location> locationsList){
        TreeView<Location> treeView = new TreeView<Location>();

        return treeView;
    }

    public TreeView<Location> SortTreeView(TreeView<Location> unsortedTreeView){
    TreeView<Location> sortedView = unsortedTreeView;

    return sortedView;
    }
}
