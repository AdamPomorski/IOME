package com.cloudwell.property;

public class Property {


    private int id;
    private String address;


    public Property(int id, String address) {
        this.id = id;
        this.address = address;
    }

    public Property() {
    }

    public int getId() {
        return id;
    }

    public void setId(int number) {
        this.id = number;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
