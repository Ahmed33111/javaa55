package tests;

import entities.Personne;
import services.ServicePersonne;
import utils.MyDatabase;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        ServicePersonne servicePersonne = new ServicePersonne();
        try {
           servicePersonne.ajouter(new Personne(20 ,"moncer","dridi"));
            servicePersonne.modifier(new Personne(1,18 ,"feten","abdelli"));
            servicePersonne.supprimer(new Personne(1,18 ,"feten","abdelli"));
            System.out.println(servicePersonne.recuperer());;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


    }
}
