package controllers;

import entities.Personne;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import services.ServicePersonne;

import java.io.IOException;
import java.sql.SQLException;

public class AjouterPersonneController {

    @FXML
    private TextField tfAge;

    @FXML
    private TextField tfNom;

    @FXML
    private TextField tfPrenom;


    @FXML
    void Afficher(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AfficherPersonne.fxml"));
            tfNom.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void Ajouter(ActionEvent event) {

        ServicePersonne servicePersonne = new ServicePersonne();
        Personne personne= new Personne(Integer.parseInt(tfAge.getText()),tfNom.getText(),tfPrenom.getText());
        try {
            servicePersonne.ajouter(personne);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ajouter personne");
            alert.setContentText("personne ajouté");
            alert.showAndWait();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

}
