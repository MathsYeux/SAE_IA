package gps;

import java.util.ArrayList;

public class InitialiseurListeCommunes {

    private static ArrayList<Ville> listeCommunes = new ArrayList<>();

    // Méthode statique pour initialiser la liste des 100 villes les plus peuplées
    public static ArrayList<Ville> initialiserListeCommunes() {
        if(listeCommunes.isEmpty()) {
            System.out.println("Initialisation de la liste des communes...");
            String url = "https://geo.api.gouv.fr/communes?fields=nom,population,centre&format=json&geometry=centre";
            String response = APICaller.appelApi(url);
            System.out.println("Réponse API reçue.");
            listeCommunes = ResponseParser.parseResponse(response);
        }
        return listeCommunes;
    }

    // Vous pouvez ajouter d'autres méthodes ou fonctionnalités à cette classe selon vos besoins
}
