package controllers;

import entities.Personne;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import services.ServicePersonne;

import java.sql.SQLException;

public class AfficherPersonneController {
    @javafx.fxml.FXML
    private TableColumn colAge;
    @javafx.fxml.FXML
    private TableColumn colNom;
    @javafx.fxml.FXML
    private TableView tvPersonnes;
    @javafx.fxml.FXML
    private TableColumn colPrenom;

    @FXML
    void initialize() {
        ServicePersonne servicePersonne = new ServicePersonne();
        try {
            ObservableList<Personne> observableList= FXCollections.observableList(servicePersonne.recuperer());
            tvPersonnes.setItems(observableList);
            colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
            colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
            colAge.setCellValueFactory(new PropertyValueFactory<>("age"));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
