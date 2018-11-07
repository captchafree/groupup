package groupup.com.groupup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

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

        initRecyclerView();

    }

    private void setupUserView(final TextView view) {
        final Homepage homepage = this;
        GroupQuery.getUserWithID(FirebaseAuth.getInstance().getCurrentUser().getUid(), new Callback() {
            @Override
            public void onCallback(Object value) {
                User user = (User) value;

                String text = "";
                text += "Name: " + user.getName() + "\n";
                text += "Email: " + user.getEmail() + "\n";
                text += "Profile Image: " + user.getProfileImage() + "\n";

                for(String groupID : user.getGroups()) {
                    text += groupID + "\n";
                }
                view.setText(text);


                List<String> userGroupIDs = user.getGroups();
                for(String groupID : userGroupIDs) {
                    GroupQuery.getGroupWithGroupID(homepage, groupID, results);
                }
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