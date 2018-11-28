package groupup.com.groupup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Group implements Serializable {

     /**Group ID to be retrieved from Firebase*/
    private String ID = null;
    private String name;
    private String bio;
    private String activity;
    private String location;
    private String picture; //URL to a group's picture
    private String owner;
    private boolean isWaitlistGroup;

    /**
     *  List of members that are currently in the group, stores user's unique IDs
     */
    private List<String> members = new ArrayList<>();

    private List<String> waitlistUsers = new ArrayList<>();

    /**
     *  List of leaders that are currently in the group, stores user's unique IDs
     */
    private List<String> leaders;


    public Group() {
        this.ID = "";
        this.name = "";
        this.activity = "";
        this.location = "";
    }

    public Group(String id, String name, String activity, String location, String picture){
        this.ID = id;
        this.name = name;
        this.bio = null;
        this.activity = activity;
        this.location = location;
        this.picture = picture;
        this.owner = null;
        this.members = new ArrayList<>();
        this.leaders = new ArrayList<>();
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getActivity() { return activity; }

    public void setActivity(String activity) { this.activity = activity; }

    public String getLocation() { return location; }

    public void setLocation(String location) { this.location = location; }

    public String getPicture() { return picture; }

    public void setPicture(String picture) { this.picture = picture; }

    public String getOwner() { return owner; }

    public void setOwner(String owner) { this.owner = owner; }

    public List<String> getMembers() { return members; }

    public void addMember(String... userID) { Collections.addAll(members, userID); }

    public void removeMember(String userID) { members.remove(userID); }
    public void removeWaitlistUser(String userID) { waitlistUsers.remove(userID); }

    public List<String> getLeaders() { return leaders; }

    public void addLeader(String... userID) { Collections.addAll(leaders, userID); }

    public void removeLeader(String userID) { leaders.remove(userID); }

    @Override
    public String toString() {
        return "Group info - {id: " + this.ID + "}, {name: " + this.name + "}, {location: " + this.location + "}, {activity: " + this.activity + "} {picture: " + this.picture + "}";
    }

    public List<String> getWaitlistUsers() {
        return waitlistUsers;
    }

    public void addWaitlistUser(String userID) {
        this.waitlistUsers.add(userID);
    }

    public boolean isWaitlistGroup() {
        return isWaitlistGroup;
    }

    public void setWaitlistGroup(boolean waitlistGroup) {
        isWaitlistGroup = waitlistGroup;
    }
}
