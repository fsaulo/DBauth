package com.felix.simpledb.view;

public enum View {
    MAIN("view/main.fxml", true),
    LOGIN("view/login.fxml", false),
    ABOUT("view/about.fxml", true);

    private final String fileName;
    private final boolean cacheable;

    View(String fileName, boolean cacheable) {
        this.fileName = fileName;
        this.cacheable = cacheable;
    }

    public String getFileName() {
        return this.fileName;
    }

    public boolean isCacheable() {
        return this.cacheable;
    }
}
