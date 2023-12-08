package read_mnist;

public class Etiquette {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNbElement() {
        return nbElement;
    }

    public void setNbElement(int nbElement) {
        this.nbElement = nbElement;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    private int id;
    private int nbElement;
    private int number;

    public Etiquette(int id, int nbElement, int number) {
        this.id = id;
        this.nbElement = nbElement;
        this.number = number;
    }
}
