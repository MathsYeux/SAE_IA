package gps;

public class Noeud implements Comparable<Noeud> {
    Ville ville;
    double coutJusquaMaintenant; // g
    double estimationRestante; // h
    double coutTotalEstime; // f

    public Noeud(Ville ville, double coutJusquaMaintenant, double estimationRestante) {
        this.ville = ville;
        this.coutJusquaMaintenant = coutJusquaMaintenant;
        this.estimationRestante = estimationRestante;
        this.coutTotalEstime = coutJusquaMaintenant + estimationRestante;
    }

    @Override
    public int compareTo(Noeud autreNoeud) {
        // Comparaison basée sur le coût total estimé (f)
        return Double.compare(this.coutTotalEstime, autreNoeud.coutTotalEstime);
    }
}
