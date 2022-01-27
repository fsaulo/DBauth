package com.felix.simpledb.controller;

import com.felix.simpledb.view.View;
import com.felix.simpledb.view.ViewSwitcher;
import javafx.fxml.FXML;

public class MainController {

    @FXML
    protected void onAbout() {
        ViewSwitcher.switchTo(View.ABOUT);
    }

    @FXML
    protected void onLogout() {
        ViewSwitcher.switchTo(View.LOGIN);
    }
}
