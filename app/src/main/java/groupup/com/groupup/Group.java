package groupup.com.groupup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Group {

    /**
     *  Group ID to be retrieved from Firebase
     */
    private String ID = null;

    /**
     *  Name of group
     */
    private String name;

    /**
     *  Group's bio
     */
    private String bio;

    /**
     *  Group's activity name
     */
    private String activity;

    /**
     *  Group's location name
     */
    private String location;

    /**
     *  Group's owner name
     */
    private String owner;

    /**
     *  List of members that are currently in the group, stores user's unique IDs
     */
    private List<String> members;

    /**
     *  List of leaders that are currently in the group, stores user's unique IDs
     */
    private List<String> leaders;

    public Group(){

    }

    public Group(String id, String name, String activity, String location){
        this.ID = id;
        this.name = name;
        this.bio = null;
        this.activity = activity;
        this.location = location;
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

    public String getOwner() { return owner; }

    public void setOwner(String owner) { this.owner = owner; }

    public List<String> getMembers() { return members; }

    public void addMember(String... userID) { Collections.addAll(members, userID); }

    public void removeMember(String userID) { members.remove(userID); }

    public List<String> getLeaders() { return leaders; }

    public void addLeader(String... userID) { Collections.addAll(leaders, userID); }

    public void removeLeader(String userID) { leaders.remove(userID); }


}
