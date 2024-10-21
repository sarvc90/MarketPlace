module com.marketplace {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.logging;
    requires javafx.graphics;
    requires java.xml.bind;

    opens com.marketplace to javafx.fxml;
    exports com.marketplace;
}
