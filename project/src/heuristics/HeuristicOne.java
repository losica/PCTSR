package heuristics;

import model.Neighbor;
import model.PathResult;
import model.Place;

import java.util.ArrayList;
import java.util.List;

public class HeuristicOne extends Heuristic {
    // the smallest coefficient h = distance/profit
    int minHeuristicId = 0;
    int availablePlaces = 0;
    double distance = 0.0;
    double minHeuristic = Double.MAX_VALUE;
    List<ArrayList<Neighbor>> neighborList;
    // fields used for experiments
    private double coefficient = Double.MIN_VALUE;

    public HeuristicOne(double[][] distanceMatrix, Place[] places, int startVertex, int agentsNumber, int minProfit) {
        super(distanceMatrix, places, startVertex, agentsNumber, minProfit);
        this.neighborList = initializeNeighborList();
    }

    // this constructor will be used for experiments
    // the double[] multipliers is used in the Neighbor class to set the ratio between distance and profit
    public HeuristicOne(double[][] distanceMatrix, Place[] places, int startVertex, int agentsNumber, int minProfit, double coefficient) {
        super(distanceMatrix, places, startVertex, agentsNumber, minProfit);
        this.coefficient = coefficient;
        this.neighborList = initializeNeighborList();
    }

    // initialize list of neighbors for each vertex. Will add all the vertices and compute their heuristic h
    private List<ArrayList<Neighbor>> initializeNeighborList() {
        List<ArrayList<Neighbor>> neighborList = new ArrayList<>(places.length);
        for (Place place : places) {
            neighborList.add(place.getId(), new ArrayList<>());
            for (Place placeNeighbor : places) {
                if (place.getId() != placeNeighbor.getId()) { //don't add itself
                    double distance = distanceMatrix[place.getId()][placeNeighbor.getId()];

                    if (place.getFirmProfit() != 0 || place.getId() == this.startVertex) {
                        if (this.coefficient == Double.MIN_VALUE) {
                            neighborList.get(place.getId()).add(new Neighbor(placeNeighbor.getId(), distance, placeNeighbor.getFirmProfit()));
                        } else {
                            // if we have the multipliers we set the coefficient
                            Neighbor neighbor = new Neighbor(placeNeighbor.getId(), distance, placeNeighbor.getFirmProfit());
                            // set heuristic and its coefficient
                            neighbor.setHeuristicCoefficient(this.coefficient);
                            neighborList.get(place.getId()).add(neighbor);
                        }
                    }
                }
            }
        }
        return neighborList;
    }

    // computes and returns the route of each sales representative
    public PathResult[] getResultPaths() {
        initializeAgentsStartingVertex();

        //set the starting vertex as visited
        visited[startVertex] = true;

        // while currently collected profit is smaller than required minimum profit to collect
        while (sumProfit < minProfit) {
            for (int i = 0; i < agentsNumber; i++) {
                for (Neighbor neighbor : neighborList.get(pathResult[i].getActualPlaceID())) {
                    // find a not visited neighbor with the smallest coefficient ((distance*2)/profit)
                    findNonVisitedVertex(neighbor);
                }

                if (availablePlaces == 0) break;
                availablePlaces = 0;

                // update the route of the current agent
                updatePathResult(i);

                //set added vertex as visited
                visited[minHeuristicId] = true;
                minHeuristic = Double.MAX_VALUE;

                // update the sum of profits and check if it's enough
                if (updateSumProfit()) {
                    break;
                }
            }
        }

        //connect generated path with the starting vertex to create a cycle and try to shorten the route with opt2
        connectAndShortenGeneratedPath();

        return pathResult;
    }

    // apply the 2opt algorithm on each agent's routes in order to imrove them
    void connectAndShortenGeneratedPath() {
        for (int i = 0; i < agentsNumber; i++) {
            pathResult[i].getResultPath().add(places[startVertex]);
            pathResult[i].increasePathLength(distanceMatrix[startVertex][pathResult[i].getActualPlaceID()]);
            // will try to optimize the route as long as improvement is made
            boolean useOpt2 = true;
            while (useOpt2) {
                useOpt2 = pathResult[i].opt2();
            }
        }
    }

    // update the total profit collected and check if it's enough
    boolean updateSumProfit() {
        sumProfit += places[minHeuristicId].getFirmProfit();
        return sumProfit >= minProfit;
    }

    // update the PathResult parameters for selected agent
    void updatePathResult(int agentIndex) {
        // update current position (vertex)
        pathResult[agentIndex].setActualPlaceID(minHeuristicId);
        // update the route length
        pathResult[agentIndex].increasePathLength(distance);
        // add the vertex to teh resultPath
        pathResult[agentIndex].getResultPath().add(places[minHeuristicId]);
        // update the profit collected by the agent
        pathResult[agentIndex].increaseActualProfit(places[minHeuristicId].getFirmProfit());
    }

    // find the best non-visited vertex based on our heuristic parameter h (the one with smallest h = distance/profit)
    private void findNonVisitedVertex(Neighbor neighbor) {
        if (neighbor.getProfit() != 0 && !visited[neighbor.getId()] && neighbor.getHeuristic() < minHeuristic) {
            minHeuristic = neighbor.getHeuristic();
            minHeuristicId = neighbor.getId();
            distance = neighbor.getDistance();
            availablePlaces++;
        }
    }

    // initialize each agent's ResultPath
    void initializeAgentsStartingVertex() {
        // add the starting vertex for all the agents
        for (int i = 0; i < agentsNumber; i++) {
            pathResult[i] = new PathResult(distanceMatrix);
            // add starting vertex to the path
            pathResult[i].getResultPath().add(places[startVertex]);
            // set starting vertex as currect location
            pathResult[i].setActualPlaceID(startVertex);
        }
    }
}