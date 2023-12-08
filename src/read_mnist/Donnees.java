package read_mnist;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Donnees {
    private ArrayList<Imagette> imagettes;

    private ArrayList<Etiquette> etiquettes;
    public Donnees(ArrayList<Imagette> imagettes, ArrayList<Etiquette> etiquettes) {
        this.imagettes = imagettes;
        this.etiquettes = etiquettes;
    }
    public Donnees(){
        this.imagettes = new ArrayList<>();
        this.etiquettes = new ArrayList<>();
    }

    public void loadData(String path,String etiquettePath) throws IOException {
        DataInputStream dis = new DataInputStream(new FileInputStream(path));

        int id = dis.readUnsignedByte() << 24 | dis.readUnsignedByte() << 16 | dis.readUnsignedByte() << 8 | dis.readUnsignedByte();
        int numImages = dis.readUnsignedByte() << 24 | dis.readUnsignedByte() << 16 | dis.readUnsignedByte() << 8 | dis.readUnsignedByte();
        int numRows =dis.readUnsignedByte() << 24 | dis.readUnsignedByte() << 16 | dis.readUnsignedByte() << 8 | dis.readUnsignedByte();
        int numCols = dis.readUnsignedByte() << 24 | dis.readUnsignedByte() << 16 | dis.readUnsignedByte() << 8 | dis.readUnsignedByte();

        ArrayList<Imagette> imagettes = new ArrayList<>();
        for (int i = 0; i < numImages; i++) {
            int[][] pixels = new int[numRows][numCols];
            for (int row = 0; row < numRows; row++) {
                for (int col = 0; col < numCols; col++) {
                    pixels[row][col] = dis.readUnsignedByte();
                }
            }
            Imagette imagette = new Imagette(pixels);
            imagettes.add(imagette);
        }
        // Ajouter etiquette
        ArrayList<Etiquette> etiquettes = ListEtiquette.listerEtiquette(etiquettePath);
        if (etiquettes.size() != imagettes.size()){
            System.out.println(etiquettes.size());
            System.out.println(imagettes.size());
            throw new IOException("Nombre d'etiquette different du nombre d'imagette");
        }
        for (int i = 0; i < etiquettes.size() ; i++) {
            imagettes.get(i).setEtiquette(etiquettes.get(i));
        }
        this.imagettes = imagettes;
        this.etiquettes = etiquettes;
    }

    public ArrayList<Imagette> getImagettes() {
        return imagettes;
    }

    public void setImagettes(ArrayList<Imagette> imagettes) {
        this.imagettes = imagettes;
    }

    public ArrayList<Etiquette> getEtiquettes() {
        return etiquettes;
    }

    public void setEtiquettes(ArrayList<Etiquette> etiquettes) {
        this.etiquettes = etiquettes;
    }

}
