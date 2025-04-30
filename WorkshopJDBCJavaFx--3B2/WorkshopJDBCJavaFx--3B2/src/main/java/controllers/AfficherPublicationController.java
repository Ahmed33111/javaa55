package controllers;

import entities.Publication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import services.ServicePublication;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class AfficherPublicationController {
    @FXML
    private TableColumn colContenu;
    @FXML
    private TableColumn colDate;
    @FXML
    private TableColumn colTitre;
    @FXML
    private TableColumn colUser;
    @FXML
    private TableView<Publication> tvPublications;

    private ServicePublication servicePublication = new ServicePublication();

    @FXML
    void initialize() {
        try {
            // Set up column cell value factories
            colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
            colContenu.setCellValueFactory(new PropertyValueFactory<>("contenu"));
            colDate.setCellValueFactory(new PropertyValueFactory<>("date_publication"));
            colUser.setCellValueFactory(new PropertyValueFactory<>("id_user"));

            // Load data
            refreshPublications();
        } catch (SQLException e) {
            showErrorAlert("Error loading publications", e.getMessage());
        }
    }

    private void refreshPublications() throws SQLException {
        ObservableList<Publication> publications = FXCollections.observableList(servicePublication.recuperer());
        tvPublications.setItems(publications);
    }

    @FXML
    void AjouterPublication(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AjouterPublication.fxml"));
            tvPublications.getScene().setRoot(root);
        } catch (IOException e) {
            showErrorAlert("Navigation Error", "Unable to load publication form: " + e.getMessage());
        }
    }

    @FXML
    void ModifierPublication(ActionEvent event) {
        Publication selectedPublication = tvPublications.getSelectionModel().getSelectedItem();
        if (selectedPublication == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a publication to modify.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterPublication.fxml"));
            Parent root = loader.load();

            // Get controller and pass the selected publication
            AjouterPublicationController controller = loader.getController();
            controller.setPublication(selectedPublication); // This method needs to be implemented in AjouterPublicationController

            tvPublications.getScene().setRoot(root);
        } catch (IOException e) {
            showErrorAlert("Navigation Error", "Unable to load publication form: " + e.getMessage());
        }
    }

    @FXML
    void SupprimerPublication(ActionEvent event) {
        Publication selectedPublication = tvPublications.getSelectionModel().getSelectedItem();
        if (selectedPublication == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a publication to delete.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete this publication?\nThis will also delete all associated comments.");
        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                servicePublication.supprimer(selectedPublication);
                refreshPublications();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Publication deleted successfully.");
            } catch (SQLException e) {
                showErrorAlert("Delete Error", "Failed to delete publication: " + e.getMessage());
            }
        }
    }

    @FXML
    void AfficherCommentaires(ActionEvent event) {
        Publication selectedPublication = tvPublications.getSelectionModel().getSelectedItem();
        if (selectedPublication == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a publication to view comments.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherCommentaire.fxml"));
            Parent root = loader.load();

            // Get controller and pass the selected publication
            AfficherCommentaireController controller = loader.getController();
            controller.setPublication(selectedPublication);

            tvPublications.getScene().setRoot(root);
        } catch (IOException e) {
            showErrorAlert("Navigation Error", "Unable to load comments view: " + e.getMessage());
        }
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
