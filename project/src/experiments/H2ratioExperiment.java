package experiments;

public class H2ratioExperiment extends HeuristicTwoExperiment {

    private final int[] AGENTS = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    private final int NUM_RUNS = 10;

    public H2ratioExperiment(String filename) {
        super(filename);
    }

    @Override
    public void run() {
        // header row of the CSV file
        printRow(String.format("%s,%s,%s,%s,%s,%s", "agents", "kmax", "percent", "ratio", "profit", "distance"));
        // run experiment
        runRatioExperiment();
        // close the writer and end the experiment
        endExperiment();
    }

    private void runRatioExperiment() {
        for (int agents : AGENTS) {
            int kmax = -1;
            int percent = -1;
            for (int m = 0; m <= 10; m++) {
                // mutations ratio = -1 means that we will use random to determine whether to remove the best vertex in the route or not
                double removeOperationRatio = -1.0;
                if (m > 0) {
                    // thank you Java for doing this to us -.-
                    removeOperationRatio = m / 10d;
                }
                // run experiments with the different profits using the above coefficients
                runCoefficientsExperiments(agents, kmax, percent, removeOperationRatio, NUM_RUNS);
            }
        }
    }
}
