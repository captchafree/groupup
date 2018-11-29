package groupup.com.groupup.LocationServices;

/**
 * A basic representation of a location based on latitude and longitude
 */
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
