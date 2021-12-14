package test;

import model.Place;
import reader.DataReader;

public class ReaderTest {
    private final DataReader reader = new DataReader();
    private final Place[] COMPANIES = reader.getAllCompanies();
    private final int NUM_COMPANIES = COMPANIES.length;
    private final double[][] DISTANCE_MATRIX = reader.getDistanceMatrix();

    public void runTests() {
        assert this.NUM_COMPANIES == 91;
        testDistanceMatrix();
        testPlaces();
        checkDistanceMatrix();
    }

    private void testDistanceMatrix() {
        for(int i = 0; i < this.NUM_COMPANIES; i++) {
            double[] companyDistance = this.DISTANCE_MATRIX[i];
            // distance to itself should be 0
            assert companyDistance[i] == 0;
            // distance to another vertex should be positive
            if (i > 0) assert companyDistance[i-1] > 0;
            else assert companyDistance[i+1] > 0;
        }
    }

    private void testPlaces() {
        for (int i = 0; i < this.NUM_COMPANIES; i++) {
            Place place = this.COMPANIES[i];
            assert place.getId() >= 0;
            assert place.getCompanyName() != null && place.getCompanyName().length() > 0;
            assert place.getAddress() != null && place.getCompanyName().length() > 0;
            assert place.getFirmProfit() > 0;
            assert place.getLatitude() > 0;
            assert place.getLongitude() > 0;
        }
    }

    private void checkDistanceMatrix() {
        for(int i=0; i< DISTANCE_MATRIX.length; i++) {
            for(int j=0; j<DISTANCE_MATRIX.length; j++){
                assert (DISTANCE_MATRIX[i][j] == DISTANCE_MATRIX[j][i]);
            }
        }
    }
}
