package com.example.phoneshop.data.model;

import java.io.Serializable;

public class Category implements Serializable {
    private final String id;
    private final String name;

    public Category(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }
}