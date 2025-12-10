package TravelingSalesmanGA;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class TravelingSalesman {
    static final int SMALL_GENERATION_SIZE = 800;
    static final int BIG_GENERATION_SIZE = 1600;
    static final int SELECTION_PERCENTAGE = 20;
    static final int ITERATIONS = 5000;
    static final double MUTATION_RATE = 20;
    // count of path lengths to be printed in the final output
    static final int FINAL_RESULTS_COUNT = 10;
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // parsing input
        String dataset = sc.nextLine();
        int n;
        boolean isDatasetInput = false;
        try {
            n = Integer.parseInt(dataset);
        } catch(NumberFormatException e) {
            isDatasetInput = true;
            n = sc.nextInt();
        }

        double[][] cities = new double[n][2];
        String[] cityNames = new String[n];
        for(int i = 0; i < n; i++) cityNames[i] = "";

        if(!isDatasetInput) {
            for(int i = 0; i < n; i++) {
                cities[i][0] = sc.nextInt();
                cities[i][1] = sc.nextInt();
            }
        } else {
            for(int i = 0; i < n; i++) {
                String str = sc.next();
                while(!isDouble(str)) {
                   cityNames[i] += str + " ";
                   str = sc.next();
                }
                cities[i][0] = Double.parseDouble(str);
                str = sc.next();
                cities[i][1] = Double.parseDouble(str);
            }
        }

        // Genetic algorithm
        int[][] generation;
        int generationSize;
        if (n <= 20) generationSize = SMALL_GENERATION_SIZE;
        else generationSize = BIG_GENERATION_SIZE;

        generation = new int[generationSize][n - 1];

        firstGeneration(generation, generationSize, n);
        double[] fitness = new double[generationSize];
        for (int i = 0; i < ITERATIONS; i++) {
            calculateGenerationFitness(cities, generation, fitness, generationSize);
            int[][] parents = rouletteWheelSelection(generation, fitness, generationSize);
            int[][] offsprings = performCrossover(parents, generationSize * (100 - SELECTION_PERCENTAGE) / 100, n);
            mutate(offsprings, n);
            combineParentsAndOffspring(generation, parents, offsprings);
        }

        calculateGenerationFitness(cities, generation, fitness, generationSize);

        // Output
        Integer[] sortingPermutation = getSortingPermutationAscending(fitness);
        for (int i = FINAL_RESULTS_COUNT; i > 0; i--) {
            System.out.println(fitness[sortingPermutation[i]]);
        }
        if (isDatasetInput) {
            System.out.println();
            System.out.print(cityNames[0]);
            for (int i : generation[sortingPermutation[0]]) {
                System.out.print("-> " + cityNames[i]);
            }
            System.out.print("\n" + fitness[sortingPermutation[0]]);
        } else {
            System.out.print("\n" + fitness[sortingPermutation[0]]);
        }
    }

    public static double calculateDistance(double[][] cities, int cityA, int cityB) {
        return Math.sqrt(Math.pow(cities[cityA][0] - cities[cityB][0], 2) +
                            Math.pow(cities[cityA][1] - cities[cityB][1], 2));
    }

    // chromosome is array of size n - 1
    public static double calculateFitness(double[][] cities, int[] chromosome) {
        double fitness = 0;
        fitness += calculateDistance(cities, 0, chromosome[0]);
        for(int i = 0; i < chromosome.length - 1; i++) {
            fitness += calculateDistance(cities, chromosome[i], chromosome[i + 1]);
        }

//        return fitness + calculateDistance(cities, chromosome[chromosome.length - 1], 0);
        return fitness;
    }

    public static void firstGeneration(int[][] generation, int generationSize, int n) {
        for(int j = 0; j < generationSize; j++) {
            for (int i = 1; i < n; i++) {
                generation[j][i - 1] = i;
            }
        }

        // shuffling
        int index;
        int t;
        Random rnd = new Random();
        for(int j = 0; j < generationSize; j++) {
            for (int i = n - 2; i > 0; i--) {
                index = rnd.nextInt(i + 1);
                if (index != i) {
                    t = generation[j][i];
                    generation[j][i] = generation[j][index];
                    generation[j][index] = t;
                }
            }
        }
    }

    public static void calculateGenerationFitness(double[][] cities, int[][] generation, double[] fitness, int generationSize) {
        for(int i = 0; i < generationSize; i++)
            fitness[i] = calculateFitness(cities, generation[i]);
    }

    public static int[][] rouletteWheelSelection(int[][] generation, double[] fitness, int generationSize) {
        double maxFitness = fitness[0];
        for (int i = 1; i < generationSize; i++) {
            if (fitness[i] > maxFitness) maxFitness = fitness[i];
        }

        // calculate total inverse fitness
        double totalInverseFitness = 0;
        for (int i = 0; i < generationSize; i++) {
            totalInverseFitness += maxFitness - fitness[i] + 1;
        }

        int selectionCount = (generationSize * SELECTION_PERCENTAGE) / 100;
        int[][] selectedParents = new int[selectionCount][];

        Random rnd = new Random();
        for (int i = 0; i < selectionCount; i++) {
            double r = rnd.nextDouble() * totalInverseFitness;
            int j = 0;
            double currSum = maxFitness - fitness[0];

            while (currSum < r && j < generationSize - 1) {
                j++;
                currSum += maxFitness - fitness[j] + 1;
            }

            selectedParents[i] = Arrays.copyOf(generation[j], generation[j].length);
        }

        return selectedParents;
    }

    private static int[][] performCrossover(int[][] parents, int crossoverSize, int n) {
        int[][] crossover = new int[crossoverSize][n - 1];
        Random rnd = new Random();
        int ind1, ind2;
        int ind = 0;
        while(ind < crossoverSize) {
            ind2 = rnd.nextInt(1, parents.length);
            ind1 = rnd.nextInt(0, ind2);
            parentsCrossover(parents[ind1], parents[ind2], crossover, ind, n);
            ind++;
        }
        return crossover;
    }

    // Order crossover (OX)
    private static void parentsCrossover(int[] parent1, int[] parent2, int[][] crossover, int index, int n) {
        Random rnd = new Random();
        int crossoverEnd = rnd.nextInt(1, parent1.length);
        int crossoverStart = rnd.nextInt(0, crossoverEnd);
        boolean[] used = new boolean[n - 1];

        for(int i = crossoverStart; i < crossoverEnd; i++) {
            crossover[index][i] = parent1[i];
            used[parent1[i] - 1] = true;
        }
        int ind = 0;
        for(int j = 0; j < parent2.length && ind < parent2.length; j++) {
            if(ind == crossoverStart) ind = crossoverEnd;
            if(ind >= parent2.length) break;
            if(!used[parent2[j] - 1]) {
                crossover[index][ind] = parent2[j];
                ind++;
            }
        }

        index++;
        if(index >= crossover.length) return;
        used = new boolean[n - 1];
        for(int i = crossoverStart; i < crossoverEnd; i++) {
            crossover[index][i] = parent2[i];
            used[parent2[i] - 1] = true;
        }
        ind = 0;
        for(int j = 0; j < parent1.length && ind < parent1.length; j++) {
            if(ind == crossoverStart) ind = crossoverEnd;
            if(ind >= parent2.length) break;
            if(!used[parent1[j] - 1]) {
                crossover[index][ind] = parent1[j];
                ind++;
            }
        }
    }

    public static void mutate(int[][] offsprings, int n) {
        Random rnd = new Random();
        int m;
        for(int i = 0; i < offsprings.length; i++) {
            m = rnd.nextInt(100);
            if(m <= MUTATION_RATE) {
                int b = rnd.nextInt(1, n - 1);
                int a = rnd.nextInt(0, b);
                m = offsprings[i][a];
                offsprings[i][a] = offsprings[i][b];
                offsprings[i][b] = m;
            }
        }
    }

    private static void combineParentsAndOffspring(int[][] generation, int[][] parents, int[][] offsprings) {
        int i = 0;
        while(i < generation.length) {
            if(i < parents.length) {
                generation[i] = parents[i];
            } else {
                generation[i] = offsprings[i - parents.length];
            }
            i++;
        }
    }

    public static boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static Integer[] getSortingPermutationAscending(double[] arr) {
        Integer[] indices = new Integer[arr.length];
        for (int i = 0; i < arr.length; i++) {
            indices[i] = i;
        }
        Arrays.sort(indices, (i, j) -> Double.compare(arr[i], arr[j]));
        return indices;
    }

}
