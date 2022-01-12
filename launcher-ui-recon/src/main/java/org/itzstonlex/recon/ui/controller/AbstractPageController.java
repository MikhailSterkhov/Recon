package org.itzstonlex.recon.ui.controller;

import javafx.fxml.FXML;

import java.net.URL;
import java.util.ResourceBundle;

public abstract class AbstractPageController {

    @FXML
    protected ResourceBundle resources;

    @FXML
    protected URL location;

    @FXML
    public abstract void initialize();


    public ResourceBundle getResources() {
        return resources;
    }

    public URL getLocation() {
        return location;
    }
}
