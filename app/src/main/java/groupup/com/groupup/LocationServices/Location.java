package groupup.com.groupup.LocationServices;

public class Location {

    public double latitude, longitude;

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Location() {
        this.latitude = 0;
        this.longitude = 0;
    }
}
