package groupup.com.groupup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import groupup.com.groupup.LocationServices.Location;
import groupup.com.groupup.LocationServices.LocationServiceManager;
import groupup.com.groupup.LocationServices.Permissions;

public class User {

    //TODO: Save ID to shared preferences.
    private String ID = null;

    /**
     * The user's name.
     */
    private String name;

    /**
     * The user's email.
     */
    private String email;

    /**
     * The user's bio.
     */
    private String bio;

    /**
     * @link Group
     * A list of groups that the user is currently in. The list stores the unique group ID's.
     */
    private List<String> groups;

    /**
     * A Base64 string representation of the user's profile image.
     */
    private String profileImage;

    private double latitude, longitude;

    public User(String name) {
        this.name = name;
        this.bio = "";
        this.groups = new ArrayList<>();
        this.profileImage = null;

        this.latitude = this.longitude = 0;
    }

    public User() {
        this("");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void addGroup(String... groupID) {
        Collections.addAll(groups, groupID);
    }

    public void removeGroup(String groupID) {
        groups.remove(groupID);
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getID() {
        return ID;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
