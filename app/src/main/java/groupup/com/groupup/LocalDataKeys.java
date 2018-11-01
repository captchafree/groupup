package groupup.com.groupup;

public enum LocalDataKeys {

    PREFERENCES("Preferences"),
    USERID("UID"),
    GROUPID("GID");

    private final String key;

    LocalDataKeys(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return key;
    }
}
