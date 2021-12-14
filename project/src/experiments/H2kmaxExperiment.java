package experiments;

public class H2kmaxExperiment extends HeuristicTwoExperiment {

    // when randomCoeff is true then we remove random number of the most profitable vertices to remove
    // else we remove (0.3 * number of vertices to remove) the most profitable vertices, and the rest
    // vertices to remove is based on (distance shortened by the vertex removal/profit of the vertex to remove) removeOperationRatio
    // NOTE : 0.3 is our initial default value for the percentage of vertices to remove
    private boolean randomCoeff;

    public H2kmaxExperiment(String filename, boolean randomCoeff) {
        super(filename);
        this.randomCoeff = randomCoeff;
    }

    @Override
    public void run() {
        // print header row of the csv file
        printRow(String.format("%s,%s,%s,%s,%s,%s", "agents", "kmax", "percent", "ratio", "profit", "distance"));
        // run experiment
        runKmaxExperiment();
        // close the writer and end the experiment
        endExperiment();
    }

    private void runKmaxExperiment() {
        for (int agents : AGENTS) {
            for (int kmax = 1; kmax <= 40; kmax += 2) {
                // the modificationPercent will have the default random value
                int percent = -1;
                double removeOperationRatio;
                if (this.randomCoeff) {
                    removeOperationRatio = -1;
                } else {
                    removeOperationRatio = 0.3;
                }
                // run experiments with the different profits using the above coefficients
                runCoefficientsExperiments(agents, kmax, percent, removeOperationRatio);
            }
        }
    }
}
