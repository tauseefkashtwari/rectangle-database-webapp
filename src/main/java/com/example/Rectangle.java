package com.example;

public class Rectangle {

    private String id;
    private String name;
    private String color;
    private double width;
    private double height;
    private double borderWidth;
    private String borderColor;
    private int image;

    //ID
    public String getId() {
        return this.id;
    }

    public void setId(String i) {
        this.id = i;
    }

    //NAME
    public String getName() {
        return this.name;
    }

    public void setName(String n) {
        this.name = n;
    }

    //COLOR
    public String getColor() {
        return this.color;
    }

    public void setColor(String c) {
        this.color = c;
    }

    //WEIGHT
    public double getWidth() {
        return this.width;
    }

    public void setWidth(double w) {
        this.width = w;
    }

    //HEIGHT
    public double getHeight() {
        return this.height;
    }

    public void setHeight(double h) {
        this.height = h;
    }

    //BORDER_WIDTH
    public double getBorderWidth() {
        return this.borderWidth;
    }

    public void setBorderWidth(double b) {
        this.borderWidth = b;
    }

    //BORDER_COLOR
    public String getBorderColor() {
        return this.borderColor;
    }

    public void setBorderColor(String c) {
        this.borderColor = c;
    }

    //IMAGE
    public int getImage() {
        return this.image;
    }

    public void setImage(int i) {
        this.image = i;
    }

}
