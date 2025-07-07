package com.example.buggydemo.controller;

import com.example.buggydemo.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public String addItem(@RequestParam String item) {
        cartService.addItem(item);
        return "Item added: " + item;
    }

    @PostMapping("/remove")
    public String removeItem(@RequestParam String item) {
        cartService.removeItem(item);
        return "Item removed: " + item;
    }

    @GetMapping("/contains")
    public boolean containsItem(@RequestParam String item) {
        return cartService.containsItem(item);
    }

    @GetMapping
    public Object getCart() {
        return cartService.getCart().getItems();
    }
} 