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

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class GroupProfile extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "GroupProfile";
    private Group currentGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_group);

        getIncomingIntent();

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
        int i = v.getId();
        final Group curr = this.currentGroup;
        if (i == R.id.leaveButton) {
            if(this.currentGroup.getMembers().size() != 0) {
                for (String uid : this.currentGroup.getMembers()) {
                    if(uid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        GroupQuery.getUserWithID(FirebaseAuth.getInstance().getCurrentUser().getUid(), new Callback() {
                            @Override
                            public void onCallback(Object value) {
                                User user = (User) value;
                                curr.removeMember(user.getID());
                                user.removeGroup(curr.getID());
                                GroupQuery.updateUserWithID(user.getID(), user);
                                FirebaseDatabase.getInstance().getReference().child("groups").child(curr.getID()).setValue(curr);
                            }
                        });
                    }
                }
            }
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

        groupMemberNames.add("elmo123");
        groupMemberNames.add("bobross");

        final RecyclerView recyclerView = findViewById(R.id.group_profile_recycview);
        MemberListRVA adapter = new MemberListRVA(this, groupMemberNames);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        for (String uid : this.currentGroup.getMembers()) {
            Log.d(TAG, "member uid: " + uid);

            GroupQuery.getUserWithID(uid, new Callback() {
                @Override
                public void onCallback(Object value) {
                    User user = (User) value;
                    Log.d(TAG, "member found: " + user.getName());
                    groupMemberNames.add(user.getName());
                    MemberListRVA adapter = new MemberListRVA(myContext, groupMemberNames);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(myContext));
                }
            });
        }

        Log.d(TAG, "#members found: " + groupMemberNames.size());

    }

    private void setDisplayText(TextView view, String text) {
        view.setText(text);
    }
}
