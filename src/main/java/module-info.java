module org.example.oopchess {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens org.example.oopchess to javafx.fxml;
    exports org.example.oopchess;
}