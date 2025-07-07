package com.example.buggydemo.service;

import com.example.buggydemo.model.Cart;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class CartService {
    private final Cart cart = new Cart();
    private final Set<String> itemSet = new HashSet<>(); // For fast lookups

    public void addItem(String item) {
        if (item == null || item.trim().isEmpty()) throw new IllegalArgumentException("Item cannot be null or empty");
        cart.addItem(item);
        itemSet.add(item);
    }

    public void removeItem(String item) {
        cart.removeItem(item);
        itemSet.remove(item);
    }

    // Performance fix: O(1) lookup
    public boolean containsItem(String item) {
        return itemSet.contains(item);
    }

    public Cart getCart() {
        return cart;
    }
} 