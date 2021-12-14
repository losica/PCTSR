package GUI;

import model.Place;

class GUIUtil {
    private double totalProfit;

    GUIUtil(Place[] places) {
        setTotalProfit(places);
    }

    private void setTotalProfit(Place[] places) {
        this.totalProfit = 0.0;
        for(Place place: places) {
            this.totalProfit += place.getFirmProfit();
        }
    }

    int getPlaceXposition(Place place, int maxX) {
        double minLongitude = 8.072245;
        double maxLongitude = 12.793928;
        return (int) ((((place.getLongitude() - minLongitude) * (maxX)) / (maxLongitude - minLongitude)));
    }

    int getPlaceYposition(Place place, int maxY) {
        //map's latitude and longitude values
        double minLatitude = 54.554316;
        double maxLatitude = 57.751806;
        return (int) (maxY - (((place.getLatitude() - minLatitude) * (maxY)) / (maxLatitude - minLatitude)));
    }

    double getTotalProfit() {
        return totalProfit;
    }
}
