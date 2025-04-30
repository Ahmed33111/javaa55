package controllers;

import entities.Commentaire;
import entities.Publication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import services.ServiceCommentaire;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class AfficherCommentaireController {
    @FXML
    private TableColumn colDate;
    @FXML
    private TableColumn colTexte;
    @FXML
    private TableColumn colUser;
    @FXML
    private Label lblTitrePublication;
    @FXML
    private TableView<Commentaire> tvCommentaires;

    private Publication currentPublication;
    private ServiceCommentaire serviceCommentaire = new ServiceCommentaire();

    @FXML
    void initialize() {
        // Set up column cell value factories
        colTexte.setCellValueFactory(new PropertyValueFactory<>("texte"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date_commentaire"));
        colUser.setCellValueFactory(new PropertyValueFactory<>("id_user"));
    }

    public void setPublication(Publication publication) {
        this.currentPublication = publication;
        lblTitrePublication.setText("Commentaires pour la publication: " + publication.getTitre());

        try {
            refreshCommentaires();
        } catch (SQLException e) {
            showErrorAlert("Error loading comments", e.getMessage());
        }
    }

    private void refreshCommentaires() throws SQLException {
        if (currentPublication != null) {
            ObservableList<Commentaire> commentaires = FXCollections.observableList(
                    serviceCommentaire.getCommentairesByPublication(currentPublication.getId_publication()));
            tvCommentaires.setItems(commentaires);
        }
    }

    @FXML
    void AjouterCommentaire(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterCommentaire.fxml"));
            Parent root = loader.load();

            // Get controller and pass the current publication
            AjouterCommentaireController controller = loader.getController();
            controller.setPublication(currentPublication);

            tvCommentaires.getScene().setRoot(root);
        } catch (IOException e) {
            showErrorAlert("Navigation Error", "Unable to load comment form: " + e.getMessage());
        }
    }

    @FXML
    void ModifierCommentaire(ActionEvent event) {
        Commentaire selectedCommentaire = tvCommentaires.getSelectionModel().getSelectedItem();
        if (selectedCommentaire == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a comment to modify.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterCommentaire.fxml"));
            Parent root = loader.load();

            // Get controller and pass the selected comment and publication
            AjouterCommentaireController controller = loader.getController();
            controller.setPublication(currentPublication);
            controller.setCommentaire(selectedCommentaire);

            tvCommentaires.getScene().setRoot(root);
        } catch (IOException e) {
            showErrorAlert("Navigation Error", "Unable to load comment form: " + e.getMessage());
        }
    }

    @FXML
    void SupprimerCommentaire(ActionEvent event) {
        Commentaire selectedCommentaire = tvCommentaires.getSelectionModel().getSelectedItem();
        if (selectedCommentaire == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a comment to delete.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete this comment?");
        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                serviceCommentaire.supprimer(selectedCommentaire);
                refreshCommentaires();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Comment deleted successfully.");
            } catch (SQLException e) {
                showErrorAlert("Delete Error", "Failed to delete comment: " + e.getMessage());
            }
        }
    }

    @FXML
    void Retour(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AfficherPublication.fxml"));
            tvCommentaires.getScene().setRoot(root);
        } catch (IOException e) {
            showErrorAlert("Navigation Error", "Unable to load publications view: " + e.getMessage());
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
