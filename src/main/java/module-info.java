module com.example.oop_cw {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.json;


    opens com.example.oop_cw to javafx.fxml;
    exports com.example.oop_cw;
}