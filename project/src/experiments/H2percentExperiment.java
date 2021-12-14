package experiments;

public class H2percentExperiment extends HeuristicTwoExperiment {

    private final int NUM_RUNS = 10;

    public H2percentExperiment (String filename) {
        super(filename);
    }

    @Override
    public void run() {
        // header row of the csv result file
        printRow(String.format("%s,%s,%s,%s,%s,%s", "agents", "kmax", "percent", "ratio", "profit", "distance"));
        // run experiment
        runPercentExperiment();
        // close the writer and end the experiment
        endExperiment();
    }

    private void runPercentExperiment() {
        for (int agents : AGENTS) {
            int kmax = -1;
            for (int percent = 0; percent <= 100; percent += 10) {
                double removeOperationRatio = -1;
                // run experiments with the different profits using the above coefficients
                if(percent != 0) {
                    //run the experiment with different values of constant percentage, e.g., 10%, 20%, etc.
                    runCoefficientsExperiments(agents, kmax, percent, removeOperationRatio, NUM_RUNS);
                } else {
                    //run the experiment with the default percentage (random value from 0% to 80%)
                    runCoefficientsExperiments(agents, kmax, -1, removeOperationRatio, NUM_RUNS);
                }
            }
        }
    }
}
