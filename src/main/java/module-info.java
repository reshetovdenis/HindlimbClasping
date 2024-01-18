module hellofx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.desktop;

    opens com.denisreshetov to javafx.fxml;
    exports com.denisreshetov;
}