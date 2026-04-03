module org.example.fugitivefinder {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.gluonhq.maps;

    opens org.example.fugitivefinder.view to javafx.graphics, javafx.fxml;
    exports org.example.fugitivefinder.view;

    opens org.example.fugitivefinder.controller to javafx.fxml;
    exports org.example.fugitivefinder.controller;
}