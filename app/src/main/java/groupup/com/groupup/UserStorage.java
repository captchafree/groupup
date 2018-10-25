package groupup.com.groupup;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserStorage extends Storage {

    private static UserStorage instance = null;

    /**
     * The subpath that the class should store data to.
     */
    private final static String subPath = "users";

    /**
     * A reference to the part of the database that stores users' information
     */
    private DatabaseReference ref;

    private UserStorage() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference(super.path).child(this.subPath);
    }

    public static UserStorage getInstance() {
        if(instance == null) {
            instance = new UserStorage();
        }
        return instance;
    }

    /**
     * Adds a user to the database.
     * @param user the user to be added to the database.
     */
    public void addUser(final User user) {
        DatabaseReference updatedReference = ref.push();
        final String userID = updatedReference.getKey();
        user.setID(userID);

        updatedReference.setValue(user, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    System.out.println("Data could not be saved: " + databaseError.getMessage());
                } else {
                    System.out.println("Data saved successfully: " + user.getName());
                }
            }
        });
    }
}
