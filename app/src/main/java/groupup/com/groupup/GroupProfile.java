package groupup.com.groupup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class GroupProfile extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "GroupProfile";
    private Group currentGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_group);

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

        final StringBuilder membersText = new StringBuilder();

        if(this.currentGroup.getMembers().size() != 0) {
            for (String uid : this.currentGroup.getMembers()) {
                GroupQuery.getUserWithID(uid, new Callback() {
                    @Override
                    public void onCallback(Object value) {
                        User user = (User) value;
                        System.out.println("name: " + user.getName());
                        membersText.append("\n" + user.getName());

                        setDisplayText(name, groupName + ": " + groupActivity + "\n\t At " + groupLocation + "\n\nMembers:" + membersText.toString());
                    }
                });
            }
        } else {
            this.setDisplayText(name, groupName + ": " + groupActivity + "\n\t At " + groupLocation + "\n\nMembers: None");
        }

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
