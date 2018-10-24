package groupup.com.groupup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class User {

    /**
     * The user's name.
     */
    private String name;

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

    public User(String name) {
        this.name = name;
        this.bio = null;
        this.groups = new ArrayList<>();
        this.profileImage = null;
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
        Collections.addAll(this.groups, groupID);
    }

    public void removeGroup(String groupID) {
        this.groups.remove(groupID);
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
}
