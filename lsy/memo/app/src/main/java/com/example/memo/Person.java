package com.example.memo;

import java.io.Serializable;

public class Person implements Serializable {
    private String name;
    private double height;

    public void setName(String name) {
        this.name=name;
    }
    public void setHeight(double height) {
        this.height=height;
    }
    public String getName() {
        return name;
    }
    public double getHeight() {
        return height;
    }
}
