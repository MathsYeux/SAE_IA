package gps;

import com.google.gson.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ResponseParser {

    private static class ProtoVille {
        public String nom;
        public int population;

        public ProtoVille(String nom, int population) {
            this.nom = nom;
            this.population = population;
        }

        public int getPopulation() {
            return population;
        }
    }

    // Méthode pour parser la réponse JSON et obtenir la liste des 100 villes les plus peuplées avec leurs coordonnées GPS
    public static ArrayList<Ville> parseResponse(String jsonResponse) {
        ArrayList<Ville> listeCommunes = new ArrayList<>();

        Gson gson = new Gson();
        JsonArray communesArray = null;

        try{
            communesArray = gson.fromJson(jsonResponse, JsonArray.class);
        }catch (JsonSyntaxException e){
            try {
                // En cas d'échec, essayez de traiter le JSON comme une primitive
                JsonPrimitive primitive = gson.fromJson(jsonResponse, JsonPrimitive.class);

                // Créez un tableau avec la primitive comme valeur
                JsonArray jsonArray = new JsonArray();
                jsonArray.add(primitive);

                // Réessayez de désérialiser en tant qu'objet JSON avec la primitive
                communesArray = gson.fromJson(jsonArray, JsonArray.class);

            } catch (JsonSyntaxException e2) {
                // Si les deux tentatives échouent, imprimez l'erreur et renvoyez null ou lancez une autre exception
                e.printStackTrace();
                return null;
            }
        }

        ArrayList<ProtoVille> protoVilles = new ArrayList<>();
        for (JsonElement element : communesArray) {
            JsonObject communeObject = element.getAsJsonObject();

            if (communeObject.has("population") && communeObject.has("nom")) {
                int population = communeObject.get("population").getAsInt();
                String nom = communeObject.get("nom").getAsString();

                protoVilles.add(new ProtoVille(nom, population));
            }
        }
        System.out.println("Nombre de villes : " + protoVilles.size());

        // Trier la liste des villes par population de manière décroissante
        protoVilles.sort(Comparator.comparingInt(ProtoVille::getPopulation).reversed());

        // On ne garde que les 100 villes le splus peuplées
        List<ProtoVille> villes =  protoVilles.subList(0, Math.min(100, protoVilles.size()));
        for (ProtoVille proto : villes) {
            int population = proto.population;
            double[] coordinates = getCoordinates(proto.nom.replace(" ","+"));
            double latitude = coordinates[0];
            double longitude = coordinates[1];
            Ville ville = new Ville(proto.nom, population, latitude, longitude);
            listeCommunes.add(ville);
        }

        // Trier la liste des communes par population de manière décroissante
        listeCommunes.sort(Comparator.comparingInt(Ville::getPopulation).reversed());
        //System.out.println("Villes triées : " + listeCommunes);

        // Retourner les 100 premières communes
        return new ArrayList<>(listeCommunes.subList(0, Math.min(100, listeCommunes.size())));
    }

    public static double[] getCoordinates(String nomVille) {
        String apiUrl = "https://api-adresse.data.gouv.fr/search/?q=" + nomVille;
        String response = APICaller.appelApi(apiUrl);

        double[] coordinates = new double[2]; // Index 0: Latitude, Index 1: Longitude

        try {
            if (!response.isEmpty()) {
                Gson gson = new GsonBuilder().setLenient().create(); // Utiliser un Gson plus permissif
                JsonArray features = new JsonArray();

                try {
                    JsonObject responseJson = gson.fromJson(response, JsonObject.class);
                    if (responseJson.has("features")) {
                        features = responseJson.getAsJsonArray("features");
                    }
                } catch (JsonSyntaxException e) {
                    // En cas d'échec, essayez de traiter le JSON comme une primitive
                    JsonPrimitive primitive = gson.fromJson(response, JsonPrimitive.class);

                    // Créez un tableau avec la primitive comme valeur
                    JsonArray jsonArray = new JsonArray();
                    jsonArray.add(primitive);

                    // Réessayez de désérialiser en tant qu'objet JSON avec la primitive
                    features = gson.fromJson(jsonArray, JsonArray.class);
                }

                if (!features.isEmpty()) {
                    JsonObject firstFeature = features.get(0).getAsJsonObject();
                    JsonObject geometry = firstFeature.getAsJsonObject("geometry");
                    JsonArray coordinatesArray = geometry.getAsJsonArray("coordinates");

                    coordinates[0] = coordinatesArray.get(0).getAsDouble(); // Longitude
                    coordinates[1] = coordinatesArray.get(1).getAsDouble(); // Latitude

                    return coordinates;
                }
            }
        } catch (Exception e) {
            // En cas d'erreur, imprimez la réponse JSON pour déboguer
            System.err.println("Erreur lors de la lecture du JSON de la ville de " + apiUrl);
        }

        // Si les coordonnées ne peuvent pas être obtenues, renvoyer un tableau avec des valeurs par défaut
        return coordinates;
    }


    // Méthode pour obtenir une Ville à partir de son nom
    public static Ville getVilleByName(String cityName) {
        String url = "https://geo.api.gouv.fr/communes?nom=" + cityName + "&fields=nom,population,centre";
        String response = APICaller.appelApi(url);

        Gson gson = new Gson();
        JsonArray communesArray = gson.fromJson(response, JsonArray.class);

        if (!communesArray.isEmpty()) {
            JsonObject communeObject = communesArray.get(0).getAsJsonObject();
            return parseVilleFromJsonObject(communeObject);
        }

        return null;
    }

    // Méthode pour extraire les informations de la Ville à partir d'un objet JSON
    private static Ville parseVilleFromJsonObject(JsonObject communeObject) {
        if (communeObject.has("population") && communeObject.has("nom") && communeObject.has("centre")) {
            int population = communeObject.get("population").getAsInt();
            String nom = communeObject.get("nom").getAsString();

            // Obtenez les coordonnées GPS
            JsonObject coordinates = communeObject.getAsJsonObject("centre");
            JsonArray coordinatesArray = coordinates.getAsJsonArray("coordinates");

            double latitude = coordinatesArray.get(0).getAsDouble();
            double longitude = coordinatesArray.get(1).getAsDouble();

            return new Ville(nom, population, latitude, longitude);
        }

        return null;
    }
}
