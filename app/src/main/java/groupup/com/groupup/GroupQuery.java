package groupup.com.groupup;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class GroupQuery {

    /**
     * Populates the results list with the groups that match the searchText
     * @param searchPage The page the search is occurring on
     * @param searchText The text to search for in the database
     * @param results A list of the groups that match the
     */
    public static void getGroupsWithActivity(final GroupSearch searchPage, final String searchText, final ArrayList<Group> results) {

        DatabaseReference database = FirebaseDatabase.getInstance().getReference("groups/");

        Query query = database.orderByChild("name");//.equalTo(searchText.toUpperCase());

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Group group = dataSnapshot.getValue(Group.class);

                if(group.getActivity().toUpperCase().contains(searchText.toUpperCase())) {
                    results.add(group);
                    searchPage.refreshView();
                }
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

    public static void getUserWithID(String id, final Callback callback) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users/");

        Query query = db.orderByChild("id").equalTo(id);

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User user = dataSnapshot.getValue(User.class);
                System.out.println(user.getName());

                callback.onCallback(user);
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
