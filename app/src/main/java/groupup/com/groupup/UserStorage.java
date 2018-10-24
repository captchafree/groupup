package groupup.com.groupup;

public class UserStorage {

    public static UserStorage instance = new UserStorage();

    public static UserStorage getInstance() {
        return instance;
    }
}
