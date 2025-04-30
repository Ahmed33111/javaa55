package services;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Service pour l'envoi d'emails (version simulée)
 */
public class EmailService {
    private static final ExecutorService executor = Executors.newFixedThreadPool(2);
    
    /**
     * Simule l'envoi d'un email de manière asynchrone
     * @param to Adresse email du destinataire
     * @param subject Sujet de l'email
     * @param body Contenu de l'email
     * @return CompletableFuture<Boolean> indiquant si l'envoi a réussi
     */
    public static CompletableFuture<Boolean> sendEmail(String to, String subject, String body) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Simuler un délai d'envoi
                Thread.sleep(1000);
                
                // Afficher les détails de l'email
                System.out.println("=== SIMULATION D'ENVOI D'EMAIL ===");
                System.out.println("À: " + to);
                System.out.println("Sujet: " + subject);
                System.out.println("Corps: " + body);
                System.out.println("=================================");
                
                System.out.println("Email simulé envoyé avec succès à " + to);
                return true;
            } catch (InterruptedException e) {
                System.err.println("Erreur lors de la simulation d'envoi d'email: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }, executor);
    }
    
    /**
     * Simule l'envoi d'un email HTML de manière asynchrone
     * @param to Adresse email du destinataire
     * @param subject Sujet de l'email
     * @param htmlBody Contenu HTML de l'email
     * @return CompletableFuture<Boolean> indiquant si l'envoi a réussi
     */
    public static CompletableFuture<Boolean> sendHtmlEmail(String to, String subject, String htmlBody) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Simuler un délai d'envoi
                Thread.sleep(1000);
                
                // Afficher les détails de l'email
                System.out.println("=== SIMULATION D'ENVOI D'EMAIL HTML ===");
                System.out.println("À: " + to);
                System.out.println("Sujet: " + subject);
                System.out.println("Corps HTML: " + htmlBody);
                System.out.println("=====================================");
                
                System.out.println("Email HTML simulé envoyé avec succès à " + to);
                return true;
            } catch (InterruptedException e) {
                System.err.println("Erreur lors de la simulation d'envoi d'email HTML: " + e.getMessage());
                e.printStackTrace();
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