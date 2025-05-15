package controllers;

import entities.Commentaire;
import entities.Publication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import services.EmailService;
import services.SMSService;
import services.ServiceCommentaire;
import tests.MainFX;

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
    
    private Publication currentPublication;
    private Commentaire commentaireToUpdate;
    private boolean isUpdateMode = false;
    private ServiceCommentaire serviceCommentaire = new ServiceCommentaire();
    
    // Utiliser une valeur fixe pour l'ID utilisateur
    private final int DEFAULT_USER_ID = 1; // Vous pouvez changer cette valeur selon vos besoins

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
    }

    @FXML
    void Ajouter(ActionEvent event) {
        if (!validateInputs()) {
            return;
        }

        try {
            String texte = taTexte.getText();
            Date dateCommentaire = Date.valueOf(dpDateCommentaire.getValue());
            
            int userId = DEFAULT_USER_ID;
            Commentaire commentaire;

            if (isUpdateMode) {
                commentaire = new Commentaire(
                        commentaireToUpdate.getId_commentaire(),
                        texte,
                        dateCommentaire,
                        userId,
                        currentPublication.getId_publication()
                );
                serviceCommentaire.modifier(commentaire);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Commentaire mis à jour avec succès.");
            } else {
                commentaire = new Commentaire(
                        texte,
                        dateCommentaire,
                        userId,
                        currentPublication.getId_publication()
                );
                serviceCommentaire.ajouter(commentaire);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Commentaire ajouté avec succès.");
                
                // Envoyer des notifications pour le nouveau commentaire
                envoyerNotifications(commentaire);
            }

            // Retourner à la liste des commentaires
            retournerAuxCommentaires();
        } catch (SQLException e) {
            showErrorAlert("Erreur de base de données", "Échec de l'enregistrement du commentaire: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void Annuler(ActionEvent event) {
        retournerAuxCommentaires();
    }

    private boolean validateInputs() {
        if (taTexte.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Erreur de validation", "Le texte du commentaire ne peut pas être vide.");
            return false;
        }

        if (dpDateCommentaire.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Erreur de validation", "Veuillez sélectionner une date.");
            return false;
        }
        
        // Ne plus vérifier l'ID utilisateur
        return true;
    }

    private void retournerAuxCommentaires() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherCommentaire.fxml"));
            Parent root = loader.load();

            AfficherCommentaireController controller = loader.getController();
            controller.setPublication(currentPublication);

            Scene scene = new Scene(root);
            MainFX.getMainStage().setScene(scene);
            MainFX.getMainStage().setTitle("Commentaires - " + currentPublication.getTitre());
        } catch (IOException e) {
            showErrorAlert("Erreur de navigation", "Impossible de charger la liste des commentaires: " + e.getMessage());
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

    /**
     * Envoie des notifications (email et SMS) pour un nouveau commentaire
     * @param commentaire Le commentaire ajouté
     */
    private void envoyerNotifications(Commentaire commentaire) {
        // 1. Envoyer notification par email
        envoyerNotificationEmail(commentaire);
        
        // 2. Envoyer notification par SMS
        envoyerNotificationSMS(commentaire);
    }
    
    /**
     * Envoie un email de notification pour un nouveau commentaire
     * @param commentaire Le commentaire ajouté
     */
    private void envoyerNotificationEmail(Commentaire commentaire) {
        // Adresse email du destinataire (à remplacer par une vraie adresse)
        String destinataire = "destinataire@example.com";
        
        // Sujet de l'email
        String sujet = "Nouveau commentaire sur la publication: " + currentPublication.getTitre();
        
        // Corps de l'email
        String corps = "Bonjour,\n\n" +
                      "Un nouveau commentaire a été ajouté à la publication \"" + 
                      currentPublication.getTitre() + "\".\n\n" +
                      "Commentaire: " + commentaire.getTexte() + "\n\n" +
                      "Date: " + commentaire.getDate_commentaire() + "\n\n" +
                      "Cordialement,\n" +
                      "L'équipe du forum";
        
        // Envoyer l'email de manière asynchrone
        EmailService.sendEmail(destinataire, sujet, corps)
            .thenAccept(success -> {
                if (success) {
                    System.out.println("Notification email envoyée avec succès");
                } else {
                    System.err.println("Échec de l'envoi de la notification email");
                }
            });
    }
    
    /**
     * Envoie un SMS de notification pour un nouveau commentaire
     * @param commentaire Le commentaire ajouté
     */
    private void envoyerNotificationSMS(Commentaire commentaire) {
        // Numéro de téléphone du destinataire (à remplacer par un vrai numéro)
        String numeroTelephone = "+33612345678";
        
        // Message SMS (version courte du message email)
        String message = "Nouveau commentaire sur \"" + currentPublication.getTitre() + 
                         "\": " + commentaire.getTexte();
        
        // Limiter la longueur du message SMS à 160 caractères
        if (message.length() > 157) {
            message = message.substring(0, 154) + "...";
        }
        
        // Envoyer le SMS de manière asynchrone (utiliser la simulation pour les tests)
        SMSService.sendSMSSimulation(numeroTelephone, message)
            .thenAccept(success -> {
                if (success) {
                    System.out.println("Notification SMS envoyée avec succès");
                } else {
                    System.err.println("Échec de l'envoi de la notification SMS");
                }
            });
    }
}
