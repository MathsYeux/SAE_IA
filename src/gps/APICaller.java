package gps;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class APICaller {

    // Méthode pour effectuer un appel à l'API à l'aide de HttpRequest
    public static String appelApi(String url) {
        // Transformer l'URL en un nom de fichier compatible
        String fileName = url
                .replace("https://", "")
                .replaceAll("[^a-zA-Z0-9.-]", "_")
                + ".json";

        // Construire le chemin du fichier
        Path filePath = Paths.get("src/api/", fileName);
        File file = filePath.toFile();
        StringBuilder response = new StringBuilder();

        // Vérifier si le dossier existe, sinon le créer
        File directory = new File("src/api/");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        if (file.exists()) {
            try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = fileReader.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            } catch (IOException e) {
                System.out.println("Erreur lors de la lecture du fichier. Nouvel appel à l'API...");
            }
        } else {
            int numberOfAttempts = 0;

            try {
                URL apiURL = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) apiURL.openConnection();
                connection.setRequestMethod("GET");

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                }

                // Enregistrez la réponse dans le fichier
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write(response.toString());
                } catch (IOException e) {
                    System.out.println("Erreur lors de l'enregistrement des données dans le fichier.");
                }
            } catch (IOException e) {
                numberOfAttempts++;
                if (numberOfAttempts < 3) {
                    System.out.println("Erreur lors de l'appel à l'API " + url + ". Nouvelle tentative...");
                    return appelApi(url);
                } else {
                    System.out.println("Erreur lors de l'appel à l'API. Nombre de tentatives dépassé.");
                }
            }
        }

        return response.toString();
    }
}
