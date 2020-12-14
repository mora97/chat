module Help {
    requires javafx.fxml;
    requires javafx.controls;
    requires java.desktop;

    opens sample;
    opens sample.controller;
    opens sample.repository;
}