package gps;

import java.util.*;

public class CalculateurItineraire {

    private static final double VITESSE_AUTOROUTE = 130.0;  // en km/h
    private static final double VITESSE_VOIE_RAPIDE = 110.0;  // en km/h
    private static final double VITESSE_ROUTE_DEPARTEMENTALE = 90.0;  // en km/h

    public static LinkedList<String> calculerItineraire(String depart, String arrivee, String typeTrajet) {
        Ville villeDepart = ResponseParser.getVilleByName(depart);
        Ville villeArrivee = ResponseParser.getVilleByName(arrivee);

        System.out.println("Ville de départ : " + villeDepart);
        System.out.println("Ville d'arrivée : " + villeArrivee);
        if (villeDepart != null && villeArrivee != null) {
            // Choisissez le bon graphe en fonction du type de trajet
            Map<Ville, Map<Ville, Double>> graph;
            switch (typeTrajet) {
                case "court":
                    System.out.println("Calcul de l'itinéraire le plus court.");
                    graph = construireGrapheTrajetLePlusCourt();
                    break;
                case "rapide":
                    System.out.println("Calcul de l'itinéraire le plus rapide.");
                    graph = construireGrapheTrajetLePlusRapide();
                    break;
                default:
                    System.out.println("Type de trajet non reconnu.");
                    return new LinkedList<>();
            }

            // Appliquer l'algorithme A*
            graph = ajouterVillesDepartArriveeAuGraphe(graph, villeDepart, villeArrivee);
            return cheminOptimal(graph, villeDepart, villeArrivee, typeTrajet);
        } else {
            System.out.println("Villes de départ ou d'arrivée non trouvées.");
            return new LinkedList<>();
        }
    }

    private static Map<Ville, Map<Ville, Double>> construireGrapheTrajetLePlusCourt() {
        Map<Ville, Map<Ville, Double>> graph = new HashMap<>();

        // Récupérer la liste des 100 villes les plus peuplées
        ArrayList<Ville> villes = InitialiseurListeCommunes.initialiserListeCommunes();
        System.out.println("Nombre de villes : " + villes.size());

        // Construire un graphe complet où chaque ville est reliée à toutes les autres avec la distance comme poids
        for (Ville ville1 : villes) {
            graph.put(ville1, new HashMap<>());
            System.out.println(ville1.getNom());

            for (Ville ville2 : villes) {
                // Ne pas relier une ville à elle-même
                System.out.println(ville1.getNom() + " " + ville2.getNom());
                if (!ville1.equals(ville2)) {
                    double distance = distanceBetweenVilles(ville1, ville2);
                    System.out.println("Distance entre " + ville1.getNom() + " et " + ville2.getNom() + " : " + distance);
                    graph.get(ville1).put(ville2, distance);
                }
            }
        }

        return graph;
    }

    private static Map<Ville, Map<Ville, Double>> construireGrapheTrajetLePlusRapide() {
        Map<Ville, Map<Ville, Double>> graph = new HashMap<>();

        // Récupérer la liste des 100 villes les plus peuplées
        ArrayList<Ville> villes = InitialiseurListeCommunes.initialiserListeCommunes();

        // Construire un graphe complet où chaque ville est reliée à toutes les autres avec le temps de trajet comme poids
        for (Ville ville1 : villes) {
            graph.put(ville1, new HashMap<>());

            for (Ville ville2 : villes) {
                // Ne pas relier une ville à elle-même
                if (!ville1.equals(ville2)) {
                    double tempsTrajet = tempsTrajetEntreVilles(ville1, ville2);
                    graph.get(ville1).put(ville2, tempsTrajet);
                }
            }
        }

        return graph;
    }

    private static double tempsTrajetEntreVilles(Ville ville1, Ville ville2) {
        double vitesseMoyenne = getVitesseMoyenneEntreVilles(ville1, ville2);
        return distanceBetweenVilles(ville1, ville2) / vitesseMoyenne;
    }


    public static Map<Ville, Map<Ville, Double>> ajouterVillesDepartArriveeAuGraphe(Map<Ville, Map<Ville, Double>> graph, Ville villeDepart, Ville villeArrivee) {
        // Assurez-vous que les villes de départ et d'arrivée existent dans le graphe
        if (!graph.containsKey(villeDepart)) {
            graph.put(villeDepart, new HashMap<>());
        }

        if (!graph.containsKey(villeArrivee)) {
            graph.put(villeArrivee, new HashMap<>());
        }

        // Ajouter les voisins de départ au graphe
        Map<Ville, Double> voisinsDepart = new HashMap<>();
        for (Ville ville : graph.keySet()) {
            if (!ville.equals(villeDepart)) {
                double distance = distanceBetweenVilles(villeDepart, ville);
                voisinsDepart.put(ville, distance);
                // Assurez-vous que le voisin est également ajouté dans le graphe
                graph.get(ville).put(villeDepart, distance);
            }
        }
        graph.put(villeDepart, voisinsDepart);

        // Ajouter les voisins d'arrivée au graphe
        Map<Ville, Double> voisinsArrivee = new HashMap<>();
        for (Ville ville : graph.keySet()) {
            if (!ville.equals(villeArrivee)) {
                double distance = distanceBetweenVilles(villeArrivee, ville);
                voisinsArrivee.put(ville, distance);
                // Assurez-vous que le voisin est également ajouté dans le graphe
                graph.get(ville).put(villeArrivee, distance);
            }
        }
        graph.put(villeArrivee, voisinsArrivee);

        return graph;
    }

    private static LinkedList<String> cheminOptimal(Map<Ville, Map<Ville, Double>> graph, Ville depart, Ville arrivee, String typeTrajet) {
        PriorityQueue<Noeud> priorityQueue = new PriorityQueue<>(Comparator.comparingDouble(noeud -> noeud.coutJusquaMaintenant + noeud.coutTotalEstime));
        Map<Ville, Double> coutJusquaMaintenant = new HashMap<>();
        Map<Ville, Ville> predecesseurs = new HashMap<>();

        Noeud departNode = new Noeud(depart, 0, tempsEstimeJusquADestination(depart, arrivee));
        priorityQueue.add(departNode);
        coutJusquaMaintenant.put(depart, 0.0);

        while (!priorityQueue.isEmpty()) {
            Noeud currentNode = priorityQueue.poll();
            Ville currentVille = currentNode.ville;

            System.out.println("Noeud actuel : " + currentVille.getNom());
            if (currentVille.equals(arrivee)) {
                // Destination atteinte, reconstruire le chemin optimal
                return reconstruireChemin(predecesseurs, arrivee);
            }

            for (Ville voisin : graph.get(currentVille).keySet()) {
                double nouveauCout = coutJusquaMaintenant.get(currentVille) + tempsEstimeJusquADestination(currentVille, voisin);
                if (!coutJusquaMaintenant.containsKey(voisin) || nouveauCout < coutJusquaMaintenant.get(voisin)) {
                    coutJusquaMaintenant.put(voisin, nouveauCout);
                    double estimationRestante = tempsEstimeJusquADestination(voisin, arrivee);
                    Noeud voisinNode = new Noeud(voisin, nouveauCout, estimationRestante);
                    priorityQueue.add(voisinNode);
                    predecesseurs.put(voisin, currentVille);

                    System.out.println("   Ajout de voisin : " + voisin.getNom());
                    System.out.println("   Nouveau coût : " + nouveauCout);
                    System.out.println("   Distance restante : " + distanceBetweenVilles(voisin, arrivee));
                }
            }
        }

        // Aucun chemin trouvé
        return null;
    }

    private static double tempsEstimeJusquADestination(Ville villeActuelle, Ville arrivee) {
        double distance = distanceBetweenVilles(villeActuelle, arrivee);
        return distance / getVitesseMoyenneEntreVilles(villeActuelle, arrivee);
    }



    private static LinkedList<String> reconstruireChemin(Map<Ville, Ville> predecesseurs, Ville arrivee) {
        LinkedList<String> chemin = new LinkedList<>();
        Ville courante = arrivee;
        Set<Ville> villesDansChemin = new HashSet<>();

        System.out.println("Reconstruction du chemin...");

        while (courante != null) {
            System.out.println("Ville courante : " + courante.getNom());
            if (villesDansChemin.contains(courante)) {
                System.out.println("Circuit détecté. Arrêt de la reconstruction du chemin.");
                break;
            }

            villesDansChemin.add(courante);  // Déplacez cette ligne après l'ajout dans le chemin
            chemin.addFirst(courante.getNom());
            courante = predecesseurs.get(courante);
        }

        return chemin;
    }




    public static double distanceBetweenVilles(Ville ville1, Ville ville2){
        double rayonTerre = 6371;
        double lat1 = Math.toRadians(ville1.getLatitude());
        double lat2 = Math.toRadians(ville2.getLatitude());
        double lon1 = Math.toRadians(ville1.getLongitude());
        double lon2 = Math.toRadians(ville2.getLongitude());
        double deltaLat = lat2-lat1;
        double deltaLng = lon2-lon1;

        double a = Math.pow(Math.sin(deltaLat/2), 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.pow(Math.sin(deltaLng/2), 2);

        double c = 2*Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return rayonTerre*c;
    }

    private static double getVitesseMoyenneEntreVilles(Ville ville1, Ville ville2) {
        // Autoroute pour les 50 premières villes, Voie rapide pour les 50 suivantes, Route départementale sinon
        ArrayList<Ville> villes = InitialiseurListeCommunes.initialiserListeCommunes();
        if (villes.indexOf(ville1) < 50 && villes.indexOf(ville2) < 50) {
            return VITESSE_AUTOROUTE;
        } else if (villes.indexOf(ville1) < 100 && villes.indexOf(ville2) < 100) {
            return VITESSE_VOIE_RAPIDE;
        } else {
            return VITESSE_ROUTE_DEPARTEMENTALE;
        }
    }
}
