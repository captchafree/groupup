package groupup.com.groupup.Database;

public enum GroupKeys {

    ID("id"),
    ACTIVITY("activity"),
    LOCATION("location"),
    NAME("name"),
    PICTURE("picture"),
    OWNER("owner"),
    MEMBERS("members");

    private final String key;

    GroupKeys(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return key;
    }
}
