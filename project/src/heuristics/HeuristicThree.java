package heuristics;

import model.Neighbor;
import model.PathResult;
import model.Place;

/**
    This class extends the logic and the computations from HeuristicOne.

    The difference is in the way we add vertices to the path results.
    Instead of iterating through the agents and adding a vertex to every agent's personal result path,
    we only look to find the best vertex to add based on every agent's current position and then add it to the agent it belongs to.
 */

public class HeuristicThree extends HeuristicOne {
    private int bestAgentID = -1;

    public HeuristicThree(double[][] distanceMatrix, Place[] places, int startVertex, int agentsNumber, int minProfit) {
        super(distanceMatrix, places, startVertex, agentsNumber, minProfit);
    }

    @Override
    public PathResult[] getResultPaths() {
        initializeAgentsStartingVertex();

        visited[startVertex] = true;

        initializeAgentOnePath();

        if (updateSumProfit()) {
            return pathResult;
        }

        while (sumProfit < minProfit) {
            for(int i = 0; i < agentsNumber; i++) {
                for (Neighbor neighbor : neighborList.get(pathResult[i].getActualPlaceID())) {
                    // find a not visited neighbor with the smallest coefficient ((distance*2)/profit)
                    findNonVisitedVertex(i, neighbor);
                }
            }

            if (availablePlaces == 0) break;
            availablePlaces = 0;

            updateResults();

            if (updateSumProfit()) {
                break;
            }
        }

        connectAndShortenGeneratedPath();

        return pathResult;
    }

    // update only the results for the best agent
    private void updateResults() {
        updatePathResult(bestAgentID);
        //set added vertex as visited
        visited[minHeuristicId] = true;
        minHeuristic = Double.MAX_VALUE;
        // reset best agent ID
        this.bestAgentID = -1;
    }

    private void findNonVisitedVertex(int agent, Neighbor neighbor) {
        if (neighbor.getProfit() != 0 && !visited[neighbor.getId()] && neighbor.getHeuristic() < minHeuristic) {
            this.bestAgentID = agent;
            minHeuristic = neighbor.getHeuristic();
            minHeuristicId = neighbor.getId();
            distance = neighbor.getDistance();
            availablePlaces++;
        }
    }

    private void initializeAgentOnePath() {
        int firstAgent = 0;

        // add the first best vertex to agent 1
        for (Neighbor neighbor : neighborList.get(pathResult[firstAgent].getActualPlaceID())) {
            findNonVisitedVertex(firstAgent, neighbor);
        }

        if (availablePlaces != 0) {
            updatePathResult(firstAgent);

            visited[minHeuristicId] = true;
            minHeuristic = Double.MAX_VALUE;
        }

        availablePlaces = 0;
    }
}
