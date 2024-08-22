package com.falson.labinventorymanager;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseGenerator {
    private final String defaultUrl= "jdbc:sqlite:./Databases/default_LabInventory.sqlite";
    private static final Logger logger = Logger.getLogger(DatabaseGenerator.class.getName());
    public DatabaseGenerator(){
        //Constructor
    }

    public String CreateDatabase(Boolean isDefault, String fileName){
        try {
            String url;
            if(isDefault){
                url = defaultUrl;
            }
            else{
                url = LabManagerMain.db_url_base + fileName + ".sqlite";
            }
            Connection conn = DriverManager.getConnection(url);

            String createLocationsTable = "CREATE TABLE IF NOT EXISTS locations_index (" +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "Name TEXT NOT NULL, " +
                    "ParentID INTEGER" +
                    ");";
            String createItemsTable = "CREATE TABLE IF NOT EXISTS Item_Locations (" +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "Name TEXT NOT NULL, " +
                    "Category TEXT NOT NULL, " +
                    "Description TEXT NOT NULL, " +
                    "LocationName TEXT NOT NULL, " +
                    "DateReceived TEXT NOT NULL, " +
                    "LocationID INTEGER, " +
                    "Quantity INTEGER, " +
                    "Threshold INTEGER, " +
                    "Unit TEXT NOT NULL" +
                    ");";
            String createRootLocation = "INSERT INTO locations_index (-1,root,-1)";

            PreparedStatement locTableStatement = conn.prepareStatement(createLocationsTable);
            PreparedStatement itemTableStatement = conn.prepareStatement(createItemsTable);
            locTableStatement.executeUpdate();
            itemTableStatement.executeUpdate();
            PreparedStatement createRootLoc = conn.prepareStatement("INSERT INTO main.locations_index(ID,Name,ParentID) VALUES (-1,'root',-1)");
            createRootLoc.executeUpdate();

            return url;
        }catch(SQLException ex){
            logger.log(Level.WARNING,String.format("Failed to create database: %s",ex));
            return "-1";
        }
    }
}
