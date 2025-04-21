package com.example.gamehorizon.entite;

import java.util.Date;

public class Jeu {

    private int id;
    private String name,description,image;
    private Date release_date;
    private float moyenne_note;

    public Jeu(int id, String name,String description,String image,Date date,float moyenne_note){
        this.id = id;
        this.name = name;
        this.description = description;
        if (image != null && image.startsWith("//")) {
            this.image = "https:" + image;
        } else {
            this.image = image; // Otherwise, use the URL as is
        }
        this.release_date = date;
        this.moyenne_note = moyenne_note;
    }

    public Jeu(int id,String name,String image){
        this.id = id;
        this.name = name;
        this.description = "";
        if (image != null && image.startsWith("//")) {
            this.image = "https:" + image;
        } else {
            this.image = image; // Otherwise, use the URL as is
        }
        this.release_date = null;
        this.moyenne_note = 0;
    }

    public Jeu(){}

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public Date getRelease_date() {
        return release_date;
    }

    public float getMoyenne_note() {
        return moyenne_note;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setRelease_date(Date release_date) {
        this.release_date = release_date;
    }

    public void setMoyenne_note(float moyenne_note) {
        this.moyenne_note = moyenne_note;
    }
}
