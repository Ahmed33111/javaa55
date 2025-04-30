package services;

import entities.Publication;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SearchService {
    private Connection con;

    public SearchService() {
        con = MyDatabase.getInstance().getCnx();
    }
    
    /**
     * Recherche des publications par mot-clé
     */
    public List<Publication> searchPublications(String keyword) throws SQLException {
        List<Publication> results = new ArrayList<>();
        
        String req = "SELECT * FROM publication WHERE titre LIKE ? OR contenu LIKE ?";
        PreparedStatement ps = con.prepareStatement(req);
        
        String searchPattern = "%" + keyword + "%";
        ps.setString(1, searchPattern);
        ps.setString(2, searchPattern);
        
        ResultSet rs = ps.executeQuery();
        
        while (rs.next()) {
            int id = rs.getInt("id_publication");
            String titre = rs.getString("titre");
            String contenu = rs.getString("contenu");
            Date datePublication = rs.getDate("date_publication");
            int idUser = rs.getInt("id_user");
            
            Publication publication = new Publication(id, titre, contenu, datePublication, idUser);
            results.add(publication);
        }
        
        return results;
    }
    
    /**
     * Recherche avancée avec plusieurs critères
     */
    public List<Publication> advancedSearch(String keyword, Date dateDebut, Date dateFin, Integer idUser) throws SQLException {
        List<Publication> results = new ArrayList<>();
        
        StringBuilder reqBuilder = new StringBuilder("SELECT * FROM publication WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            reqBuilder.append(" AND (titre LIKE ? OR contenu LIKE ?)");
            String searchPattern = "%" + keyword + "%";
            params.add(searchPattern);
            params.add(searchPattern);
        }
        
        if (dateDebut != null) {
            reqBuilder.append(" AND date_publication >= ?");
            params.add(dateDebut);
        }
        
        if (dateFin != null) {
            reqBuilder.append(" AND date_publication <= ?");
            params.add(dateFin);
        }
        
        if (idUser != null) {
            reqBuilder.append(" AND id_user = ?");
            params.add(idUser);
        }
        
        reqBuilder.append(" ORDER BY date_publication DESC");
        
        PreparedStatement ps = con.prepareStatement(reqBuilder.toString());
        
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
        
        ResultSet rs = ps.executeQuery();
        
        while (rs.next()) {
            int id = rs.getInt("id_publication");
            String titre = rs.getString("titre");
            String contenu = rs.getString("contenu");
            Date datePublication = rs.getDate("date_publication");
            int userId = rs.getInt("id_user");
            
            Publication publication = new Publication(id, titre, contenu, datePublication, userId);
            results.add(publication);
        }
        
        return results;
    }
    
    /**
     * Filtre les publications par date (les plus récentes d'abord)
     */
    public List<Publication> filterByDate(int limit) throws SQLException {
        List<Publication> results = new ArrayList<>();
        
        String req = "SELECT * FROM publication ORDER BY date_publication DESC LIMIT ?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, limit);
        
        ResultSet rs = ps.executeQuery();
        
        while (rs.next()) {
            int id = rs.getInt("id_publication");
            String titre = rs.getString("titre");
            String contenu = rs.getString("contenu");
            Date datePublication = rs.getDate("date_publication");
            int idUser = rs.getInt("id_user");
            
            Publication publication = new Publication(id, titre, contenu, datePublication, idUser);
            results.add(publication);
        }
        
        return results;
    }
    
    /**
     * Filtre les publications par popularité (nombre de commentaires)
     */
    public List<Publication> filterByPopularity(int limit) throws SQLException {
        List<Publication> results = new ArrayList<>();
        
        String req = "SELECT p.*, COUNT(c.id_commentaire) as nb_commentaires " +
                     "FROM publication p " +
                     "LEFT JOIN commentaire c ON p.id_publication = c.id_publication " +
                     "GROUP BY p.id_publication " +
                     "ORDER BY nb_commentaires DESC " +
                     "LIMIT ?";
        
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, limit);
        
        ResultSet rs = ps.executeQuery();
        
        while (rs.next()) {
            int id = rs.getInt("id_publication");
            String titre = rs.getString("titre");
            String contenu = rs.getString("contenu");
            Date datePublication = rs.getDate("date_publication");
            int idUser = rs.getInt("id_user");
            
            Publication publication = new Publication(id, titre, contenu, datePublication, idUser);
            results.add(publication);
        }
        
        return results;
    }
    
    /**
     * Filtre les publications par utilisateur
     */
    public List<Publication> filterByUser(int userId) throws SQLException {
        List<Publication> results = new ArrayList<>();
        
        String req = "SELECT * FROM publication WHERE id_user = ? ORDER BY date_publication DESC";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, userId);
        
        ResultSet rs = ps.executeQuery();
        
        while (rs.next()) {
            int id = rs.getInt("id_publication");
            String titre = rs.getString("titre");
            String contenu = rs.getString("contenu");
            Date datePublication = rs.getDate("date_publication");
            
            Publication publication = new Publication(id, titre, contenu, datePublication, userId);
            results.add(publication);
        }
        
        return results;
    }
}