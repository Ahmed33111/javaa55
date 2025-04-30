package services;

import entities.Personne;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicePersonne implements IService<Personne> {
    private Connection con;

    public ServicePersonne() {
        con = MyDatabase.getInstance().getCnx();

    }
    @Override
    public void ajouter(Personne personne) throws SQLException {
        String req ="INSERT INTO personne(nom,prenom,age)"
        +"VALUES ('"+personne.getNom()+"','"+personne.getPrenom()+"',"+personne.getAge()+")";
        Statement st = con.createStatement();
        st.executeUpdate(req);
        System.out.println("personne ajouté");

    }

    @Override
    public void modifier(Personne personne) throws SQLException {
        String req = "update personne set nom=? , prenom=? , age=? where id=?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setString(1, personne.getNom());
        ps.setString(2, personne.getPrenom());
        ps.setInt(3, personne.getAge());
        ps.setInt(4, personne.getId());
        ps.executeUpdate();
        System.out.println("personne modifié");
    }

    @Override
    public void supprimer(Personne personne) throws SQLException {
        String req = "delete from personne where id=?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, personne.getId());
        ps.executeUpdate();
        System.out.println("personne supprimé");
    }

    @Override
    public List<Personne> recuperer() throws SQLException {
        List<Personne> personnes = new ArrayList<>();
        String req = "select * from personne";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(req);
        while (rs.next()) {
            int idP = rs.getInt("id");
            int ageP = rs.getInt(4);
            String nomP = rs.getString("nom");
            String prenomP = rs.getString("prenom");
            Personne p= new Personne(idP,ageP,nomP,prenomP);
            personnes.add(p);

        }
        return personnes;
    }
}
