package in.thefleet.thefuelfilling.model;

public class Station {

    private int Station_ID;
    private int Fleet_ID;
    private double Latitude;
    private double Longitude;
    private String Location_Name;
    private String State_Name;
    private String Station_Name;
    private double RegularPrice;
    private double PremiumPrice;


    public int getStation_ID() {
        return Station_ID;
    }

    public void setStation_ID(int station_ID) {
        Station_ID = station_ID;
    }

    public int getFleet_ID() {
        return Fleet_ID;
    }

    public void setFleet_ID(int fleet_ID) {
        Fleet_ID = fleet_ID;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public String getLocation_Name() {
        return Location_Name;
    }

    public void setLocation_Name(String location_Name) {
        Location_Name = location_Name;
    }

    public String getState_Name() {
        return State_Name;
    }

    public void setState_Name(String state_Name) {
        State_Name = state_Name;
    }

    public String getStation_Name() {
        return Station_Name;
    }

    public void setStation_Name(String station_Name) {
        Station_Name = station_Name;
    }

    public double getPremiumPrice() {return PremiumPrice;}

    public void setPremiumPrice(double premiumPrice) {PremiumPrice = premiumPrice;}

    public double getRegularPrice() {return RegularPrice;}

    public void setRegularPrice(double regularPrice) {RegularPrice = regularPrice;}
}
