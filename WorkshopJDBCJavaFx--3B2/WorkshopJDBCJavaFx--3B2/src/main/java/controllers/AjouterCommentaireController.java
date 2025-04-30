package controllers;

import entities.Commentaire;
import entities.Publication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import services.ServiceCommentaire;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

public class AjouterCommentaireController {
    @FXML
    private DatePicker dpDateCommentaire;
    @FXML
    private Label lblPublication;
    @FXML
    private TextArea taTexte;
    @FXML
    private TextField tfUserId;

    private Publication currentPublication;
    private Commentaire commentaireToUpdate;
    private boolean isUpdateMode = false;
    private ServiceCommentaire serviceCommentaire = new ServiceCommentaire();

    @FXML
    void initialize() {
        // Set today's date as default
        dpDateCommentaire.setValue(LocalDate.now());
    }

    public void setPublication(Publication publication) {
        this.currentPublication = publication;
        lblPublication.setText(publication.getTitre());
    }

    public void setCommentaire(Commentaire commentaire) {
        this.commentaireToUpdate = commentaire;
        this.isUpdateMode = true;

        // Fill the form with comment data
        taTexte.setText(commentaire.getTexte());

        // Convert SQL Date to LocalDate
        if (commentaire.getDate_commentaire() != null) {
            dpDateCommentaire.setValue(commentaire.getDate_commentaire().toLocalDate());
        }

        tfUserId.setText(String.valueOf(commentaire.getId_user()));
    }

    @FXML
    void Ajouter(ActionEvent event) {
        if (!validateInputs()) {
            return;
        }

        try {
            String texte = taTexte.getText();
            Date dateCommentaire = Date.valueOf(dpDateCommentaire.getValue());
            int userId = Integer.parseInt(tfUserId.getText());

            if (isUpdateMode) {
                Commentaire updatedCommentaire = new Commentaire(
                        commentaireToUpdate.getId_commentaire(),
                        texte,
                        dateCommentaire,
                        userId,
                        currentPublication.getId_publication()
                );
                serviceCommentaire.modifier(updatedCommentaire);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Comment updated successfully.");
            } else {
                Commentaire newCommentaire = new Commentaire(
                        texte,
                        dateCommentaire,
                        userId,
                        currentPublication.getId_publication()
                );
                serviceCommentaire.ajouter(newCommentaire);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Comment added successfully.");
            }

            // Return to comments list
            navigateToCommentairesList();
        } catch (SQLException e) {
            showErrorAlert("Database Error", "Failed to save comment: " + e.getMessage());
        }
    }

    @FXML
    void Annuler(ActionEvent event) {
        navigateToCommentairesList();
    }

    private boolean validateInputs() {
        if (taTexte.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Comment text cannot be empty.");
            return false;
        }

        if (dpDateCommentaire.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a date.");
            return false;
        }

        try {
            int userId = Integer.parseInt(tfUserId.getText());
            if (userId <= 0) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "User ID must be a positive number.");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "User ID must be a valid number.");
            return false;
        }

        return true;
    }

    private void navigateToCommentairesList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherCommentaire.fxml"));
            Parent root = loader.load();

            // Get controller and pass the current publication
            AfficherCommentaireController controller = loader.getController();
            controller.setPublication(currentPublication);

            taTexte.getScene().setRoot(root);
        } catch (IOException e) {
            showErrorAlert("Navigation Error", "Unable to load comments list: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String content) {
        showAlert(Alert.AlertType.ERROR, title, content);
    }
}
