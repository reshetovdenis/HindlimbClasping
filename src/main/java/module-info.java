module com.reshetovdenis.clasping {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.reshetovdenis.clasping to javafx.fxml;
    exports com.reshetovdenis.clasping;
}