package com.kassaiweb.bpmenetrend.utils;

import java.util.ArrayList;
import java.util.List;


public class Favourites {

    private static Favourites instance;
    private List<String> stopIds = new ArrayList<>();

    public static Favourites getInstance() {
        if (instance == null) {
            instance = new Favourites();
        }
        return instance;
    }

    public String getFavouritesToAPI() {
        String favourites = null;
        for (int i = 0; i < stopIds.size(); i++) {
            if (favourites == null) {
                favourites = stopIds.get(i);
            } else {
                favourites = favourites + ";" + stopIds.get(i);
            }
        }
        return favourites;
    }

    public void setFavouritesFromString(String string) {

        String[] ids = string.split(";");
        for (int i = 0; i < ids.length; i++) {
            if (!stopIds.contains(ids[i])) {
                stopIds.add(ids[i]);
            }
        }

    }

    public void add(String id) {
        if (!stopIds.contains(id)) {
            stopIds.add(id);
        }
    }

    public boolean contains(String id) {
        return stopIds.contains(id);
    }

    public void remove(String id) {
        stopIds.remove(id);
    }

    public int count() {
        return stopIds.size();
    }
}
