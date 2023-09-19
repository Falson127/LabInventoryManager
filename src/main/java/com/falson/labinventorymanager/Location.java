package com.falson.labinventorymanager;

public class Location {
    private final int id;
    private final String name;
    private final int parentID;


    public Location(int id, String name, int parentID) {
        this.id = id;
        this.name = name;
        this.parentID = parentID;
    }
    public int getID(){
        return id;
    }
    public String getName(){
        return name;
    }
    public int getParentID() {
        return parentID;
    }

}
