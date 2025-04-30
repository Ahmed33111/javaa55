package services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SMSService {
    private static final ExecutorService executor = Executors.newFixedThreadPool(2);
    
    // Remplacer par vos identifiants Twilio
    private static final String ACCOUNT_SID = "votre_account_sid";
    private static final String AUTH_TOKEN = "votre_auth_token";
    private static final String FROM_NUMBER = "votre_numero_twilio"; // Format: +33612345678
    
    /**
     * Envoie un SMS de manière asynchrone
     */
    public static CompletableFuture<Boolean> sendSMS(String to, String message) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // URL de l'API Twilio
                URL url = new URL("https://api.twilio.com/2010-04-01/Accounts/" + ACCOUNT_SID + "/Messages.json");
                
                // Ouvrir la connexion
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                
                // Ajouter l'authentification Basic
                String auth = ACCOUNT_SID + ":" + AUTH_TOKEN;
                String encodedAuth = java.util.Base64.getEncoder().encodeToString(auth.getBytes());
                conn.setRequestProperty("Authorization", "Basic " + encodedAuth);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                
                // Préparer les données à envoyer
                String data = "From=" + FROM_NUMBER + "&To=" + to + "&Body=" + java.net.URLEncoder.encode(message, "UTF-8");
                
                // Envoyer la requête
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = data.getBytes("utf-8");
                    os.write(input, 0, input.length);           
                }
                
                // Vérifier la réponse
                int responseCode = conn.getResponseCode();
                
                if (responseCode >= 200 && responseCode < 300) {
                    System.out.println("SMS envoyé avec succès à " + to);
                    return true;
                } else {
                    // Lire le message d'erreur
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    br.close();
                    
                    System.err.println("Erreur lors de l'envoi du SMS: " + response.toString());
                    return false;
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de l'envoi du SMS: " + e.getMessage());
                return false;
            }
        }, executor);
    }
    
    /**
     * Version simplifiée pour les tests (simulation d'envoi)
     */
    public static CompletableFuture<Boolean> sendSMSSimulation(String to, String message) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Simuler un délai d'envoi
                Thread.sleep(1000);
                
                // Afficher les détails du SMS
                System.out.println("=== SIMULATION D'ENVOI DE SMS ===");
                System.out.println("À: " + to);
                System.out.println("Message: " + message);
                System.out.println("===============================");
                
                return true;
            } catch (InterruptedException e) {
                System.err.println("Erreur lors de la simulation d'envoi de SMS: " + e.getMessage());
                return false;
            }
        }, executor);
    }
    
    /**
     * Ferme le pool d'exécution lors de l'arrêt de l'application
     */
    public static void shutdown() {
        executor.shutdown();
    }
}