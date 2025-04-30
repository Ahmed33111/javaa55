package entities;

import java.sql.Date;

public class Publication {
    private int id_publication;
    private String titre;
    private String contenu;
    private Date date_publication;
    private int id_user;

    // Constructor with id
    public Publication(int id_publication, String titre, String contenu, Date date_publication, int id_user) {
        this.id_publication = id_publication;
        this.titre = titre;
        this.contenu = contenu;
        this.date_publication = date_publication;
        this.id_user = id_user;
    }

    // Constructor without id (for insertion)
    public Publication(String titre, String contenu, Date date_publication, int id_user) {
        this.titre = titre;
        this.contenu = contenu;
        this.date_publication = date_publication;
        this.id_user = id_user;
    }

    // Getters and Setters
    public int getId_publication() {
        return id_publication;
    }

    public void setId_publication(int id_publication) {
        this.id_publication = id_publication;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public Date getDate_publication() {
        return date_publication;
    }

    public void setDate_publication(Date date_publication) {
        this.date_publication = date_publication;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    @Override
    public String toString() {
        return "Publication{" +
                "id_publication=" + id_publication +
                ", titre='" + titre + '\'' +
                ", contenu='" + contenu + '\'' +
                ", date_publication=" + date_publication +
                ", id_user=" + id_user +
                '}';
    }
}
