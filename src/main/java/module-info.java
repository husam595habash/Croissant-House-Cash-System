module com.example.database {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires kernel;
    requires layout;
    requires io;

    opens com.example.database to javafx.fxml;
    opens ClassesOfTables to javafx.base;
    exports com.example.database;
}
