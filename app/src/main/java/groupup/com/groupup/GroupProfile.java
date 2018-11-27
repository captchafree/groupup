package groupup.com.groupup;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import groupup.com.groupup.Database.DatabaseManager;
import groupup.com.groupup.Database.GetDataListener;
import groupup.com.groupup.Database.UserKeys;

public class GroupProfile extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "GroupProfile";
    private Group currentGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_group);

        getIncomingIntent();

        findViewById(R.id.leaveButton).setOnClickListener(this);

        Button editGroup = findViewById(R.id.edit_group_button);

        /*final GroupProfile from = this;
        editGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditGroup to = new EditGroup(currentGroup.getID());
                startActivity(new Intent(from, to.getClass()));
            }
        });*/
    }

    private void getIncomingIntent(){
        Log.d(TAG, "getIncomingIntent: checking for incoming intents");
        if(getIntent().hasExtra("group"))
        {
            Log.d(TAG, "getIncomingIntent: found intent extras.");

            Group currentGroup = (Group) getIntent().getSerializableExtra("group");
            String imageUrl = currentGroup.getPicture();
            String groupName = currentGroup.getName();
            String groupLocation = currentGroup.getLocation();
            String groupActivity = currentGroup.getActivity();

            this.currentGroup = currentGroup;
            setImage(imageUrl, groupName, groupLocation, groupActivity);
        }
    }

    public void onClick(View v) {
        Log.d(TAG, "onClick: Button pressed");
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        int i = v.getId();

        DatabaseManager manager = DatabaseManager.getInstance();
        if (i == R.id.leaveButton) {
            if(this.currentGroup.getMembers().size() != 0) {
                this.currentGroup.removeMember(userID);
                manager.updateGroupWithID(this.currentGroup.getID(), currentGroup);
            }
            //finish();
            //Intent intent = new Intent(this, Homepage.class);
            //this.startActivity(intent);
        }
    }

    private synchronized void setImage(final String imageUrl, final String groupName, final String groupLocation, final String groupActivity){
        Log.d(TAG, "setImage: setting the image and name to widgets");

        final TextView name = findViewById(R.id.group_description);

        setDisplayText(name, groupName + ": " + groupActivity + "\n\t At " + groupLocation);

        ImageView image = findViewById(R.id.image);

        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(image);

        final Context myContext = this;
        final ArrayList<String> groupMemberNames = new ArrayList<>();

        final RecyclerView recyclerView = findViewById(R.id.member_recyc);
        MemberListRVA adapter = new MemberListRVA(this, groupMemberNames);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        DatabaseManager manager = DatabaseManager.getInstance();

        for (String uid : this.currentGroup.getMembers()) {
            Log.d(TAG, "member uid: " + uid);

            manager.getUserWithIdentifier(UserKeys.ID, uid, new GetDataListener() {
                @Override
                public void onSuccess(DataSnapshot data) {
                    User user = data.getValue(User.class);
                    Log.d(TAG, "member found: " + user.getName());
                    groupMemberNames.add(user.getName());
                    MemberListRVA adapter = new MemberListRVA(myContext, groupMemberNames);
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

    private void setDisplayText(TextView view, String text) {
        view.setText(text);
    }
}
