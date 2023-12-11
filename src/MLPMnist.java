import read_mnist.Donnees;

import java.io.IOException;

public class MLPMnist {
    public static void main(String[] args) throws IOException {
        Donnees trainData = new Donnees();
        Donnees testData = new Donnees();

        trainData.loadData("train-images.idx3-ubyte", "train-labels.idx1-ubyte");
        testData.loadData("t10k-images.idx3-ubyte", "t10k-labels.idx1-ubyte");


    }
}
