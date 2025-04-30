package entities;

import java.sql.Date;

public class Commentaire {
    private int id_commentaire;
    private String texte;
    private Date date_commentaire;
    private int id_user;
    private int id_publication;

    // Constructor with id
    public Commentaire(int id_commentaire, String texte, Date date_commentaire, int id_user, int id_publication) {
        this.id_commentaire = id_commentaire;
        this.texte = texte;
        this.date_commentaire = date_commentaire;
        this.id_user = id_user;
        this.id_publication = id_publication;
    }

    // Constructor without id (for insertion)
    public Commentaire(String texte, Date date_commentaire, int id_user, int id_publication) {
        this.texte = texte;
        this.date_commentaire = date_commentaire;
        this.id_user = id_user;
        this.id_publication = id_publication;
    }

    // Getters and Setters
    public int getId_commentaire() {
        return id_commentaire;
    }

    public void setId_commentaire(int id_commentaire) {
        this.id_commentaire = id_commentaire;
    }

    public String getTexte() {
        return texte;
    }

    public void setTexte(String texte) {
        this.texte = texte;
    }

    public Date getDate_commentaire() {
        return date_commentaire;
    }

    public void setDate_commentaire(Date date_commentaire) {
        this.date_commentaire = date_commentaire;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public int getId_publication() {
        return id_publication;
    }

    public void setId_publication(int id_publication) {
        this.id_publication = id_publication;
    }

    @Override
    public String toString() {
        return "Commentaire{" +
                "id_commentaire=" + id_commentaire +
                ", texte='" + texte + '\'' +
                ", date_commentaire=" + date_commentaire +
                ", id_user=" + id_user +
                ", id_publication=" + id_publication +
                '}';
    }
}
