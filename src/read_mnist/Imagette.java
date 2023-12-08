package read_mnist;

public class Imagette {
    private int[][] pixels;

    int[][] getPixels() {
        return pixels;
    }

    private Etiquette etiquette;

    public Imagette(int[][] pixels) {
        this.pixels = pixels;
    }

    private void setPixels(int[][] pixels) {
        this.pixels = pixels;
    }

    Etiquette getEtiquette() {
        return etiquette;
    }

    void setEtiquette(Etiquette etiquette) {
        this.etiquette = etiquette;
    }

}