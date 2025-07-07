package com.example.buggydemo.model;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<String> items = new ArrayList<>();

    public List<String> getItems() {
        return items;
    }

    // State management bug: exposes internal list directly
    public void addItem(String item) {
        items.add(item);
    }

    public void removeItem(String item) {
        items.remove(item);
    }
} 