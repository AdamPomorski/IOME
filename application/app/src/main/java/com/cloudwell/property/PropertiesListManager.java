package com.cloudwell.property;

import java.util.ArrayList;

public class PropertiesListManager {
    private static PropertiesListManager instance;
    private ArrayList<Property> properties;

    private PropertiesListManager() {
        properties = new ArrayList<>();
    }

    public static PropertiesListManager getInstance() {
        if (instance == null) {
            instance = new PropertiesListManager();
        }
        return instance;
    }

    public ArrayList<Property> getProperties() {
        return properties;
    }

    public void setProperties(ArrayList<Property> properties) {
        this.properties = properties;
    }
}
