package experiments;

import heuristics.Heuristic;
import heuristics.HeuristicTwo;
import model.PathResult;

import java.util.Locale;

public abstract class HeuristicTwoExperiment extends Experiment {

    HeuristicTwoExperiment(String fileName) {
        super(fileName);
    }

    @Override
    public abstract void run();

    void runCoefficientsExperiments(int agents, int kmax, int percent, double removeOperationRatio) {
        for (int i = 0; i < PROFITS.length; i += 5) {
            int profit = PROFITS[i];

            calculateResults(agents, profit, kmax, percent, removeOperationRatio);

            double distanceResult = calculateAverageDistance();

            // print row with test result
            printRow(String.format(Locale.US,"%d,%d,%d,%.2f,%d,%.2f", agents, kmax, percent, removeOperationRatio, profit, distanceResult));

            // re-instantiate the distance array in the parent class
            resetDistanceArray();
        }
    }

    // allows performing multiple runs per starting vertex in order to take the average when using random
    void runCoefficientsExperiments(int agents, int kmax, int percent, double removeOperationRatio, int numberOfRuns) {
        for (int i = 0; i < PROFITS.length; i += 5) {
            int profit = PROFITS[i];
            calculateResults(agents, profit, kmax, percent, removeOperationRatio, numberOfRuns);

            double distanceResult = calculateAverageDistance();

            // print row with test result
            printRow(String.format(Locale.US,"%d,%d,%d,%.2f,%d,%.2f", agents, kmax, percent, removeOperationRatio, profit, distanceResult));

            // re-instantiate the distance array in the parent class
            resetDistanceArray();
        }
    }

    private void calculateResults(int agents, int profit, int kmax, int percent, double removeOperationRatio) {
        // calculate results from every starting vertex and take the average
        for (int i = 0; i < START_VERTICES.length; i++) {
            int startV = START_VERTICES[i];
            Heuristic h = new HeuristicTwo(distanceMatrix, places, startV, agents, profit, kmax, percent, removeOperationRatio);
            calculateDistanceResults(h, i);
        }
    }

    // allows performing multiple runs per starting vertex in order to take the average when using random
    private void calculateResults(int agents, int profit, int kmax, int percent, double removeOperationRatio, int numberOfRuns) {
        // calculate results from every starting vertex and take the average
        for (int i = 0; i < START_VERTICES.length; i++) {
            int startV = START_VERTICES[i];
            Heuristic h = new HeuristicTwo(distanceMatrix, places, startV, agents, profit, kmax, percent, removeOperationRatio);
            calculateDistanceResults(h, i, numberOfRuns);
        }
    }

    private void calculateDistanceResults(Heuristic h, int index, int numberOfRuns) {
        double[] routeDistanceResults = new double[numberOfRuns];

        for (int i = 0; i < numberOfRuns; i++) {
            PathResult[] resultPath = h.getResultPaths();
            // sum the total path length
            double routeDistance = 0d;
            for (PathResult res : resultPath) {
                routeDistance += res.getPathLength();
            }
            routeDistanceResults[i] = routeDistance;
            h.resetResults();
        }

        double totalDistance = 0d;
        for (double distance : routeDistanceResults) {
            totalDistance += distance;
        }

        distanceResults[index] = totalDistance / numberOfRuns;
    }
}
