import read_mnist.Donnees;
import read_mnist.Imagette;

import java.io.IOException;
import java.util.ArrayList;

public class MLPMnist {
    public static void main(String[] args) throws IOException {
        Donnees trainData = new Donnees();
        Donnees testData = new Donnees();

        trainData.loadData("ressource/train-images-idx3-ubyte", "ressource/train-labels-idx1-ubyte");
        testData.loadData("ressource/t10k-images-idx3-ubyte", "ressource/t10k-labels-idx1-ubyte");


        // Tester avec les deux fonctions de transfert
        TransferFunction[] activationFunctions = {new Sigmoide(), new Tanh()};

        double [][] trainInputs = new double[trainData.getImagettes().size()][trainData.getCols() * trainData.getRows()];
        for (int i = 0; i < trainData.getImagettes().size(); i++) {
            Imagette imagette = trainData.getImagettes().get(i);
            for (int j = 0; j < trainData.getCols() * trainData.getRows(); j++) {
                trainInputs[i][j] = imagette.getPixels()[j / trainData.getCols()][j % trainData.getCols()];
            }
        }
        int [] trainOutputs = new int[trainData.getImagettes().size()];
        for (int i = 0; i < trainData.getImagettes().size(); i++) {
            trainOutputs[i] = trainData.getImagettes().get(i).getEtiquette().getNumber();
        }

        for (int i = 0; i < trainData.getImagettes().size(); i++) {
            System.out.println(trainOutputs[i]);
        }

        // Définir la structure du réseau
        int[] layers = {trainData.getCols() * trainData.getRows(), 2, 1}; // Exemple : 2 neurones en entrée, 2 en couche cachée, 1 en sortie
        double learningRate = 0.01;

        for (TransferFunction activationFunction : activationFunctions) {
            System.out.println("\nFonction de Transfert : " + activationFunction.getClass().getSimpleName());

            MLP mlp = new MLP(layers, learningRate, activationFunction);

            // Entraînement et tests pour MNIST


        }

    }
}
