package read_mnist;

public class Imagette {
    private int[][] pixels;

    private Etiquette etiquette;

    public Imagette(int[][] pixels) {
        this.pixels = pixels;
    }

    public int[][] getPixels() {
        return pixels;
    }

    public Etiquette getEtiquette() {
        return etiquette;
    }

    private void setPixels(int[][] pixels) {
        this.pixels = pixels;
    }

    void setEtiquette(Etiquette etiquette) {
        this.etiquette = etiquette;
    }

}