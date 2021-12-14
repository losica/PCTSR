package model;

public class Neighbor {
    private int id;
    private double distance;
    private double profit;
    // the heuristic parameter based on which vertices will be selected when inserting into a path
    private double heuristic;

    public Neighbor(int id, double distance, double profit) {
        this.id = id;
        this.distance = distance;
        this.profit = profit;
        // determined via experiment
        if (profit != 0) this.heuristic = distance / profit;
    }

    // GETTERS

    public int getId() {
        return id;
    }

    public double getDistance() {
        return distance;
    }

    public double getProfit() {
        return profit;
    }

    public double getHeuristic() {
        return heuristic;
    }

    public void useRank(double rank) {
        this.heuristic = this.distance / rank;
    }

    // used for experiments
    public void setHeuristicCoefficient(double coefficient) {
        if (coefficient > 0.0) {
            this.heuristic = (this.distance * coefficient) / (this.profit);
        } else {
            this.heuristic = this.distance;
        }
    }
}