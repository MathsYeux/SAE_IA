import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

class Table {
    double[][] inputs;
    double[][] outputs;

    Table(double[][] inputs, double[][] outputs) {
        this.inputs = inputs;
        this.outputs = outputs;
    }
}

public class MainMLP {
    // Tableau pour stocker les résultats
    private static ArrayList<String> results = new ArrayList<>();

    // Données d'entraînement et de test pour XOR
    private static final Table xorTable = new Table(
            new double[][]{{0, 0}, {0, 1}, {1, 0}, {1, 1}},
            new double[][]{{0}, {1}, {1}, {0}}
    );

    // Données d'entraînement et de test pour OR
    private static final Table orTable = new Table(
            new double[][]{{0, 0}, {0, 1}, {1, 0}, {1, 1}},
            new double[][]{{0}, {1}, {1}, {1}}
    );

    // Données d'entraînement et de test pour AND
    private static final Table andTable = new Table(
            new double[][]{{0, 0}, {0, 1}, {1, 0}, {1, 1}},
            new double[][]{{0}, {0}, {0}, {1}}
    );

    public static void main(String[] args) {
        results = new ArrayList<>();
        results.add("Réseau de neurones MLP");

        // Tester avec les deux fonctions de transfert
        TransferFunction[] activationFunctions = {new Sigmoide(), new Tanh()};

        for (TransferFunction activationFunction : activationFunctions) {
            results.add("\nFonction de Transfert : " + activationFunction.getClass().getSimpleName());

            Table xorTableCopy = xorTable;
            Table orTableCopy = orTable;
            Table andTableCopy = andTable;

            if(activationFunction instanceof Tanh) {
                xorTableCopy = new Table(
                        convertZerosToMinusOne(xorTable.inputs),
                        convertZerosToMinusOne(xorTable.outputs)
                );

                orTableCopy = new Table(
                        convertZerosToMinusOne(orTable.inputs),
                        convertZerosToMinusOne(orTable.outputs)
                );

                andTableCopy = new Table(
                        convertZerosToMinusOne(andTable.inputs),
                        convertZerosToMinusOne(andTable.outputs)
                );
            }

            // Entraînement et tests pour XOR avec architecture {2,4,4,1} et learningRate 3.0
            MLP mlp1 = new MLP(new int[]{2, 4, 4, 1}, 1.5, activationFunction);
            trainAndTest(mlp1, "XOR", xorTableCopy, activationFunction);

            // Entraînement et tests pour OR avec architecture {2,1} et learningRate 0.3
            MLP mlp2 = new MLP(new int[]{2, 1}, 0.3, activationFunction);
            trainAndTest(mlp2, "OR", orTableCopy, activationFunction);

            // Entraînement et tests pour AND avec architecture {2,1} et learningRate 0.3
            MLP mlp3 = new MLP(new int[]{2, 1}, 0.3, activationFunction);
            trainAndTest(mlp3, "AND", andTableCopy, activationFunction);

            System.out.println();
        }
        System.out.println("Résultats :");
        for (String result : results) {
            System.out.println(result);
        }
    }

    // Méthode générique pour l'entraînement et les tests
    private static void trainAndTest(MLP mlp, String tableName, Table table, TransferFunction activationFunction) {
        int numEpochs = 100000;
        int successfulExamples = 0;

        System.out.println("Entraînement pour la table " + tableName + " :");

        // Entraînement
        for (int epoch = 0; epoch < numEpochs; epoch++) {
            double totalError = 0.0;

            // Mélanger les données d'entraînement
            shuffle(table.inputs, table.outputs);

            // Itérer sur chaque exemple d'entraînement
            for (int example = 0; example < table.inputs.length; example++) {
                double[] input = table.inputs[example];
                double[] targetOutput = table.outputs[example];

                // Forward pass (exécution) et Backward pass (rétropropagation)
                double error = mlp.backPropagate(input, targetOutput);
                totalError += error;
            }

            // Calculer l'erreur moyenne pour cette époque
            double averageError = totalError / table.inputs.length;

            // Afficher l'erreur moyenne pour le suivi
            //System.out.println("Époque " + epoch + ", Erreur Moyenne : " + averageError);

            // Tester régulièrement les résultats
            if (epoch % 1000 == 0) {
                successfulExamples = countSuccessfulExamples(mlp, table, activationFunction);
                System.out.println("Époque " + epoch + ", Table " + tableName + ", Exemples Réussis : " + successfulExamples +
                        " / " + table.inputs.length);
            }

            // Tester si tous les exemples sont réussis
            if (successfulExamples == table.inputs.length) {
                System.out.println("Tous les exemples sont réussis. Arrêt de l'apprentissage.");
                break;
            }
        }

        // Trier les exemples avant de les afficher
        sortExamples(table);

        // Tester sur les données après l'apprentissage
        results.add("Test final sur la table " + tableName + " :");
        for (int i = 0; i < table.inputs.length; i++) {
            double[] input = table.inputs[i];
            double[] prediction = mlp.execute(input);
            results.add("Exemple " + Arrays.toString(table.inputs[i]) + ", Prédiction : " + Arrays.toString(prediction));
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
    private static int countSuccessfulExamples(MLP mlp, Table table, TransferFunction activationFunction) {
        int count = 0;
        for (int i = 0; i < table.inputs.length; i++) {
            double[] input = table.inputs[i];
            double[] target = table.outputs[i];
            double[] prediction = mlp.execute(input);

            boolean success;

            if (activationFunction instanceof Sigmoide) {
                success = isSuccessSigmoide(target, prediction);
            } else if (activationFunction instanceof Tanh) {
                success = isSuccessTanh(target, prediction);
            } else {
                System.out.println("Erreur : Fonction de transfert non reconnue.");
                System.exit(1);
                return 0; // Ajout pour éviter l'erreur de compilation (ce chemin du code ne devrait jamais être atteint)
            }

            if (success) {
                count++;
            }
        }
        return count;
    }

    private static boolean isSuccessSigmoide(double[] target, double[] prediction) {
        // Vérifier si la prédiction correspond à la cible
        boolean success = true;
        for (int j = 0; j < target.length; j++) {
            if (Math.abs(prediction[j] - target[j]) > 0.1) {
                success = false;
                break;
            }
        }
        return success;
    }

    private static boolean isSuccessTanh(double[] target, double[] prediction) {
        // Vérifier si les signes des prédictions correspondent aux signes des cibles
        boolean success = true;
        for (int j = 0; j < target.length; j++) {
            if ((prediction[j] > 0.0 && target[j] < 0.0) || (prediction[j] < 0.0 && target[j] > 0.0)) {
                success = false;
                break;
            }
        }
        return success;
    }


    public static double[][] convertZerosToMinusOne(double[][] inputs) {
        double[][] convertedInputs = new double[inputs.length][inputs[0].length];
        for (int i = 0; i < inputs.length; i++) {
            double[] input = inputs[i];
            for (int j = 0; j < input.length; j++) {
                if (input[j] == 0.0) {
                    convertedInputs[i][j] = -1.0;
                } else {
                    convertedInputs[i][j] = input[j];
                }
            }
        }
        return convertedInputs;
    }

    // Trier les exemples pour l'affichage
    private static void sortExamples(Table table) {
        Arrays.sort(table.inputs, Comparator.comparing(Arrays::toString));
        Arrays.sort(table.outputs, Comparator.comparing(Arrays::toString));
    }
}
