package com.example.buggydemo.controller;

import com.example.buggydemo.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<?> addItem(@RequestParam String item) {
        try {
            cartService.addItem(item);
            return ResponseEntity.ok("Item added: " + item);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @PostMapping("/remove")
    public ResponseEntity<?> removeItem(@RequestParam String item) {
        try {
            cartService.removeItem(item);
            return ResponseEntity.ok("Item removed: " + item);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @GetMapping("/contains")
    public ResponseEntity<?> containsItem(@RequestParam String item) {
        try {
            boolean result = cartService.containsItem(item);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @GetMapping
    public ResponseEntity<?> getCart() {
        try {
            return ResponseEntity.ok(cartService.getCart().getItems());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }
} 