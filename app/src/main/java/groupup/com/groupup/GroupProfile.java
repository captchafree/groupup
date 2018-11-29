package groupup.com.groupup;

import android.app.Activity;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

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

        Button editGroup = findViewById(R.id.edit_group_button);
        Button viewWaitlist = findViewById(R.id.viewWaitlist);
        Button leaveButton = findViewById(R.id.leaveButton);

        Button groupChatButton = findViewById(R.id.group_chat_button);
        groupChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transitionToGroupChat();
            }
        });

        leaveButton.setOnClickListener(this);
        viewWaitlist.setOnClickListener(this);

        if(currentGroup.isWaitlistGroup()){
            viewWaitlist.setVisibility(View.VISIBLE);
        }

        editGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transitionToEditGroup();
            }
        });
    }

    private void transitionToGroupChat() {
        Intent intent = new Intent(this, GroupCommunicationPage.class);
        intent.putExtra("GROUP_NAME", currentGroup.getName());
        this.startActivity(intent);
    }

    private void transitionToEditGroup() {
        Intent intent = new Intent(this, EditGroup.class);
        intent.putExtra("GROUP_ID", currentGroup.getID());
        this.startActivity(intent);
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
        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final Context context = this;
        final int clickedButtonID = v.getId();
        final Group curr = this.currentGroup;
        final DatabaseManager manager = DatabaseManager.getInstance();



        Log.d(TAG, "onClick: Button pressed" + clickedButtonID);

        if(clickedButtonID == R.id.viewWaitlist){
            Intent intent = new Intent(this, GroupWaitlistPage.class);
            intent.putExtra("group", currentGroup);
            this.startActivity(intent);
        }

        manager.getUserWithIdentifier(UserKeys.ID, userID, new GetDataListener() {
            @Override
            public void onSuccess(DataSnapshot data) {
                User user = data.getValue(User.class);

                if (clickedButtonID == R.id.leaveButton) {
                    if(curr.getMembers().size()-1 != 0) {
                        user.removeGroup(curr.getID());
                        curr.removeMember(userID);
                        manager.updateUserWithID(userID, user);
                        manager.updateGroupWithID(curr.getID(), currentGroup);
                    }
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }

            @Override
            public void onFailure(DatabaseError error) {

            }
        });

    }

    private synchronized void setImage(final String imageUrl, final String groupName, final String groupLocation, final String groupActivity){
        Log.d(TAG, "setImage: setting the image and name to widgets");

        final TextView name = findViewById(R.id.wlusers);

        setDisplayText(name, groupName + ": " + groupActivity + "\n\t Location: " + groupLocation);

        ImageView image = findViewById(R.id.image);

        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(image);

        final Context myContext = this;
        final ArrayList<String> groupMemberNames = new ArrayList<>();
        final List<String> groupMemberIDs = this.currentGroup.getMembers();

        final RecyclerView recyclerView = findViewById(R.id.member_recyc);
        MemberListRVA adapter = new MemberListRVA(this, groupMemberNames, groupMemberIDs, currentGroup);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        DatabaseManager manager = DatabaseManager.getInstance();

        for (String uid : groupMemberIDs) {
            Log.d(TAG, "member uid: " + uid);

            manager.getUserWithIdentifier(UserKeys.ID, uid, new GetDataListener() {
                @Override
                public void onSuccess(DataSnapshot data) {
                    User user = data.getValue(User.class);
                    Log.d(TAG, "member found: " + user.getName());
                    groupMemberNames.add(user.getName());
                    MemberListRVA adapter = new MemberListRVA(myContext, groupMemberNames, groupMemberIDs, currentGroup);
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
