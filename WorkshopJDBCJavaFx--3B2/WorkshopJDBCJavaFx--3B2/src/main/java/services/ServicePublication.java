package services;

import entities.Publication;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicePublication implements IService<Publication> {
    private Connection con;

    public ServicePublication() {
        con = MyDatabase.getInstance().getCnx();
    }

    @Override
    public void ajouter(Publication publication) throws SQLException {
        String req = "INSERT INTO publication(titre, contenu, date_publication, id_user) " +
                "VALUES (?, ?, ?, ?)";

        PreparedStatement ps = con.prepareStatement(req);
        ps.setString(1, publication.getTitre());
        ps.setString(2, publication.getContenu());
        ps.setDate(3, publication.getDate_publication());
        ps.setInt(4, publication.getId_user());

        ps.executeUpdate();
        System.out.println("Publication ajoutée");
    }

    @Override
    public void modifier(Publication publication) throws SQLException {
        String req = "UPDATE publication SET titre=?, contenu=?, date_publication=?, id_user=? " +
                "WHERE id_publication=?";

        PreparedStatement ps = con.prepareStatement(req);
        ps.setString(1, publication.getTitre());
        ps.setString(2, publication.getContenu());
        ps.setDate(3, publication.getDate_publication());
        ps.setInt(4, publication.getId_user());
        ps.setInt(5, publication.getId_publication());

        ps.executeUpdate();
        System.out.println("Publication modifiée");
    }

    @Override
    public void supprimer(Publication publication) throws SQLException {
        String req = "DELETE FROM publication WHERE id_publication=?";

        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, publication.getId_publication());

        ps.executeUpdate();
        System.out.println("Publication supprimée");
    }

    @Override
    public List<Publication> recuperer() throws SQLException {
        List<Publication> publications = new ArrayList<>();
        String req = "SELECT * FROM publication";

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(req);

        while (rs.next()) {
            int id = rs.getInt("id_publication");
            String titre = rs.getString("titre");
            String contenu = rs.getString("contenu");
            Date datePublication = rs.getDate("date_publication");
            int idUser = rs.getInt("id_user");

            Publication publication = new Publication(id, titre, contenu, datePublication, idUser);
            publications.add(publication);
        }

        return publications;
    }

    // Méthode pour trouver une publication par son ID
    public Publication findById(int id) throws SQLException {
        String req = "SELECT * FROM publication WHERE id_publication=?";

        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, id);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            String titre = rs.getString("titre");
            String contenu = rs.getString("contenu");
            Date datePublication = rs.getDate("date_publication");
            int idUser = rs.getInt("id_user");

            return new Publication(id, titre, contenu, datePublication, idUser);
        }

        return null;
    }

    // Méthode pour récupérer toutes les publications d'un utilisateur spécifique
    public List<Publication> getPublicationsByUser(int userId) throws SQLException {
        List<Publication> publications = new ArrayList<>();
        String req = "SELECT * FROM publication WHERE id_user=?";

        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, userId);

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            int id = rs.getInt("id_publication");
            String titre = rs.getString("titre");
            String contenu = rs.getString("contenu");
            Date datePublication = rs.getDate("date_publication");

            Publication publication = new Publication(id, titre, contenu, datePublication, userId);
            publications.add(publication);
        }

        return publications;
    }
}
