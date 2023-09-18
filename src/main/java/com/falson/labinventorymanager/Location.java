package com.falson.labinventorymanager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Location {
    private int id;
    private String name;
    private Optional<Integer> parentID;
    private List<Location> children;

    public Location(int id, String name, Optional<Integer> parentID) {
        this.id = id;
        this.name = name;
        this.parentID = parentID;
        this.children = new ArrayList<>();
    }
    public int getID(){
        return id;
    }
    public String getName(){
        return name;
    }
    public List<Location> getChildren() {
        return children;
    }

    public Optional<Integer> getParentID() {
        return parentID;
    }
}
