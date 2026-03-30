module org.example.fugitivefinder {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.fugitivefinder to javafx.fxml;
    exports org.example.fugitivefinder;
    exports org.example.fugitivefinder.view;
    opens org.example.fugitivefinder.view to javafx.fxml;
    exports org.example.fugitivefinder.controller;
    opens org.example.fugitivefinder.controller to javafx.fxml;
}