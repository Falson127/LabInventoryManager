package com.falson.labinventorymanager;

public class Item {
    private final Integer ID;
    private final String Name;
    private final String Category;
    private final String Description;
    private final String locationName;

    private final String DateReceived;
    private final Integer LocationID;
    private final Integer Quantity;
    private final Integer Threshold;
    private final String Unit;

    public Item(Integer id, String name, String category, String description, String locationname, String datereceived, Integer locationid, Integer quantity, Integer threshold, String unit){
        this.ID = id;
        this.Name = name;
        this.Category = category;
        this.Description = description;
        this.locationName = locationname;
        this.DateReceived = datereceived;
        this.LocationID = locationid;
        this.Quantity = quantity;
        this.Threshold = threshold;
        this.Unit = unit;
    }

    public Item(Integer id,String name, String locationname, String description,Integer locationID){
        this(id,name,null,description,locationname,null,locationID,null,null,null);
    }
    public Item(Integer id, String name, String locationname, String description, Integer locationID, Integer quantity, Integer threshold){
        this(id,name,null,description,locationname,null,locationID,quantity,threshold,null);
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
    public Integer getQuantity(){return Quantity;}
    public Integer getThreshold(){return Threshold;}
    public String getUnit(){return Unit;}
}