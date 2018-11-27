package groupup.com.groupup;

import android.nfc.Tag;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import groupup.com.groupup.Database.DatabaseManager;
import groupup.com.groupup.Database.GetDataListener;
import groupup.com.groupup.Database.GroupKeys;
import groupup.com.groupup.Database.UserKeys;

public class OutsiderGroupPage extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "OutsiderGroupPage";
    private Group currentGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outsider_group_page);
        findViewById(R.id.joinButton).setOnClickListener(this);
        getIncomingIntent();
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

        final View finalV = v;
        final Group curr = this.currentGroup;
        final DatabaseManager manager = DatabaseManager.getInstance();


        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        manager.getUserWithIdentifier(UserKeys.ID, userID, new GetDataListener() {
            @Override
            public void onSuccess(DataSnapshot data) {
                User user = data.getValue(User.class);
                int i = finalV.getId();
                boolean isMem = false;

                if (i == R.id.joinButton) {
                    for (String uid : curr.getMembers()) {
                        if(uid.equals(userID)){
                            isMem = true;
                            Toast.makeText(OutsiderGroupPage.this, "You are already a member of this group", Toast.LENGTH_LONG).show();
                        }
                    }
                    if(!isMem){
                        Log.d(TAG, "Member is being added");
                        currentGroup.addMember(userID);
                        user.addGroup(currentGroup.getID());
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

    private synchronized void setImage(final String imageUrl, final String groupName, final String groupLocation, final String groupActivity){
        Log.d(TAG, "setImage: setting the image and name to widgets");

        final TextView name = findViewById(R.id.group_description);
        setDisplayText(name, groupName + ": " + groupActivity + "\n\t Location: " +
                groupLocation + "\n\n" + this.currentGroup.getMembers().size() + " Members");

        ImageView image = findViewById(R.id.image);

        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(image);
    }

    private void setDisplayText(TextView view, String text) {
        view.setText(text);
    }
}
