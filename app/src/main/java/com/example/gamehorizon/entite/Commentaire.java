package com.example.gamehorizon.entite;

public class Commentaire {
    private int idUser;
    private String username, last_maj, contenu;

    public Commentaire(int idUser,String username,String last_maj,String contenu){
        this.idUser = idUser;
        this.username = username;
        this.last_maj = last_maj;
        this.contenu = contenu;
    }

    public Commentaire(){}

    public int getIdUser() {
        return idUser;
    }
    public String getUsername() {
        return username;
    }
    public String getLast_maj() {
        return last_maj;
    }
    public String getContenu() {
        return contenu;
    }
    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setLast_maj(String last_maj) {
        this.last_maj = last_maj;
    }
    public void setContenu(String contenu) {
        this.contenu = contenu;
    }
}
