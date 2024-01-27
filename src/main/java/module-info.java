module hindlimbclasping {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.desktop;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;

    opens com.denisreshetov to javafx.fxml;
    exports com.denisreshetov;
}