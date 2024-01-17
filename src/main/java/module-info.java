module hellofx {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.denisreshetov to javafx.fxml;
    exports com.denisreshetov;
}