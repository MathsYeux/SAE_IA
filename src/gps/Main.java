package gps;

import java.util.LinkedList;

public class Main {
    public static void main(String[] args) {
        String villeDepart = "Hyeres";
        String villeArrivee = "Saint-Malo";
        String typeTrajet = "rapide";
        if(args.length >= 2) {
            villeDepart = args[0];
            villeArrivee = args[1];
            if (args.length == 3) {
                typeTrajet = args[2];
            }
        }
        LinkedList<String> itineraire = CalculateurItineraire.calculerItineraire(villeDepart, villeArrivee, typeTrajet);
        System.out.println("Itinéraire : " + itineraire);
    }
}
