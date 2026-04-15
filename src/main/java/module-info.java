module org.example.fugitivefinder {

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.web;

    requires com.gluonhq.maps;
    requires com.fasterxml.jackson.databind;
    requires java.net.http;
    requires org.json;

    exports org.example.fugitivefinder.view;
    exports org.example.fugitivefinder.controller;

    opens org.example.fugitivefinder.model to com.fasterxml.jackson.databind;
    exports org.example.fugitivefinder.model;

    opens org.example.fugitivefinder.viewModel to javafx.fxml;
    exports org.example.fugitivefinder.viewModel;

    opens org.example.fugitivefinder.session to javafx.fxml;
    exports org.example.fugitivefinder.session;

    opens org.example.fugitivefinder.service to javafx.fxml;
    exports org.example.fugitivefinder.service;

    opens org.example.fugitivefinder.view to javafx.fxml, javafx.graphics;
    opens org.example.fugitivefinder.controller to javafx.fxml;
}