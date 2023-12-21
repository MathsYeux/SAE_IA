package gps;

// Classe représentant une ville avec son nom, sa population et ses coordonnées GPS
public class Ville {
    private final String nom;
    private final int population;
    private final double latitude;
    private final double longitude;

    public Ville(String nom, int population, double latitude, double longitude) {
        this.nom = nom;
        this.population = population;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getNom() {
        return nom;
    }

    public int getPopulation() {
        return population;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String toString() {
        return nom;
    }
}
