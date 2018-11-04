package groupup.com.groupup;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

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
     * Initializes the UserStorage class with the current activity.
     * @param activity The activity to reference.
     */
    public UserStorage init(Activity activity) {
        this.activity = activity;
        return this;
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

    public void setCurrentUser(User user) {
        (new LocalStorage(activity)).putString(LocalDataKeys.USERID.toString(), user.getID());
    }

    public static User getCurrentUser() {
        String UID = "-LQUxfvD_f0oiI5nC-T7";//(new LocalStorage(activity)).getString(LocalDataKeys.USERID.toString());

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users/");

        Query query = db.orderByChild("id");//.equalTo(UID);

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                System.out.println("HIT!");
                User user = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });


        return null;
    }
}
