package test;

import heuristics.*;
import model.*;
import reader.*;

public class HeuristicTest {
    private DataReader reader = new DataReader();

    public void runTests() {
        double[][] matrix = reader.getDistanceMatrix();
        Place[] places = reader.getAllCompanies();
        int vertex = 0;
        int agents = 1;
        int profit = 10;

        double P = 0d;
        for (Place place : places) {
            P += place.getFirmProfit();
        }

        final int V = places.length;
        final int A = 10;

        // we want to run each heuristic solution from each starting vertex, with different number of agents and different profit to be collected
        while (vertex < V) {
            final int MAX_P = (int) (P - places[vertex].getFirmProfit());
            while (agents < A) {
                while (profit < MAX_P) {
                    Heuristic[] H = new Heuristic[]{
                            new HeuristicOne(matrix, places, vertex, agents, profit),
                            new HeuristicTwo(matrix, places, vertex, agents, profit),
                            new HeuristicThree(matrix, places, vertex, agents, profit),
                            new HeuristicFour(matrix, places, vertex, agents, profit)
                    };

                    for (Heuristic h : H) {
                        PathResult[] pr = h.getResultPaths();
                        double totalProfit = getTotalProfit(pr);
                        assert totalProfit >= profit;
                        if (profit >= 290) {
                            evaluatePathResults(pr, !h.getMethodName().equals("HeuristicThree"));
                        }
                    }
                    profit += (profit < 250) ? 10 : 5;
                }
                profit = 10;
                agents++;
            }
            agents = 1;
            vertex++;
        }
    }

    private void evaluatePathResults(PathResult[] pr, boolean notH3) {
        for (PathResult p : pr) {
            if (notH3) {
                // if the solutions is not HeuristicThree, each agent should be utilized and should bring some profit
                assert p.getResultPath().size() > 2 && p.getActualProfit() > 0;
            } else {
                // in HeuristicThree, if an agent is not utilized, his/her profit should be 0 and visited places should be 2 (just the starting vertex added twice)
                assert (p.getResultPath().size() > 2 && p.getActualProfit() > 0) || (p.getActualProfit() == 0 && p.getResultPath().size() == 2);
            }
        }
    }

    private double getTotalProfit(PathResult[] pr) {
        double res = 0d;
        for (PathResult p : pr) {
            res += p.getActualProfit();
        }
        return res;
    }
}
