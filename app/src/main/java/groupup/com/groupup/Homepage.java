package groupup.com.groupup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

import groupup.com.groupup.Authentication.Authenticator;
import groupup.com.groupup.Database.DatabaseManager;
import groupup.com.groupup.Database.GetDataListener;
import groupup.com.groupup.Database.GroupKeys;
import groupup.com.groupup.Database.UserKeys;

public class Homepage extends AppCompatActivity {

    private static final String TAG = "Homepage";
    private ArrayList<Group> results = new ArrayList<>();

    TextView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        Button GroupSearch_Button = findViewById(R.id.GroupSearch_Button);
        Button CreateGroup_Button = findViewById(R.id.CreateGroup_Button);

        GroupSearch_Button.setOnClickListener(new PageTransitionListener(this, GroupSearch.class));
        CreateGroup_Button.setOnClickListener(new PageTransitionListener(this, CreateGroup.class));

        this.view = findViewById(R.id.userInformation);

        this.setupUserView(view);

        //initRecyclerView();

    }

    private void setupUserView(final TextView view) {
        final Homepage homepage = this;
        final DatabaseManager db = DatabaseManager.getInstance();

        String userID = Authenticator.getInstance().getCurrentUser().getUid();

        db.getUserWithIdentifier(UserKeys.ID, userID, new GetDataListener() {
            @Override
            public void onSuccess(DataSnapshot data) {
                User user = data.getValue(User.class);

                String text = "";
                text += "Name: " + user.getName() + "\n";
                text += "Email: " + user.getEmail() + "\n";
                text += "Profile Image: " + user.getProfileImage() + "\n";

                view.setText(text);


                List<String> userGroupIDs = user.getGroups();
                for(String groupID : userGroupIDs) {
                    db.getGroupWithIdentifier(GroupKeys.ID, groupID, new GetDataListener() {
                        @Override
                        public void onSuccess(DataSnapshot data) {
                            results.add(data.getValue(Group.class));
                            refreshView();
                        }

                        @Override
                        public void onFailure(DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onFailure(DatabaseError error) {

            }
        });
    }

    public void refreshView() {
        Log.d(TAG, "refreshView: " + results.size());
        this.initRecyclerView();
    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init recyclerView.");
        RecyclerView recyclerView = findViewById(R.id.homepage_recycview);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, results, GroupProfile.class);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}