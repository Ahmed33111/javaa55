package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import services.ServiceStatistique;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class StatistiquesController {
    @FXML
    private BarChart<String, Number> barChartPublicationsParUtilisateur;
    
    @FXML
    private BarChart<String, Number> barChartCommentairesParPublication;
    
    @FXML
    private PieChart pieChartPublicationsParMois;
    
    @FXML
    private Label lblTotalPublications;
    
    @FXML
    private Label lblTotalCommentaires;
    
    @FXML
    private Label lblUtilisateurPlusActif;
    
    private ServiceStatistique serviceStatistique = new ServiceStatistique();
    
    @FXML
    void initialize() {
        try {
            // Charger les statistiques
            chargerStatistiques();
        } catch (SQLException e) {
            showErrorAlert("Erreur de base de données", "Impossible de charger les statistiques: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void chargerStatistiques() throws SQLException {
        // Statistiques générales
        int totalPublications = serviceStatistique.getNombreTotalPublications();
        int totalCommentaires = serviceStatistique.getNombreTotalCommentaires();
        int utilisateurPlusActif = serviceStatistique.getUtilisateurPlusActif();
        
        lblTotalPublications.setText(String.valueOf(totalPublications));
        lblTotalCommentaires.setText(String.valueOf(totalCommentaires));
        lblUtilisateurPlusActif.setText("Utilisateur #" + utilisateurPlusActif);
        
        // Publications par utilisateur
        Map<Integer, Integer> statsPublicationsParUtilisateur = serviceStatistique.getNombrePublicationsParUtilisateur();
        XYChart.Series<String, Number> seriesPublications = new XYChart.Series<>();
        seriesPublications.setName("Nombre de publications");
        
        for (Map.Entry<Integer, Integer> entry : statsPublicationsParUtilisateur.entrySet()) {
            seriesPublications.getData().add(new XYChart.Data<>("User " + entry.getKey(), entry.getValue()));
        }
        
        barChartPublicationsParUtilisateur.getData().add(seriesPublications);
        
        // Commentaires par publication
        Map<Integer, Integer> statsCommentairesParPublication = serviceStatistique.getNombreCommentairesParPublication();
        XYChart.Series<String, Number> seriesCommentaires = new XYChart.Series<>();
        seriesCommentaires.setName("Nombre de commentaires");
        
        for (Map.Entry<Integer, Integer> entry : statsCommentairesParPublication.entrySet()) {
            seriesCommentaires.getData().add(new XYChart.Data<>("Pub " + entry.getKey(), entry.getValue()));
        }
        
        barChartCommentairesParPublication.getData().add(seriesCommentaires);
        
        // Publications par mois
        Map<String, Integer> statsPublicationsParMois = serviceStatistique.getPublicationsParMois();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        
        for (Map.Entry<String, Integer> entry : statsPublicationsParMois.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }
        
        pieChartPublicationsParMois.setData(pieChartData);
    }
    
    @FXML
    void retourMenu(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AfficherPublication.fxml"));
            barChartPublicationsParUtilisateur.getScene().setRoot(root);
        } catch (IOException e) {
            showErrorAlert("Erreur de navigation", "Impossible de charger la liste des publications: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}