module org.example.fugitivefinder {

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.web;

    requires com.gluonhq.maps;
    requires com.fasterxml.jackson.databind;
    requires org.json;
    requires java.net.http;

    exports org.example.fugitivefinder.view;
    exports org.example.fugitivefinder.controller;
    exports org.example.fugitivefinder.service;
    exports org.example.fugitivefinder.viewModel;

    opens org.example.fugitivefinder.view to javafx.fxml, javafx.graphics;
    opens org.example.fugitivefinder.controller to javafx.fxml;
    opens org.example.fugitivefinder.viewModel to javafx.fxml;
}