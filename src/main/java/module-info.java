module com.example.oop_cw {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.json;
    requires java.desktop;
    requires weka.dev;


    opens com.example.oop_cw to javafx.fxml;
    exports com.example.oop_cw;
}