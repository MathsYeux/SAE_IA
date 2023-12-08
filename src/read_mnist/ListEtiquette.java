package read_mnist;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ListEtiquette {
    public static ArrayList<Etiquette> listerEtiquette(String filepath) throws IOException {
        DataInputStream dis = new DataInputStream(new FileInputStream(filepath));
        ArrayList<Etiquette> res = new ArrayList<>();

        int id = dis.readUnsignedByte() << 24 | dis.readUnsignedByte() << 16 | dis.readUnsignedByte() << 8 | dis.readUnsignedByte();
        int nbElement = dis.readUnsignedByte() << 24 | dis.readUnsignedByte() << 16 | dis.readUnsignedByte() << 8 | dis.readUnsignedByte();
        for (int i = 0; i < nbElement; i++) {
            int number = dis.readUnsignedByte();
            Etiquette etiquette = new Etiquette(id, nbElement, number);
            res.add(etiquette);
        }

        return res;
    }

}
