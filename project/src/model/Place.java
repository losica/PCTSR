package model;

public class Place {
    private int id;
    private String companyName;
    private String address;
    private double latitude;
    private double longitude;
    private double firmProfit;

    public Place(int id, String companyName, String address, double firmProfit, double latitude, double longitude) {
        this.id = id;
        this.companyName = companyName;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.firmProfit = firmProfit;
    }

    // GETTERS

    public int getId() {
        return id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getFirmProfit() {
        return firmProfit;
    }

    @Override
    public String toString() {
        return "\n{> " + this.companyName + " <}";
    }

    String getShortDescription() {
        return "#" + this.id + "\n"
                + this.companyName + "\n"
                + this.address;
    }

    String getDescription() {
        return "#" + this.id + "\n"
                + this.companyName + "\n"
                + this.address + "\n"
                + "Profit: " + this.firmProfit;
    }
}
