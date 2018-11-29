package groupup.com.groupup;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.bumptech.glide.util.LogTime;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

import groupup.com.groupup.Database.DatabaseManager;
import groupup.com.groupup.Database.GetDataListener;
import groupup.com.groupup.Database.UserKeys;

public class GroupWaitlistPage extends AppCompatActivity {
    private static final String TAG = "GroupWaitlistPage";
    Group currentGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Waitlist of potential applicants
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_potential_group_page);

        getIncomingIntent();

        setTitle(" Waitlist ");


    }

    private void getIncomingIntent(){
        Log.d(TAG, "getIncomingIntent: checking for incoming intents");
        if(getIntent().hasExtra("group"))
        {
            Log.d(TAG, "getIncomingIntent: found intent extras.");

            Group currentGroup = (Group) getIntent().getSerializableExtra("group");
            this.currentGroup = currentGroup;
            this.displayWaitlist();
        }
    }

    private synchronized void displayWaitlist()
    {
        final Context myContext = this;
        final ArrayList<String> groupMemberNames = new ArrayList<>();
        final List<String> groupMemberIDs = currentGroup.getWaitlistUsers();

        final RecyclerView recyclerView = findViewById(R.id.waitlist_recyc);
        WaitlistRVA adapter = new WaitlistRVA(this, groupMemberNames, groupMemberIDs, currentGroup);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DatabaseManager manager = DatabaseManager.getInstance();

        for (String uid : this.currentGroup.getWaitlistUsers()) {
            Log.d(TAG, "member uid: " + uid);

            manager.getUserWithIdentifier(UserKeys.ID, uid, new GetDataListener() {
                @Override
                public void onSuccess(DataSnapshot data) {
                    User user = data.getValue(User.class);
                    Log.d(TAG, "member found: " + user.getName());
                    groupMemberNames.add(user.getName());
                    WaitlistRVA adapter = new WaitlistRVA(myContext, groupMemberNames, groupMemberIDs, currentGroup);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(myContext));
                }

                @Override
                public void onFailure(DatabaseError error) {

                }
            });
        }

        Log.d(TAG, "#members found: " + groupMemberNames.size());
    }

}
