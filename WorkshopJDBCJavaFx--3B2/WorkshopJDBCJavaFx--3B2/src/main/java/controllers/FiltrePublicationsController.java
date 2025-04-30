package controllers;

import entities.Publication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import services.SearchService;
import services.ServicePublication;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class FiltrePublicationsController {
    @FXML
    private ComboBox<String> cbTypeFiltre;
    
    @FXML
    private TextField tfParametre;
    
    @FXML
    private Label lblParametre;
    
    @FXML
    private TableView<Publication> tvPublications;
    
    @FXML
    private TableColumn<Publication, Integer> colId;
    
    @FXML
    private TableColumn<Publication, String> colTitre;
    
    @FXML
    private TableColumn<Publication, String> colContenu;
    
    @FXML
    private TableColumn<Publication, String> colDate;
    
    @FXML
    private TableColumn<Publication, Integer> colUser;
    
    @FXML
    private Label lblResultats;
    
    private SearchService searchService = new SearchService();
    private ServicePublication servicePublication = new ServicePublication();
    
    // Types de filtres disponibles
    private final String FILTRE_DATE = "Date (plus récentes)";
    private final String FILTRE_POPULARITE = "Popularité";
    private final String FILTRE_UTILISATEUR = "Utilisateur";
    private final String FILTRE_MOT_CLE = "Mot-clé";
    
    @FXML
    void initialize() {
        // Initialiser les colonnes du tableau
        colId.setCellValueFactory(new PropertyValueFactory<>("id_publication"));
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colContenu.setCellValueFactory(new PropertyValueFactory<>("contenu"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date_publication"));
        colUser.setCellValueFactory(new PropertyValueFactory<>("id_user"));
        
        // Initialiser la liste déroulante des filtres
        ObservableList<String> typesFiltres = FXCollections.observableArrayList(
            FILTRE_DATE,
            FILTRE_POPULARITE,
            FILTRE_UTILISATEUR,
            FILTRE_MOT_CLE
        );
        cbTypeFiltre.setItems(typesFiltres);
        
        // Ajouter un écouteur pour changer le libellé du paramètre en fonction du filtre sélectionné
        cbTypeFiltre.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                switch (newVal) {
                    case FILTRE_DATE:
                        lblParametre.setText("Nombre:");
                        tfParametre.setPromptText("Ex: 10");
                        break;
                    case FILTRE_POPULARITE:
                        lblParametre.setText("Nombre:");
                        tfParametre.setPromptText("Ex: 5");
                        break;
                    case FILTRE_UTILISATEUR:
                        lblParametre.setText("ID Utilisateur:");
                        tfParametre.setPromptText("Ex: 1");
                        break;
                    case FILTRE_MOT_CLE:
                        lblParametre.setText("Mot-clé:");
                        tfParametre.setPromptText("Ex: Java");
                        break;
                }
            }
        });
        
        // Charger toutes les publications par défaut
        try {
            chargerToutesPublications();
        } catch (SQLException e) {
            showErrorAlert("Erreur de base de données", "Impossible de charger les publications: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    void appliquerFiltre(ActionEvent event) {
        String typeFiltre = cbTypeFiltre.getValue();
        if (typeFiltre == null) {
            showAlert(Alert.AlertType.WARNING, "Filtre non sélectionné", "Veuillez sélectionner un type de filtre.");
            return;
        }
        
        String parametre = tfParametre.getText().trim();
        
        try {
            List<Publication> resultats = null;
            
            switch (typeFiltre) {
                case FILTRE_DATE:
                    int limit = 10; // Valeur par défaut
                    if (!parametre.isEmpty()) {
                        try {
                            limit = Integer.parseInt(parametre);
                        } catch (NumberFormatException e) {
                            showAlert(Alert.AlertType.ERROR, "Erreur de format", "Le nombre doit être un entier valide.");
                            return;
                        }
                    }
                    resultats = searchService.filterByDate(limit);
                    break;
                    
                case FILTRE_POPULARITE:
                    int limitPop = 10; // Valeur par défaut
                    if (!parametre.isEmpty()) {
                        try {
                            limitPop = Integer.parseInt(parametre);
                        } catch (NumberFormatException e) {
                            showAlert(Alert.AlertType.ERROR, "Erreur de format", "Le nombre doit être un entier valide.");
                            return;
                        }
                    }
                    resultats = searchService.filterByPopularity(limitPop);
                    break;
                    
                case FILTRE_UTILISATEUR:
                    if (parametre.isEmpty()) {
                        showAlert(Alert.AlertType.WARNING, "Paramètre manquant", "Veuillez entrer un ID utilisateur.");
                        return;
                    }
                    try {
                        int userId = Integer.parseInt(parametre);
                        resultats = searchService.filterByUser(userId);
                    } catch (NumberFormatException e) {
                        showAlert(Alert.AlertType.ERROR, "Erreur de format", "L'ID utilisateur doit être un entier valide.");
                        return;
                    }
                    break;
                    
                case FILTRE_MOT_CLE:
                    if (parametre.isEmpty()) {
                        showAlert(Alert.AlertType.WARNING, "Paramètre manquant", "Veuillez entrer un mot-clé.");
                        return;
                    }
                    resultats = searchService.searchPublications(parametre);
                    break;
            }
            
            if (resultats != null) {
                afficherResultats(resultats);
            }
            
        } catch (SQLException e) {
            showErrorAlert("Erreur de base de données", "Erreur lors de l'application du filtre: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    void reinitialiser(ActionEvent event) {
        try {
            chargerToutesPublications();
            cbTypeFiltre.getSelectionModel().clearSelection();
            tfParametre.clear();
        } catch (SQLException e) {
            showErrorAlert("Erreur de base de données", "Impossible de charger les publications: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    void retourMenu(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AfficherPublication.fxml"));
            tvPublications.getScene().setRoot(root);
        } catch (IOException e) {
            showErrorAlert("Erreur de navigation", "Impossible de charger la page: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void chargerToutesPublications() throws SQLException {
        List<Publication> publications = servicePublication.recuperer();
        afficherResultats(publications);
    }
    
    private void afficherResultats(List<Publication> publications) {
        ObservableList<Publication> observableList = FXCollections.observableArrayList(publications);
        tvPublications.setItems(observableList);
        lblResultats.setText(publications.size() + " publication(s) trouvée(s)");
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