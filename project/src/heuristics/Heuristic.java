package heuristics;

import model.PathResult;
import model.Place;

public abstract class Heuristic {
    // all the companies (vertices)
    Place[] places;
    int startVertex;
    // number of sales representatives
    int agentsNumber;
    // desired profit to be collected by all sales representatives
    int minProfit;
    // results of each agent
    PathResult[] pathResult;
    double[][] distanceMatrix;
    // vertices visited by each agent
    boolean[] visited;
    // total profit collected by all sales representatives
    double sumProfit = 0.0;

    Heuristic(double[][] distanceMatrix, Place[] places, int startVertex, int agentsNumber, int minProfit) {
        this.distanceMatrix = distanceMatrix;
        this.places = places;
        this.startVertex = startVertex;
        this.agentsNumber = agentsNumber;
        this.minProfit = minProfit;
        this.visited = new boolean[places.length];
        this.pathResult = new PathResult[this.agentsNumber];
    }

    // invokes the solution
    public abstract PathResult[] getResultPaths();

    public double getSumProfit() {
        return sumProfit;
    }

    // GETTERS
    public String getMethodName() {
        String name = this.getClass().getName();
        return name.substring(name.indexOf('.') + 1);
    }

    // used to reset the solution results in the experiments when more than one trial runs are made per design point
    public void resetResults() {
        visited = new boolean[places.length];
        pathResult = new PathResult[this.agentsNumber];
        sumProfit = 0.0;
    }
}
