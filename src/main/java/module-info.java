module com.falson.labinventorymanager {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens com.falson.labinventorymanager to javafx.fxml;
    exports com.falson.labinventorymanager;
}