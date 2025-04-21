package com.example.gamehorizon.entite;

public class Plateform {

    private int id;
    private String name;

    public Plateform(int id, String name){
        this.id = id;
        this.name = name;
    }

    public Plateform(){}

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    @Override
    public String toString() {
        return name;
    }
}
