package experiments;

import heuristics.*;
import model.PathResult;
import model.Place;

import java.util.Locale;
import java.util.Random;

public class HeuristicComparison extends Experiment {
    private int NUM_RUNS = 100;
    private final int NUM_V = START_VERTICES.length;
    private long[] timeResults = new long[NUM_V];
    private double totalProfit;

    public HeuristicComparison(String fileName) {
        super(fileName);
        this.totalProfit = calculateTotalProfit();
    }

    public void run() {
        // warm up the compiler
        warmUp();

        // print the header row for the csv
        printRow(String.format("%s,%s,%s,%s,%s", "heuristic", "agents", "profit", "time (ns)", "distance (km)"));

        // run with HeuristicOne and Two
        runExperiment("one");
        runExperiment("two");
        runExperiment("three");
        runExperiment("four");

        // close the print writer
        endExperiment();
    }

    // runs the experiment with the specified heuristic method
    private void runExperiment(String heuristic) {
        for (int agent : AGENTS) {
            for (int profit : PROFITS) {
                for (int i = 0; i < NUM_V; i++) {
                    int startV = START_VERTICES[i];
                    calculateResults(heuristic, startV, agent, profit, i);
                }
                writeResultsAndReset(heuristic, agent, profit);
            }
            runWithMaxProfit(agent, heuristic);
        }
    }

    private void runWithMaxProfit(int agent, String heuristic) {
        for (int i = 0; i < NUM_V; i++) {
            int startV = START_VERTICES[i];
            // calculate the max profit we can collect from the starting vertex
            int profit = (int) (this.totalProfit - this.places[startV].getFirmProfit());

            calculateResults(heuristic, startV, agent, profit, i);
        }

        // 333 profit corresponds to the max profit we can collect from every vertex
        writeResultsAndReset(heuristic, agent, 333);
    }

    // writes results to the output file and resets the time and distance arrays
    private void writeResultsAndReset(String heuristic, int agent, int profit) {
        double distance = calculateAverageDistance();
        long time = 0;
        for (long timeRes : timeResults) {
            time += timeRes;
        }

        // print the results on the output file
        printRow(String.format(Locale.US, "%s,%d,%d,%d,%.2f", heuristic, agent, profit, (time / NUM_V), (distance / 1000)));

        // reset the two result arrays
        resetResultArrays();
    }

    private void calculateResults(String heuristic, int startV, int agent, int profit, int resultIndex) {
        Heuristic h = null;
        // init heuristic method
        switch (heuristic) {
            case "one":
                h = new HeuristicOne(distanceMatrix, places, startV, agent, profit);
                break;
            case "two":
                h = new HeuristicTwo(distanceMatrix, places, startV, agent, profit);
                break;
            case "three":
                h = new HeuristicThree(distanceMatrix, places, startV, agent, profit);
                break;
            case "four":
                h = new HeuristicFour(distanceMatrix, places, startV, agent, profit);
                break;
        }

        fillTimeAndDistanceResults(h, resultIndex);
    }

    private void fillTimeAndDistanceResults(Heuristic h, int idx) {
        long totalRunTime = 0L;
        double totalRunDistance = 0d;
        for (int i = 0; i < NUM_RUNS; i++) {
            // get execution time
            long time = System.nanoTime();
            PathResult[] resultPath = h.getResultPaths();

            // save time results
            time = System.nanoTime() - time;

            // sum the total path length
            double totalDistance = 0d;
            for (PathResult res : resultPath) {
                totalDistance += res.getPathLength();
            }

            //save results to local variables
            totalRunTime += time;
            totalRunDistance += totalDistance;

            //reset Heuristic results
            h.resetResults();
        }

        // fill the results
        timeResults[idx] = totalRunTime / NUM_RUNS;
        distanceResults[idx] = totalRunDistance / NUM_RUNS;
    }

    // reset the arrays with all the results
    private void resetResultArrays() {
        // re-instantiate the distance array in the parent class
        resetDistanceArray();
        timeResults = new long[NUM_V];
    }

    private double calculateTotalProfit() {
        double res = 0;

        for (Place p : places) {
            res += p.getFirmProfit();
        }

        return res;
    }

    // HELPERS TO WARM UP THE COMPILER. NOT PART OF THE ACTUAL EXPERIMENT

    private void warmUp() {
        Random rnd = new Random();
        int tmp = 0;

        for (int i = 0; i < 10_000; i++) {
            int profit = rnd.nextInt(299);
            int startV = rnd.nextInt(90);
            int agents = rnd.nextInt(9);

            Heuristic h = new HeuristicOne(distanceMatrix, places, startV, (agents + 1), profit);
            tmp += sumWarmUpResults(h.getResultPaths());

            h = new HeuristicTwo(distanceMatrix, places, startV, (agents + 1), profit);
            tmp -= sumWarmUpResults(h.getResultPaths());

            h = new HeuristicThree(distanceMatrix, places, startV, (agents + 1), profit);
            tmp += sumWarmUpResults(h.getResultPaths());

            h = new HeuristicFour(distanceMatrix, places, startV, (agents + 1), profit);
            tmp -= sumWarmUpResults(h.getResultPaths());
        }
    }

    private int sumWarmUpResults(PathResult[] results) {
        int tmp = 0;
        for (PathResult res : results) {
            tmp += res.getPathLength();
        }
        return tmp;
    }
}