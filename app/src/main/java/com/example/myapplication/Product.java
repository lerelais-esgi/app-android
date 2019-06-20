package com.example.myapplication;

public class Product {


    private String name;
    private String expiration_date;
    private int qt;

    public Product(String name, String expiration_date) {
        this.name = name;
        this.expiration_date = expiration_date;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExpiration_date() {
        return expiration_date;
    }

    public void setExpiration_date(String expiration_date) {
        this.expiration_date = expiration_date;
    }





}
