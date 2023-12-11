import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class MainMLP {
    // Tableau pour stocker les résultats
    private static ArrayList<String> results = new ArrayList<>();

    // Données d'entraînement et de test inputs
    private static final double[][] trainingInputs = {
            {0, 0},
            {0, 1},
            {1, 0},
            {1, 1}
    };

    // Données d'entraînement et de test pour XOR
    private static final double[][] trainingOutputsXOR = {
            {0},
            {1},
            {1},
            {0}
    };

    // Données d'entraînement et de test pour OR
    private static final double[][] trainingOutputsOR = {
            {0},
            {1},
            {1},
            {1}
    };

    // Données d'entraînement et de test pour AND
    private static final double[][] trainingOutputsAND = {
            {0},
            {0},
            {0},
            {1}
    };

    public static void main(String[] args) {
        results = new ArrayList<>();
        results.add("Réseau de neurones MLP");
        // Définir la structure du réseau
        int[] layers = {2, 2, 1}; // Exemple : 2 neurones en entrée, 2 en couche cachée, 1 en sortie
        double learningRate = 0.01;

        // Tester avec les deux fonctions de transfert
        TransferFunction[] activationFunctions = {new Sigmoide(), new Tanh()};

        for (TransferFunction activationFunction : activationFunctions) {
            results.add("\nFonction de Transfert : " + activationFunction.getClass().getSimpleName());
            MLP mlp = new MLP(layers, learningRate, activationFunction);

            double[][] testInputsXOR = trainingInputs.clone();
            double[][] testOutputsXOR = trainingOutputsXOR.clone();

            // Entraînement et tests pour XOR
            trainAndTest(mlp, "XOR", trainingInputs, trainingOutputsXOR, testInputsXOR, testOutputsXOR);

            double[][] testInputsOR = trainingInputs.clone();
            double[][] testOutputsOR = trainingOutputsOR.clone();

            // Entraînement et tests pour OR
            trainAndTest(mlp, "OR", trainingInputs, trainingOutputsOR, testInputsOR, testOutputsOR);

            double[][] testInputsAND = trainingInputs.clone();
            double[][] testOutputsAND = trainingOutputsAND.clone();

            // Entraînement et tests pour AND
            trainAndTest(mlp, "AND", trainingInputs, trainingOutputsAND, testInputsAND, testOutputsAND);

            System.out.println();
        }
        System.out.println("Résultats :");
        for (String result : results) {
            System.out.println(result);
        }
    }

    // Méthode générique pour l'entraînement et les tests
    private static void trainAndTest(MLP mlp, String tableName, double[][] trainingInputs, double[][] trainingOutputs,
                                     double[][] testInputs, double[][] testOutputs) {
        int numEpochs = 100000;
        int successfulExamples = 0;

        System.out.println("Entraînement pour la table " + tableName + " :");

        // Entraînement
        for (int epoch = 0; epoch < numEpochs; epoch++) {
            double totalError = 0.0;

            // Mélanger les données d'entraînement
            shuffle(trainingInputs, trainingOutputs);

            // Itérer sur chaque exemple d'entraînement
            for (int example = 0; example < trainingInputs.length; example++) {
                double[] input = trainingInputs[example];
                double[] targetOutput = trainingOutputs[example];

                // Forward pass (exécution) et Backward pass (rétropropagation)
                double error = mlp.backPropagate(input, targetOutput);
                totalError += error;
            }

            // Calculer l'erreur moyenne pour cette époque
            double averageError = totalError / trainingInputs.length;

            // Afficher l'erreur moyenne pour le suivi
            System.out.println("Époque " + epoch + ", Erreur Moyenne : " + averageError);

            // Tester régulièrement les résultats
            if (epoch % 1000 == 0) {
                successfulExamples = countSuccessfulExamples(mlp, testInputs, testOutputs);
                System.out.println("Époque " + epoch + ", Table " + tableName + ", Exemples Réussis : " + successfulExamples +
                        " / " + testInputs.length);
            }

            // Tester si tous les exemples sont réussis
            if (successfulExamples == testInputs.length) {
                System.out.println("Tous les exemples sont réussis. Arrêt de l'apprentissage.");
                break;
            }
        }

        // Tester sur les données après l'apprentissage
        results.add("Test final sur la table " + tableName + " :");
        for (int i = 0; i < testInputs.length; i++) {
            double[] input = testInputs[i];
            double[] prediction = mlp.execute(input);
            results.add("Exemple " + Arrays.toString(testInputs[i]) + ", Prédiction : " + Arrays.toString(prediction));
        }
    }

    // Mélanger les données d'entraînement
    private static void shuffle(double[][] inputs, double[][] outputs) {
        Random rand = new Random();
        for (int i = inputs.length - 1; i > 0; i--) {
            int index = rand.nextInt(i + 1);

            // Échanger les entrées
            double[] tempInput = inputs[index];
            inputs[index] = inputs[i];
            inputs[i] = tempInput;

            // Échanger les sorties correspondantes
            double[] tempOutput = outputs[index];
            outputs[index] = outputs[i];
            outputs[i] = tempOutput;
        }
    }

    // Compter le nombre d'exemples réussis
    private static int countSuccessfulExamples(MLP mlp, double[][] inputs, double[][] targets) {
        int count = 0;
        for (int i = 0; i < inputs.length; i++) {
            double[] input = inputs[i];
            double[] target = targets[i];
            double[] prediction = mlp.execute(input);

            // Vérifier si la prédiction correspond à la cible
            boolean success = true;
            for (int j = 0; j < target.length; j++) {
                if (Math.abs(prediction[j] - target[j]) > 0.1) {
                    success = false;
                    break;
                }
            }

            if (success) {
                count++;
            }
        }
        return count;
    }
}
