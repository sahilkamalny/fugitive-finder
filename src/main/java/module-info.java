module org.example.fugitivefinder {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    requires java.net.http;

    opens org.example.fugitivefinder.view to javafx.fxml;
    exports org.example.fugitivefinder.view;

    opens org.example.fugitivefinder.controller to javafx.fxml;
    exports org.example.fugitivefinder.controller;
}