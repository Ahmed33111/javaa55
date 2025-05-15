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
import java.util.List;
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
    private Label lblStatusCommentaire;
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
        lblTitrePublication.setText("Commentaires pour: " + publication.getTitre());

        try {
            List<Commentaire> commentaires = serviceCommentaire.getCommentairesByPublication(publication.getId_publication());
            refreshCommentaires(commentaires);
            
            // Mettre à jour le statut
            if (commentaires.isEmpty()) {
                lblStatusCommentaire.setText("Aucun commentaire pour cette publication");
            } else {
                lblStatusCommentaire.setText(commentaires.size() + " commentaire(s) trouvé(s)");
            }
        } catch (SQLException e) {
            showErrorAlert("Erreur de chargement", "Erreur lors du chargement des commentaires: " + e.getMessage());
            lblStatusCommentaire.setText("Erreur lors du chargement des commentaires");
        }
    }

    private void refreshCommentaires(List<Commentaire> commentaires) {
        ObservableList<Commentaire> observableCommentaires = FXCollections.observableList(commentaires);
        tvCommentaires.setItems(observableCommentaires);
    }
    
    private void refreshCommentaires() throws SQLException {
        if (currentPublication != null) {
            List<Commentaire> commentaires = serviceCommentaire.getCommentairesByPublication(currentPublication.getId_publication());
            refreshCommentaires(commentaires);
            
            // Mettre à jour le statut
            if (commentaires.isEmpty()) {
                lblStatusCommentaire.setText("Aucun commentaire pour cette publication");
            } else {
                lblStatusCommentaire.setText(commentaires.size() + " commentaire(s) trouvé(s)");
            }
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
            showErrorAlert("Erreur de navigation", "Impossible de charger le formulaire de commentaire: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void ModifierCommentaire(ActionEvent event) {
        Commentaire selectedCommentaire = tvCommentaires.getSelectionModel().getSelectedItem();
        if (selectedCommentaire == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner un commentaire à modifier.");
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
            showErrorAlert("Erreur de navigation", "Impossible de charger le formulaire de commentaire: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void SupprimerCommentaire(ActionEvent event) {
        Commentaire selectedCommentaire = tvCommentaires.getSelectionModel().getSelectedItem();
        if (selectedCommentaire == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner un commentaire à supprimer.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Êtes-vous sûr de vouloir supprimer ce commentaire?");
        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                serviceCommentaire.supprimer(selectedCommentaire);
                refreshCommentaires();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Commentaire supprimé avec succès.");
            } catch (SQLException e) {
                showErrorAlert("Erreur de suppression", "Échec de la suppression du commentaire: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    void Retour(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AfficherPublication.fxml"));
            tvCommentaires.getScene().setRoot(root);
        } catch (IOException e) {
            showErrorAlert("Erreur de navigation", "Impossible de charger la liste des publications: " + e.getMessage());
            e.printStackTrace();
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