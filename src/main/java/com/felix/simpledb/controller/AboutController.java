package com.felix.simpledb.controller;

import com.felix.simpledb.view.View;
import com.felix.simpledb.view.ViewSwitcher;
import javafx.fxml.FXML;

public class AboutController {

    @FXML
    protected void onGoBack() {
        ViewSwitcher.switchTo(View.MAIN);
    }
}
