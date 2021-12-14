package reader;

import model.Place;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class DataReader {
    private final int NUM_COMPANIES = 91;
    // a list with all the companies
    private final Place[] COMPANIES = new Place[NUM_COMPANIES];
    // a matrix with distances between all the companies
    private final double[][] DISTANCE_MATRIX = new double[91][91];

    public DataReader() {
        try {
            readFile();
            makeMatrixSymmetric();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readFile() throws IOException {
        InputStream is = DataReader.class.getResourceAsStream("/data/data.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        // these companies must be sorted based on their profit in order for the rank to make sense
        for(int i = 0; i < NUM_COMPANIES; i++) {
            // parse company data 0 - name, 1 - address, 2 - profit, 3 - latitude, 4 - longitude, 5 - distance array
            String[] company = br.readLine().split(";");
            this.COMPANIES[i] = new Place(i, company[0], company[1], Double.parseDouble(company[2]), Double.parseDouble(company[3]), Double.parseDouble(company[4]));
            this.DISTANCE_MATRIX[i] = getDistanceArray(company[5]);
        }
    }

    private double[] getDistanceArray(String distances) {
        // all the distances are comma separated
        String[] distanceString = distances.split(",");
        double[] distanceArray = new double[distanceString.length];
        for(int i = 0; i < distanceString.length; i++) {
            distanceArray[i] = Double.parseDouble(distanceString[i]);
        }
        return distanceArray;
    }

    // due to the usage of distances from the real road network, distance from company A to company B is not always the same as the one from B to A
    // we need our graph to be symmetric, thus, if such case occurs, we take the average between the two 
    private void makeMatrixSymmetric() {
        for(int i=0; i<DISTANCE_MATRIX.length; i++) {
            for(int j=0; j<DISTANCE_MATRIX.length; j++){
                if(DISTANCE_MATRIX[i][j] != DISTANCE_MATRIX[j][i]) {
                    double avg = (DISTANCE_MATRIX[i][j]+ DISTANCE_MATRIX[j][i])/2;
                    DISTANCE_MATRIX[i][j] = avg;
                    DISTANCE_MATRIX[j][i] = avg;
                }
            }
        }
    }

    // GETTERS
    
    public double[][] getDistanceMatrix() {
        return this.DISTANCE_MATRIX;
    }

    public Place[] getAllCompanies() {
        return this.COMPANIES;
    }
}

