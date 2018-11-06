package groupup.com.groupup.Database;

public enum UserKeys {

    ID("id"),
    EMAIL("email"),
    NAME("name"),
    GROUPS("groups");

    private final String key;

    UserKeys(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return key;
    }

}
