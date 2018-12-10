package groupup.com.groupup.Database;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.HashMap;

import groupup.com.groupup.Authentication.Authenticator;
import groupup.com.groupup.Group;
import groupup.com.groupup.GroupSearch;
import groupup.com.groupup.User;

/**
 * Handles firebase database updating and querying.
 */
public class DatabaseManager {

    private static final DatabaseManager instance = new DatabaseManager();

    public static DatabaseManager getInstance() {
        return instance;
    }

    private final DatabaseReference database;

    private DatabaseManager() {
        this.database = FirebaseDatabase.getInstance().getReference();
    }

    /**
     * Query's firebase for groups that match the request.
     * @param attribute The field to check.
     * @param value The value to compare the field's value to.
     */
    public void getGroupWithIdentifier(GroupKeys attribute, String value, final GetDataListener listener) {
        this.getObjectWithIdentifier("groups/", attribute.toString(), value, listener);
    }

    /**
     * Query's firebase for users that match the request.
     * @param attribute The field to check.
     * @param value The value to compare the field's value to.
     */
    public void getUserWithIdentifier(UserKeys attribute, String value, final GetDataListener listener) {
        this.getObjectWithIdentifier("users/", attribute.toString(), value, listener);
    }

    /**
     * Query's firebase for data that matches the request.
     * @param attribute The field to check.
     * @param value The value to compare the field's value to.
     */
    private void getObjectWithIdentifier(String childPath, String attribute, String value, final GetDataListener listener) {
        DatabaseReference groupRef = database.child(childPath);
        groupRef.orderByChild(attribute).equalTo(value).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailure(databaseError);
            }
        });
    }

    /**
     * Adds a group the database.
     * @param group The group to add.
     * @param attributes Contains a map of keys and values to be assigned to the group object before saving.
     */
    public void addGroup(final Group group, final HashMap<GroupKeys, String> attributes) {
        DatabaseReference updatedReference = database.child("groups/").push();
        FirebaseUser currentUser = Authenticator.getInstance().getCurrentUser();

        String id = updatedReference.getKey();
        group.setID(id);

        for(GroupKeys key : attributes.keySet()) {
            String value = attributes.get(key);
            switch(key) {
                case ID:
                    group.setID(value);
                    break;
                case ACTIVITY:
                    group.setActivity(value);
                    break;
                case LOCATION:
                    group.setLocation(value);
                    break;
                case NAME:
                    group.setName(value);
                    break;
                case PICTURE:
                    group.setPicture(value);
                    break;
                case MEMBERS:
                    break;
                default:
                    break;
            }
        }

        group.addMember(currentUser.getUid());
        group.setOwner(currentUser.getUid());

        final DatabaseManager manager = DatabaseManager.getInstance();

        this.getUserWithIdentifier(UserKeys.ID, currentUser.getUid(), new GetDataListener() {
            @Override
            public void onSuccess(DataSnapshot data) {
                User user = data.getValue(User.class);
                user.addGroup(group.getID());
                manager.updateUserWithID(user.getID(), user);
            }

            @Override
            public void onFailure(DatabaseError error) {}
        });

        updatedReference.setValue(group);
    }

    /**
     * Adds a user to the database.
     * @param user the user to be added to the database.
     */
    public void addUser(final User user) {
        DatabaseReference updatedReference = database.child("users/").push();
        updatedReference.setValue(user, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if(databaseError == null) {
                    System.out.println("User added.");
                } else {
                    System.out.println("User couldn't be added.");
                }
            }
        });
    }

    /**
     * Updates the user object at a specified ID.
     * @param id The id to update.
     * @param user The new User object to be set at the ID.
     */
    public void updateUserWithID(final String id, final User user) {
        DatabaseReference db = database.child("users/");
        Query query = db.orderByChild("id").equalTo(id);

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String key = dataSnapshot.getKey();
                FirebaseDatabase.getInstance().getReference().child("users").child(key).setValue(user);
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
    }

    /**
     * Updates the group object at a specified ID.
     * @param id The id to update.
     * @param group The new Group object to be set at the ID.
     */
    public void updateGroupWithID(final String id, final Group group) {
        DatabaseReference db = database.child("groups/");
        Query query = db.orderByChild("id").equalTo(id);

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String key = dataSnapshot.getKey();
                FirebaseDatabase.getInstance().getReference().child("groups").child(key).setValue(group);
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
    }

    /**
     * Populates the results list with the groups that match the searchText
     * @param searchPage The page the search is occurring on
     * @param searchText The text to search for in the database
     * @param results A list of the groups that match the
     */
    public void getGroupsWithActivity(final GroupSearch searchPage, final String searchText, final ArrayList<Group> results) {

        DatabaseReference database = FirebaseDatabase.getInstance().getReference("groups/");

        Query query = database.orderByChild("name");

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final Group group = dataSnapshot.getValue(Group.class);

                DatabaseManager manager = DatabaseManager.getInstance();
                String uid = Authenticator.getInstance().getCurrentUser().getUid();

                manager.getUserWithIdentifier(UserKeys.ID, uid, new GetDataListener() {
                    @Override
                    public void onSuccess(DataSnapshot data) {
                        User currentUser = data.getValue(User.class);

                        if(group.getActivity().toUpperCase().contains(searchText.toUpperCase()) && !currentUser.getGroups().contains(group.getID())) {
                            results.add(group);
                            searchPage.refreshView();
                        }
                    }

                    @Override
                    public void onFailure(DatabaseError error) {

                    }
                });
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
    }

    /**
     * Populates the results list with the groups that match the searchText
     * @param searchPage The page the search is occurring on
     * @param searchText The text to search for in the database
     * @param results A list of the groups that match the
     */
    public void getGroupsWithName(final GroupSearch searchPage, final String searchText, final ArrayList<Group> results) {

        DatabaseReference database = FirebaseDatabase.getInstance().getReference("groups/");

        Query query = database.orderByChild("name");

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final Group group = dataSnapshot.getValue(Group.class);

                DatabaseManager manager = DatabaseManager.getInstance();
                String uid = Authenticator.getInstance().getCurrentUser().getUid();

                manager.getUserWithIdentifier(UserKeys.ID, uid, new GetDataListener() {
                    @Override
                    public void onSuccess(DataSnapshot data) {
                        User currentUser = data.getValue(User.class);

                        if(group.getName().toUpperCase().contains(searchText.toUpperCase()) && !currentUser.getGroups().contains(group.getID())) {
                            results.add(group);
                            searchPage.refreshView();
                        }
                    }

                    @Override
                    public void onFailure(DatabaseError error) {

                    }
                });
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
    }
}
