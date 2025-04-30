package tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainFX extends Application {
    private static Stage mainStage;


    public static void main(String[] args) {

        try {

            Class.forName("javafx.application.Application");

            launch(args);
        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: JavaFX runtime components are missing");
            System.err.println("To fix this, run the application with VM options:");
            System.err.println("--module-path \"PATH_TO_JAVAFX_SDK/lib\" --add-modules javafx.controls,javafx.fxml");
            System.err.println("Or use: mvn javafx:run");
            System.exit(1);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        mainStage = primaryStage;

        showPublicationList();
    }

    /**
     * Load FXML and set it as the current scene
     */
    public static void loadFXML(String fxmlPath, String title) {
        try {
            System.out.println("Loading FXML: " + fxmlPath);
            FXMLLoader loader = new FXMLLoader(MainFX.class.getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            mainStage.setScene(scene);
            mainStage.setTitle(title);
            mainStage.show();
        } catch (IOException e) {
            System.err.println("Error loading FXML: " + fxmlPath);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error loading FXML: " + fxmlPath);
            e.printStackTrace();
        }
    }

    /**
     * Get the main application stage
     */
    public static Stage getMainStage() {
        return mainStage;
    }
    
    // Navigation methods for publication management
    public static void showPublicationList() {
        loadFXML("/AfficherPublication.fxml", "Liste des Publications");
    }
    
    public static void showAddPublicationForm() {
        loadFXML("/AjouterPublication.fxml", "Ajouter Publication");
    }
    
    public static void showStatistics() {
        loadFXML("/Statistiques.fxml", "Statistiques du Forum");
    }
    
    // Méthode pour vérifier si un fichier FXML existe
    public static void checkFXMLFiles() {
        String[] fxmlFiles = {
            "/AfficherPublication.fxml",
            "/AjouterPublication.fxml",
            "/AfficherCommentaire.fxml",
            "/AjouterCommentaire.fxml",
            "/Statistiques.fxml"
        };
        
        for (String file : fxmlFiles) {
            if (MainFX.class.getResource(file) == null) {
                System.err.println("FXML file not found: " + file);
            } else {
                System.out.println("FXML file exists: " + file);
            }
        }
    }
}
