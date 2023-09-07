package com.falson.labinventorymanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
public class DatabaseController {
    private Connection connection;
    @FXML
    private void onSubmitLocationButtonClick(){
        String locationName = "placeholder";
        int iD = 1;
        String parent = "placeholder";
        String room = "placeholder";
        try{
            String url = "jdbc:sqlite:LabInventory.sqlite";
            connection = DriverManager.getConnection(url);
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO locations_index(Name, ID, Parent, Room) VALUES(?,?,?,?)");
            preparedStatement.setString(1,locationName);
            preparedStatement.setInt(2,iD);
            preparedStatement.setString(3,parent);
            preparedStatement.setString(4,room);

            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
