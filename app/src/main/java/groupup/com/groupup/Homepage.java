package groupup.com.groupup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class Homepage extends AppCompatActivity {

    private static final String TAG = "Homepage";
    private ArrayList<Group> results = new ArrayList<>();
    private String userID;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        Button GroupSearch_Button = findViewById(R.id.GroupSearch_Button);
        Button CreateGroup_Button = findViewById(R.id.CreateGroup_Button);
        Button GroupProfile_Button = findViewById(R.id.GroupProfile_Button);

        GroupSearch_Button.setOnClickListener(new PageTransitionListener(this, GroupSearch.class));
        CreateGroup_Button.setOnClickListener(new PageTransitionListener(this, CreateGroup.class));
        GroupProfile_Button.setOnClickListener(new PageTransitionListener(this, GroupProfile.class));

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        //search(userID);
        if(results.size() > 0)
            refreshView();
    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: init recyclerView.");
        RecyclerView recyclerView = findViewById(R.id.recyclerv_view);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, results);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void refreshView() {
        this.initRecyclerView();
    }

    private void search(String userID) {

        GroupQuery.getGroupwithUserID(this, userID, results);
        /* TO DO: make above
         *      query firebase for the groups that have a userID in their member list
        */
    }
}