module org.example.oopchess {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.oopchess to javafx.fxml;
    exports org.example.oopchess;
}