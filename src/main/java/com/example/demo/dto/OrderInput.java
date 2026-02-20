package com.example.demo.dto;

import java.util.List;

public class OrderInput {
    private List<OrderItemInput> items;

    public List<OrderItemInput> getItems() {
        return items;
    }

    public void setItems(List<OrderItemInput> items) {
        this.items = items;
    }
}

