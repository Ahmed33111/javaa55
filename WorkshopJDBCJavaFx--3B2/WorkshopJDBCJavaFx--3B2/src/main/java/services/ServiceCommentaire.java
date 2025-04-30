package services;

import entities.Commentaire;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCommentaire implements IService<Commentaire> {
    private Connection con;

    public ServiceCommentaire() {
        con = MyDatabase.getInstance().getCnx();
    }

    @Override
    public void ajouter(Commentaire commentaire) throws SQLException {
        String req = "INSERT INTO commentaire(texte, date_commentaire, id_user, id_publication) " +
                "VALUES (?, ?, ?, ?)";

        PreparedStatement ps = con.prepareStatement(req);
        ps.setString(1, commentaire.getTexte());
        ps.setDate(2, commentaire.getDate_commentaire());
        ps.setInt(3, commentaire.getId_user());
        ps.setInt(4, commentaire.getId_publication());

        ps.executeUpdate();
        System.out.println("Commentaire ajouté");
    }

    @Override
    public void modifier(Commentaire commentaire) throws SQLException {
        String req = "UPDATE commentaire SET texte=?, date_commentaire=?, id_user=?, id_publication=? " +
                "WHERE id_commentaire=?";

        PreparedStatement ps = con.prepareStatement(req);
        ps.setString(1, commentaire.getTexte());
        ps.setDate(2, commentaire.getDate_commentaire());
        ps.setInt(3, commentaire.getId_user());
        ps.setInt(4, commentaire.getId_publication());
        ps.setInt(5, commentaire.getId_commentaire());

        ps.executeUpdate();
        System.out.println("Commentaire modifié");
    }

    @Override
    public void supprimer(Commentaire commentaire) throws SQLException {
        String req = "DELETE FROM commentaire WHERE id_commentaire=?";

        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, commentaire.getId_commentaire());

        ps.executeUpdate();
        System.out.println("Commentaire supprimé");
    }

    @Override
    public List<Commentaire> recuperer() throws SQLException {
        List<Commentaire> commentaires = new ArrayList<>();
        String req = "SELECT * FROM commentaire";

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(req);

        while (rs.next()) {
            int id = rs.getInt("id_commentaire");
            String texte = rs.getString("texte");
            Date dateCommentaire = rs.getDate("date_commentaire");
            int idUser = rs.getInt("id_user");
            int idPublication = rs.getInt("id_publication");

            Commentaire commentaire = new Commentaire(id, texte, dateCommentaire, idUser, idPublication);
            commentaires.add(commentaire);
        }

        return commentaires;
    }

    // Additional method to get all comments for a specific publication
    public List<Commentaire> getCommentairesByPublication(int publicationId) throws SQLException {
        List<Commentaire> commentaires = new ArrayList<>();
        String req = "SELECT * FROM commentaire WHERE id_publication=? ORDER BY date_commentaire DESC";

        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, publicationId);

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            int id = rs.getInt("id_commentaire");
            String texte = rs.getString("texte");
            Date dateCommentaire = rs.getDate("date_commentaire");
            int idUser = rs.getInt("id_user");

            Commentaire commentaire = new Commentaire(id, texte, dateCommentaire, idUser, publicationId);
            commentaires.add(commentaire);
        }

        return commentaires;
    }

    // Method to get all comments by a specific user
    public List<Commentaire> getCommentairesByUser(int userId) throws SQLException {
        List<Commentaire> commentaires = new ArrayList<>();
        String req = "SELECT * FROM commentaire WHERE id_user=?";

        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, userId);

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            int id = rs.getInt("id_commentaire");
            String texte = rs.getString("texte");
            Date dateCommentaire = rs.getDate("date_commentaire");
            int idPublication = rs.getInt("id_publication");

            Commentaire commentaire = new Commentaire(id, texte, dateCommentaire, userId, idPublication);
            commentaires.add(commentaire);
        }

        return commentaires;
    }
}
