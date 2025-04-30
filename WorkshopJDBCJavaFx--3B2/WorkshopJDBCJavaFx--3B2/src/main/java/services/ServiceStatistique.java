package services;

import utils.MyDatabase;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ServiceStatistique {
    private Connection con;

    public ServiceStatistique() {
        con = MyDatabase.getInstance().getCnx();
    }

    // Nombre de publications par utilisateur
    public Map<Integer, Integer> getNombrePublicationsParUtilisateur() throws SQLException {
        Map<Integer, Integer> stats = new HashMap<>();
        String req = "SELECT id_user, COUNT(*) as nombre FROM publication GROUP BY id_user";
        
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(req);
        
        while (rs.next()) {
            int idUser = rs.getInt("id_user");
            int nombre = rs.getInt("nombre");
            stats.put(idUser, nombre);
        }
        
        return stats;
    }
    
    // Nombre de commentaires par publication
    public Map<Integer, Integer> getNombreCommentairesParPublication() throws SQLException {
        Map<Integer, Integer> stats = new HashMap<>();
        String req = "SELECT id_publication, COUNT(*) as nombre FROM commentaire GROUP BY id_publication";
        
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(req);
        
        while (rs.next()) {
            int idPublication = rs.getInt("id_publication");
            int nombre = rs.getInt("nombre");
            stats.put(idPublication, nombre);
        }
        
        return stats;
    }
    
    // Publications par mois
    public Map<String, Integer> getPublicationsParMois() throws SQLException {
        Map<String, Integer> stats = new HashMap<>();
        String req = "SELECT MONTH(date_publication) as mois, YEAR(date_publication) as annee, " +
                     "COUNT(*) as nombre FROM publication GROUP BY YEAR(date_publication), MONTH(date_publication)";
        
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(req);
        
        while (rs.next()) {
            int mois = rs.getInt("mois");
            int annee = rs.getInt("annee");
            int nombre = rs.getInt("nombre");
            stats.put(annee + "-" + mois, nombre);
        }
        
        return stats;
    }
    
    // Méthode pour obtenir le nombre total de publications
    public int getNombreTotalPublications() throws SQLException {
        String req = "SELECT COUNT(*) as total FROM publication";
        
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(req);
        
        if (rs.next()) {
            return rs.getInt("total");
        }
        
        return 0;
    }
    
    // Méthode pour obtenir le nombre total de commentaires
    public int getNombreTotalCommentaires() throws SQLException {
        String req = "SELECT COUNT(*) as total FROM commentaire";
        
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(req);
        
        if (rs.next()) {
            return rs.getInt("total");
        }
        
        return 0;
    }
    
    // Méthode pour obtenir l'utilisateur le plus actif (avec le plus de publications)
    public int getUtilisateurPlusActif() throws SQLException {
        String req = "SELECT id_user, COUNT(*) as nombre FROM publication GROUP BY id_user ORDER BY nombre DESC LIMIT 1";
        
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(req);
        
        if (rs.next()) {
            return rs.getInt("id_user");
        }
        
        return -1; // Aucun utilisateur trouvé
    }
}