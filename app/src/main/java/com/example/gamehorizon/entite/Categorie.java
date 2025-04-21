package com.example.gamehorizon.entite;

public class Categorie {
    private int id;
    private String name;

    public Categorie(int id, String name){
        this.id = id;
        this.name = name;
    }

    public Categorie(){}

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
