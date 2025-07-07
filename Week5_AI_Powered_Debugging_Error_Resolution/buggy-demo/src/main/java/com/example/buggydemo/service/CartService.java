package com.example.buggydemo.service;

import com.example.buggydemo.model.Cart;
import org.springframework.stereotype.Service;

@Service
public class CartService {
    private final Cart cart = new Cart();

    public void addItem(String item) {
        cart.addItem(item);
    }

    public void removeItem(String item) {
        cart.removeItem(item);
    }

    // Performance issue: O(n^2) search for item existence
    public boolean containsItem(String item) {
        for (String i : cart.getItems()) {
            for (String j : cart.getItems()) {
                if (i.equals(j) && j.equals(item)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Cart getCart() {
        return cart;
    }
} 