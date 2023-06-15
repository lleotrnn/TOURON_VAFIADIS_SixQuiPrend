module com.isep.sixquiprend {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.isep.sixquiprend.GUI to javafx.fxml;
    exports com.isep.sixquiprend.GUI;
}