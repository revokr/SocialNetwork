module main.labjavafx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.sql;
    requires java.desktop;

    opens main.labjavafx to javafx.fxml;
    exports main.labjavafx;
}