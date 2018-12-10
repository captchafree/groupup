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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

import groupup.com.groupup.Database.DatabaseManager;
import groupup.com.groupup.Database.GetDataListener;
import groupup.com.groupup.Database.GroupKeys;
import groupup.com.groupup.Database.UserKeys;

public class GroupProfile extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "GroupProfile";
    private Group currentGroup;

    /**
     * Initializes the view with the groups information and sets the necessary listeners
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_group);

        //Retrieve the information send from the previous page
        getIncomingIntent();
        setTitle(" Group Profile ");

        Button groupChatButton = findViewById(R.id.group_chat_button);
        Button editGroup = findViewById(R.id.edit_group_button);
        Button viewWaitlist = findViewById(R.id.viewWaitlist);
        Button leaveButton = findViewById(R.id.leaveButton);

        //Set a listener to send the user to the group chat page when the button is pressed
        groupChatButton.setOnClickListener(this);
        leaveButton.setOnClickListener(this);

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (currentGroup.getOwner().equals(userID)) {
            editGroup.setVisibility(View.VISIBLE);
            editGroup.setOnClickListener(this);
            if (currentGroup.isWaitlistGroup()) {
                viewWaitlist.setVisibility(View.VISIBLE);
                viewWaitlist.setOnClickListener(this);
            }
        } else {
            leaveButton.setVisibility(View.VISIBLE);
            leaveButton.setOnClickListener(this);
        }
    }

    /**
     * Refreshes the page after a child activity finishes
     */
    @Override
    public void onRestart() {  // After a pause OR at startup
        super.onRestart();
        Log.d(TAG, "onRestart: Refreshing user's list of groups");

        //Query firebase for the group's updated information

        DatabaseManager manager = DatabaseManager.getInstance();
        manager.getGroupWithIdentifier(GroupKeys.ID, "", new GetDataListener() {
            @Override
            public void onSuccess(DataSnapshot data) {
                currentGroup = data.getValue(Group.class);
                refreshView();
            }

            @Override
            public void onFailure(DatabaseError error) {

            }
        });
    }

    /**
     * Transitions from one page to the next.
     *
     * @param nextPage The activity to transition to.
     * @param toSend   The data to send to the next page.
     */
    private void transitionToPage(Class nextPage, String toSend) {
        Intent intent;
        if (toSend.equals("GROUP_NAME")) {
            intent = new Intent(this, nextPage);
            intent.putExtra(toSend, currentGroup.getName());
        } else if (toSend.equals("GROUP_ID")) {
            intent = new Intent(this, nextPage);
            intent.putExtra(toSend, currentGroup.getID());
        } else {
            intent = new Intent(this, GroupWaitlistPage.class);
            intent.putExtra("group", currentGroup);
        }
        this.startActivity(intent);
    }

    /**
     * Overrides onClick to satisfy the OnClickListener interface
     *
     * @param v the view that was clicked.
     */
    @Override
    public void onClick(View v) {
        final int clickedButtonID = v.getId();
        Log.d(TAG, "onClick: Button pressed" + clickedButtonID);

        //Check which button was pressed
        if (clickedButtonID == R.id.group_chat_button)
            this.transitionToPage(GroupCommunicationPage.class, "GROUP_NAME");
        else if (clickedButtonID == R.id.edit_group_button)
            this.transitionToPage(EditGroup.class, "GROUP_ID");
        else if (clickedButtonID == R.id.viewWaitlist) {
            this.transitionToPage(GroupWaitlistPage.class, "group");
        } else {
            final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            final DatabaseManager manager = DatabaseManager.getInstance();
            final Group curr = this.currentGroup;

            //Retrieve the current user and update their information
            manager.getUserWithIdentifier(UserKeys.ID, userID, new GetDataListener() {
                @Override
                public void onSuccess(DataSnapshot data) {
                    User user = data.getValue(User.class);

                    if (curr.getMembers().size() - 1 != 0) {
                        user.removeGroup(curr.getID());
                        curr.removeMember(userID);
                        manager.updateUserWithID(userID, user);
                        manager.updateGroupWithID(curr.getID(), currentGroup);
                    }
                    finish();
                }

                @Override
                public void onFailure(DatabaseError error) {

                }
            });
        }
    }

    /**
     * Retrieves the previous intent that the user was on and retrieves any data that was sent from it.
     */
    private void getIncomingIntent() {
        Log.d(TAG, "getIncomingIntent: checking for incoming intents");
        if (getIntent().hasExtra("group")) {
            Log.d(TAG, "getIncomingIntent: found intent extras.");

            this.currentGroup = (Group) getIntent().getSerializableExtra("group");
            refreshView();
        }
    }

    /**
     * Refreshes the view with the updated information.
     * This is called any time that firebase detects a change in data.
     */
    private synchronized void refreshView() {
        Log.d(TAG, "refreshView: setting the image and name to widgets");

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final List<String> groupMemberIDs = this.currentGroup.getMembers();
        final RecyclerView recyclerView = findViewById(R.id.member_recyc);
        final ArrayList<String> groupMemberNames = new ArrayList<>();
        final TextView name = findViewById(R.id.wlusers);
        final Context myContext = this;

        String groupLocation = currentGroup.getLocation();
        String groupActivity = currentGroup.getActivity();
        String imageUrl = currentGroup.getPicture();
        String groupName = currentGroup.getName();

        setDisplayText(name, groupName + ": " + groupActivity + "\n\t Location: " + groupLocation);

        //Retrieve the group's profile image
        ImageView image = findViewById(R.id.image);
        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(image);

        MemberListRVA adapter = new MemberListRVA(this, groupMemberNames, groupMemberIDs, currentGroup);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DatabaseManager manager = DatabaseManager.getInstance();

        //Display all the members of the group
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

    /**
     * Sets the text of a specified view
     *
     * @param view The view to set the text of
     * @param text The text to put in the view.
     */
    private void setDisplayText(TextView view, String text) {
        view.setText(text);
    }
}