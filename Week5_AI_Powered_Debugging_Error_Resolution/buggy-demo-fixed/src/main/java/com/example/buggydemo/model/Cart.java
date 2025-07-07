package com.example.buggydemo.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cart {
    private final List<String> items = Collections.synchronizedList(new ArrayList<>());

    public List<String> getItems() {
        synchronized (items) {
            return new ArrayList<>(items); // Return a copy for immutability
        }
    }

    public void addItem(String item) {
        items.add(item);
    }

    public void removeItem(String item) {
        items.remove(item);
    }
} 