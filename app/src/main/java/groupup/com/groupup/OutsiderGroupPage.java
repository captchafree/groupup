package groupup.com.groupup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import groupup.com.groupup.Database.DatabaseManager;
import groupup.com.groupup.Database.GetDataListener;
import groupup.com.groupup.Database.UserKeys;

public class OutsiderGroupPage extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "OutsiderGroupPage";
    private Group currentGroup;

    /**
     * Initializes the view with the necessary information
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outsider_group_page);
        findViewById(R.id.joinButton).setOnClickListener(this);
        getIncomingIntent();

        setTitle(" View Group ");
    }

    /**
     * Retrieves the previous intent that the user was on and gets any data that was sent from it.
     */
    private void getIncomingIntent() {
        Log.d(TAG, "getIncomingIntent: checking for incoming intents");
        if (getIntent().hasExtra("group")) {
            Log.d(TAG, "getIncomingIntent: found intent extras.");

            Group currentGroup = (Group) getIntent().getSerializableExtra("group");
            String imageUrl = currentGroup.getPicture();
            String groupName = currentGroup.getName();
            String groupLocation = currentGroup.getLocation();
            String groupActivity = currentGroup.getActivity();

            this.currentGroup = currentGroup;

            Button joinButton = findViewById(R.id.joinButton);

            if (this.currentGroup.isWaitlistGroup())
                joinButton.setText("Join Waitlist");

            setImage(imageUrl, groupName, groupLocation, groupActivity);
        }
    }

    /**
     * Overrides onClick to satisfy the OnClickListener interface
     *
     * @param v The view that was clicked
     */
    public void onClick(View v) {

        final int clickedButtonID = v.getId();
        final Group curr = this.currentGroup;
        final DatabaseManager manager = DatabaseManager.getInstance();
        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Retrieve the user with the current ID
        manager.getUserWithIdentifier(UserKeys.ID, userID, new GetDataListener() {
            @Override
            public void onSuccess(DataSnapshot data) {
                User user = data.getValue(User.class);
                boolean isMem = false;

                if (clickedButtonID == R.id.joinButton) {
                    for (String uid : curr.getMembers()) {
                        if (uid.equals(userID)) {
                            isMem = true;
                            Toast.makeText(OutsiderGroupPage.this, "You are already a member of this group", Toast.LENGTH_LONG).show();
                        }
                    }
                    for (String uid : curr.getWaitlistUsers()) {
                        if (uid.equals(userID)) {
                            isMem = true;
                            Toast.makeText(OutsiderGroupPage.this, "You are already in the waitlist of this group", Toast.LENGTH_LONG).show();
                        }
                    }
                    if (!isMem) {
                        Log.d(TAG, "Member is being added");
                        if (currentGroup.isWaitlistGroup()) {
                            currentGroup.addWaitlistUser(userID);
                        } else {
                            currentGroup.addMember(userID);
                            user.addGroup(currentGroup.getID());
                        }
                        manager.updateUserWithID(userID, user);
                        manager.updateGroupWithID(currentGroup.getID(), currentGroup);
                    }
                    finish();
                }
            }

            @Override
            public void onFailure(DatabaseError error) {

            }
        });

    }

    /**
     * Sets the image to the groups profile image and displays the group's information
     *
     * @param imageUrl      The url of the group's profile image
     * @param groupName     The name of the group
     * @param groupLocation The location of the group
     * @param groupActivity The activity of the group
     */
    private synchronized void setImage(final String imageUrl, final String groupName, final String groupLocation, final String groupActivity) {
        Log.d(TAG, "setImage: setting the image and name to widgets");

        final TextView name = findViewById(R.id.wlusers);
        setDisplayText(name, groupName + ": " + groupActivity + "\n\t Location: " +
                groupLocation + "\n\n" + this.currentGroup.getMembers().size() + " Members");

        ImageView image = findViewById(R.id.image);

        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(image);
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
