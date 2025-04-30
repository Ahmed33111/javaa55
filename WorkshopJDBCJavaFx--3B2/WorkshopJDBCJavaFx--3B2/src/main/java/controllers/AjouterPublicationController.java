package controllers;

import entities.Publication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import services.ServicePublication;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

public class AjouterPublicationController {
    @FXML
    private DatePicker dpDatePublication;
    @FXML
    private TextArea taContenu;
    @FXML
    private TextField tfTitre;
    @FXML
    private TextField tfUserId;

    private Publication publicationToUpdate;
    private boolean isUpdateMode = false;
    private ServicePublication servicePublication = new ServicePublication();

    @FXML
    void initialize() {
        // Set today's date as default
        dpDatePublication.setValue(LocalDate.now());
    }

    public void setPublication(Publication publication) {
        this.publicationToUpdate = publication;
        this.isUpdateMode = true;

        // Fill the form with publication data
        tfTitre.setText(publication.getTitre());
        taContenu.setText(publication.getContenu());

        // Convert SQL Date to LocalDate
        if (publication.getDate_publication() != null) {
            dpDatePublication.setValue(publication.getDate_publication().toLocalDate());
        }

        tfUserId.setText(String.valueOf(publication.getId_user()));
    }

    @FXML
    void Ajouter(ActionEvent event) {
        if (!validateInputs()) {
            return;
        }

        try {
            String titre = tfTitre.getText();
            String contenu = taContenu.getText();
            Date datePublication = Date.valueOf(dpDatePublication.getValue());
            int userId = Integer.parseInt(tfUserId.getText());

            if (isUpdateMode) {
                Publication updatedPublication = new Publication(
                        publicationToUpdate.getId_publication(),
                        titre,
                        contenu,
                        datePublication,
                        userId
                );
                servicePublication.modifier(updatedPublication);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Publication updated successfully.");
            } else {
                Publication newPublication = new Publication(
                        titre,
                        contenu,
                        datePublication,
                        userId
                );
                servicePublication.ajouter(newPublication);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Publication added successfully.");
            }

            // Return to publications list
            navigateToPublicationsList();
        } catch (SQLException e) {
            showErrorAlert("Database Error", "Failed to save publication: " + e.getMessage());
        }
    }

    @FXML
    void Annuler(ActionEvent event) {
        navigateToPublicationsList();
    }

    private boolean validateInputs() {
        if (tfTitre.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Title cannot be empty.");
            return false;
        }

        if (taContenu.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Content cannot be empty.");
            return false;
        }

        if (dpDatePublication.getValue() == null) {
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

    private void navigateToPublicationsList() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AfficherPublication.fxml"));
            tfTitre.getScene().setRoot(root);
        } catch (IOException e) {
            showErrorAlert("Navigation Error", "Unable to load publications list: " + e.getMessage());
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
