package com.felix.simpledb.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ViewSwitcher {

    public static Scene scene;
    private static final Map<View, Parent> cache = new HashMap<>();

    public static void setScene(Scene scene) {
        ViewSwitcher.scene = scene;
    }

    public static void switchTo(View view) {
       if (scene == null) {
           System.out.println("No scene was set");
           return;
       }

        Parent root;

        if (view.isCacheable()) {
            if (cache.containsKey(view)) {
                root = cache.get(view);
            } else {
                root = getLoader(view);
                cache.put(view, root);
            }
        } else {
            root = getLoader(view);
        }

        scene.setRoot(Objects.requireNonNull(root));
    }

    private static Parent getLoader(View view) {
        try {
            return FXMLLoader.load(
                    Objects.requireNonNull(ViewSwitcher.class.getClassLoader().getResource(view.getFileName())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
