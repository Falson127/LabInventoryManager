package com.falson.labinventorymanager;

import javafx.scene.control.TreeCell;

public class LocationTreeCell extends TreeCell<Location> {
    @Override
    protected void updateItem(Location item, boolean empty){
        super.updateItem(item, empty);

        if(empty || item == null){
            setText(null);
            setGraphic(null);
        } else{
            setText(String.format("%s (%d)",item.getName(),item.getID()));
        }
    }
}
