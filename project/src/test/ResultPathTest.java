package test;

import heuristics.*;
import model.PathResult;
import model.Place;
import reader.DataReader;

import java.util.Arrays;
import java.util.List;

public class ResultPathTest {

    public void getResultPath(String heuristic, int startVertex, int agentsNumber, int minProfit) {
        String method = heuristic.toLowerCase();

        if (validateInput(method, startVertex, agentsNumber, minProfit)) {
            DataReader reader = new DataReader();
            double[][] distanceMatrix = reader.getDistanceMatrix();
            Place[] places = reader.getAllCompanies();
            Heuristic h = null;

            switch (method) {
                case "one":
                    h = new HeuristicOne(distanceMatrix, places, startVertex, agentsNumber, minProfit);
                    break;
                case "two":
                    h = new HeuristicTwo(distanceMatrix, places, startVertex, agentsNumber, minProfit);
                    break;
                case "three":
                    h = new HeuristicThree(distanceMatrix, places, startVertex, agentsNumber, minProfit);
                    break;
                case "four":
                    h = new HeuristicFour(distanceMatrix, places, startVertex, agentsNumber, minProfit);
                    break;
            }

            printResults(h.getMethodName(), h.getResultPaths());
        } else {
            System.out.println("## Wrong input ##");
            System.exit(0);
        }
    }

    private boolean validateInput(String method, int startVertex, int agentsNumber, int minProfit) {
        String[] heuristics = new String[] { "one", "two", "three", "four" };
        return Arrays.asList(heuristics).contains(method) &&
                (startVertex >= 0 && startVertex <= 90) &&
                (agentsNumber > 0 && agentsNumber <= 10) &&
                (minProfit >= 5 && minProfit < 300);
    }

    private void printResults(String method, PathResult[] results) {
        printResultHeader(method);
        int agent = 0;
        for (PathResult pr : results) {
            System.out.println("\n Result for agent #" + agent++);
            printResultPath(pr.getResultPath());
        }
        printResultFooter(getTotalLength(results), getTotalProfitCollected(results));
    }

    private void printResultFooter(double length, double profit) {
        System.out.printf("%n%s %.2f", "Total length:", length);
        System.out.printf("%n%s %.2f%n", "Total profit:", profit);
        System.out.println("\n########################");
        System.out.println("########################\n\n");
    }

    private void printResultHeader(String method) {
        System.out.println("\n########################");
        System.out.println("> " + method + " Results <");
        System.out.println("########################");
    }

    private double getTotalLength(PathResult[] results) {
        double length = 0.0;
        for(PathResult res: results) {
            length += res.getPathLength();
        }
        return length;
    }

    private double getTotalProfitCollected(PathResult[] results) {
        double profit = 0.0;
        for(PathResult res: results) {
            profit += res.getActualProfit();
        }
        return profit;
    }

    private void printResultPath(List<Place> places) {
        System.out.println(places.toString().replaceAll("\\[", "Start").replaceAll("\\]", "\nEnd"));
    }
}
