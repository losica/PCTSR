package model;

import java.util.ArrayList;
import java.util.List;

public class PathResult {
    // list with all the visited companies from an sales representative
    private List<Place> resultPath;
    // length of the sales representative's route
    private double pathLength;
    // the collected profit
    private double actualProfit;
    // the vertex the sales representative is currently on
    private int actualPlaceID;
    // distanes based on the starting point
    private double[][] neighborsMatrix;

    // for HeuristicTwo
    private double previousMinLength;
    private List<Place> previousMinPlaces;
    private double previousMaxProfit;

    public PathResult(double[][] neighborsMatrix) {
        this.neighborsMatrix = neighborsMatrix;
        this.resultPath = new ArrayList<>();
        this.pathLength = 0;
        this.actualProfit = 0;
        this.actualPlaceID = 0;
        this.previousMinPlaces = new ArrayList<>();

    }

    // the 2opt algorithm for improving a route
    public boolean opt2() {
        double minus, plus;
        int n = this.resultPath.size();
        for (int i = 0; i < n - 2; i++) {
            for (int j = i + 2; j < n - 1; j++) {
                //check the distances of edges before and after possible exchange
                //minus - sum of edges (i, i+1) and (j, j+1) (before possible exchange)
                //plus - sum of edges (i, j) and (i+1, j+1) (after possible exchange)
                minus = neighborsMatrix[resultPath.get(i).getId()][resultPath.get(i + 1).getId()] + neighborsMatrix[resultPath.get(j).getId()][resultPath.get(j + 1).getId()];
                plus = neighborsMatrix[resultPath.get(i).getId()][resultPath.get(j).getId()] + neighborsMatrix[resultPath.get(i + 1).getId()][resultPath.get(j + 1).getId()];
                // check if exchange of edges (i,i+1) (j, j+1) into edges (i,j) (i+1, j+1) shortens the route
                if (plus < minus) {
                    exchangeEdges(i, j);
                    //update the path length
                    double diff = minus - plus;
                    this.pathLength -= diff;
                    return true;
                }
            }
        }

        // 2opt correction not found
        return false;
    }

    //exchange (i, i+1) and (j, j+1) to (i,j) (i+1, j+1)
    private void exchangeEdges(int i, int j) {
        List<Place> newPlaces = new ArrayList<>();
        int pathSize = this.resultPath.size();
        //add vertices from starting vertex to vertex i
        for (int k = 0; k <= i; k++) {
            newPlaces.add(resultPath.get(k));
        }
        //add vertices from vertex j to vertex i+1
        for (int k = j; k >= i + 1; k--) {
            newPlaces.add(resultPath.get(k));
        }
        //add vertices from vertex j+1 to the the starting vertex
        for (int k = j + 1; k <= pathSize - 1; k++) {
            newPlaces.add(resultPath.get(k));
        }
        this.resultPath = newPlaces;
    }

    public String getPathDescription() {
        String pathDescription = "";
        for (int i = 0; i < resultPath.size(); i++) {
            Place p = resultPath.get(i);
            if (i == 0 || i == resultPath.size() - 1) {
                pathDescription += p.getShortDescription() + "\n\n";
            } else {
                pathDescription += p.getDescription() + "\n\n";
            }
        }
        return pathDescription;
    }

    // HELPER FUNCTIONS. GETTERS & SETTERS

    public List<Place> getResultPath() {
        return resultPath;
    }

    public void setResultPath(List<Place> resultPath) {
        this.resultPath = resultPath;
    }

    public int getActualPlaceID() {
        return actualPlaceID;
    }

    public void setActualPlaceID(int actualPlaceID) {
        this.actualPlaceID = actualPlaceID;
    }

    public void increaseActualProfit(double amountToIncrease) {
        this.actualProfit += amountToIncrease;
    }

    public void increasePathLength(double distanceAdded) {
        this.pathLength += distanceAdded;
    }

    public double getPathLength() {
        return pathLength;
    }

    public void setPathLength(double pathLength) {
        this.pathLength = pathLength;
    }

    public double getActualProfit() {
        return actualProfit;
    }

    public void setActualProfit(double actualProfit) {
        this.actualProfit = actualProfit;
    }

    public List<Place> getPreviousMinPlaces() {
        return previousMinPlaces;
    }

    public void setPreviousMinPlaces(List<Place> previousMinPlaces) {
        this.previousMinPlaces = previousMinPlaces;
    }

    public double getPreviousMaxProfit() {
        return previousMaxProfit;
    }

    public void setPreviousMaxProfit(double previousMaxProfit) {
        this.previousMaxProfit = previousMaxProfit;
    }

    public double getPreviousMinLength() {
        return previousMinLength;
    }

    public void setPreviousMinLength(double previousMinLength) {
        this.previousMinLength = previousMinLength;
    }
}