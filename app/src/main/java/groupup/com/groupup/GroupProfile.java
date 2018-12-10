package groupup.com.groupup;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

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

public class GroupProfile extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

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
        manager.getGroupWithIdentifier(GroupKeys.ID, currentGroup.getID(), new GetDataListener() {
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
     * Displays a popup menu when the Options button is pressed
     */
    public void showPopup(View v){
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        PopupMenu popup = new PopupMenu(this, v);

        popup.setOnMenuItemClickListener(this);
        Menu menuOptions = popup.getMenu();
        popup.inflate(R.menu.group_profile_popup);

        //Hide options that an owner cant choose
        if (currentGroup.getOwner().equals(userID)) {
            menuOptions.getItem(1).setVisible(false);
            if (!currentGroup.isWaitlistGroup())
                menuOptions.getItem(4).setVisible(false);
        }
        else { //Hide options that a nonowner cant choose
            menuOptions.getItem(3).setVisible(false); //
            menuOptions.getItem(4).setVisible(false);
        }

        popup.show();
    }

    /**
     * Listens for and handles a button in the popup menu to be clicked
     * @param item the button that was pressed in menu
     * @return
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Intent intent;
        switch(item.getItemId()){
            case R.id.refresh: //Refresh the page
                //recreate();
                onRestart();
                this.onRestart();
                break;
            case R.id.leave: //Leave the group
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
                break;
            case R.id.chat: //Navigate to group chatroom
                this.transitionToPage(GroupCommunicationPage.class, "GROUP_NAME");
                break;
            case R.id.edit: //Navigate to the edit group page
                this.transitionToPage(EditGroup.class, "GROUP_ID");
                break;
            case R.id.waitlist: //Navigate to the group waitlist
                this.transitionToPage(GroupWaitlistPage.class, "group");
                break;
            default:
                return false;

        }
        return true;
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
        final TextView name = findViewById(R.id.group_info);
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