module org.example.fugitivefinder {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    requires java.net.http;

    exports org.example.fugitivefinder.view;
    opens org.example.fugitivefinder.view to javafx.fxml;

    exports org.example.fugitivefinder.controller;
    opens org.example.fugitivefinder.controller to javafx.fxml;

    exports org.example.fugitivefinder.service;
    exports org.example.fugitivefinder.viewModel;
    opens org.example.fugitivefinder.viewModel to javafx.fxml;
    requires com.gluonhq.maps;
    requires com.fasterxml.jackson.databind;

    opens org.example.fugitivefinder.view to javafx.graphics, javafx.fxml;
    exports org.example.fugitivefinder.view;

    opens org.example.fugitivefinder.controller to javafx.fxml;
    exports org.example.fugitivefinder.controller;
}