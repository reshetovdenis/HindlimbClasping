module hellofx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    opens com.denisreshetov to javafx.fxml;
    exports com.denisreshetov;
}