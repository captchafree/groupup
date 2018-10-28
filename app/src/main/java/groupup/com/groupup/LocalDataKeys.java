package groupup.com.groupup;

public enum LocalDataKeys {

    PREFERENCES("Preferences"),
    USERID("UID");

    private final String key;

    LocalDataKeys(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return key;
    }
}
