package com.falson.labinventorymanager;

public class Item {
    private final Integer ID;
    private final String Name;
    private final String Category;
    private final String Description;
    private final String locationName;

    private final String DateReceived;
    private final Integer LocationID;

    public Item(Integer id, String name, String category, String description, String locationname, String datereceived, Integer locationid){
        this.ID = id;
        this.Name = name;
        this.Category = category;
        this.Description = description;
        this.locationName = locationname;
        this.DateReceived = datereceived;
        this.LocationID = locationid;
    }

    public Item(String name, String locationname, String description){
        this(null,name,null,description,locationname,null,null);
    }
    public Integer getID(){
        return ID;
    }
    public Integer getLocationID(){
        return LocationID;
    }
    public String getName(){
        return Name;
    }
    public String getCategory(){
        return Category;
    }
    public String getDescription(){
        return Description;
    }
    public String getLocationName(){
        return locationName;
    }
    public String getDateReceived(){
        return DateReceived;
    }
}

