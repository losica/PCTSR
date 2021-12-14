package experiments;

import heuristics.Heuristic;
import model.PathResult;
import model.Place;
import reader.DataReader;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class Experiment {
    private static PrintWriter writer;
    private DataReader reader = new DataReader();
    // name of the output file
    private String resultFile;
    // all companies and their info
    Place[] places = reader.getAllCompanies();
    // distances between companies
    double[][] distanceMatrix = reader.getDistanceMatrix();
    // number of agents to experiment with
    final int[] AGENTS = new int[]{ 1, 2, 4, 8, 10 };
    // the number of vertices
    private final int NUM_VERTICES = 91;
    // the different amount of profit we will be looking for when making an experimental run
    int[] PROFITS;
    // the different starting vertices we will make an experimental run from
    int[] START_VERTICES;
    // to store our distance results for each experimental run; we will have as many results as the number of starting vertices
    double[] distanceResults = new double[NUM_VERTICES];

    // resultFile is the name of the output file, e.g. results.csv
    Experiment(String resultFile) {
        this.resultFile = resultFile;
        init();
        initProfitArray();
        initStartVertices();
    }

    // instantiate the print writer
    private void init() {
        Charset CHARSET = StandardCharsets.UTF_8;
        Path RES_FILE = Paths.get(resultFile);
        try {
            writer = new PrintWriter(Files.newBufferedWriter(RES_FILE, CHARSET));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // the desired profit we will be looking for will start at 5 and go up to 295, increasing by 5 each time (eg. 5, 10, 15, ..., 295)
    private void initProfitArray() {
        int MAX_PROFIT = 299;
        PROFITS = new int[MAX_PROFIT / 5];
        int idx = 0;
        for (int i = 5; i <= MAX_PROFIT; i += 5) {
            PROFITS[idx] = i;
            idx++;
        }
    }

    // we will make an experimental run from each of our 90 starting vertices
    private void initStartVertices() {
        START_VERTICES = new int[NUM_VERTICES];
        for (int i = 0; i < NUM_VERTICES; i++) {
            START_VERTICES[i] = i;
        }
    }

    // re-instantiate a clean distance array after each time we calculate results for an experimental run
    void resetDistanceArray(){
        this.distanceResults = new double[NUM_VERTICES];
    }

    // used to run the experiment
    public abstract void run();

    // print a row to the output file. The string must be formatted to fit the file type.
    // For csv, use fx String.format(Locale.US, "%s,%s,%d,%.2f", "someString", "anotherString", 234, 44.145)
    void printRow(String row) {
        writer.println(row);
    }

    // close the writer at the end of the experiment
    void endExperiment() {
        writer.close();
    }

    // sum um all the distances in the distance array and return the average
    double calculateAverageDistance() {
        double res = 0d;
        for (double d : distanceResults) {
            res += d;
        }
        return res / distanceResults.length;
    }

    // calculate distance results for an experimental run with given heuristic method
    void calculateDistanceResults(Heuristic h, int index) {
        PathResult[] resultPath = h.getResultPaths();
        // sum the total path length
        double routeDistance = 0d;
        for (PathResult res : resultPath) {
            routeDistance += res.getPathLength();
        }
        // index is the index of the current starting vertex
        distanceResults[index] = routeDistance;
    }

    public String getResultFileName() {
        return this.resultFile;
    }

    public String getExperimentName() {
        String name = this.getClass().getName();
        return name.substring(name.indexOf('.') + 1);
    }
}
