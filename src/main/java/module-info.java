module com.falson.labinventorymanager {
    requires javafx.controls;
    requires javafx.fxml;


    requires java.sql;

    opens com.falson.labinventorymanager to javafx.fxml;
    exports com.falson.labinventorymanager;
}