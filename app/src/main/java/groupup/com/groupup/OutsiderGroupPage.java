package groupup.com.groupup;

import android.nfc.Tag;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class OutsiderGroupPage extends AppCompatActivity {
    private static final String TAG = "OutsiderGroupPage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outsider_group_page);

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
            /* TO DO: using ArrayList<String> groupMemberIDs = currentGroup.getMembers();
             *      1. Query firebase using each user's ID to get their name
             *      2. Add names to the member string
             *      3. Send member string to setImage
             *
             *      Or create an adapter to query and add the names to an arraylist & display
             */

            setImage(imageUrl, groupName, groupLocation, groupActivity);
        }
    }

    private void setImage(String imageUrl, String groupName, String groupLocation, String groupActivity){
        Log.d(TAG, "setImage: setting the image and name to widgets");

        TextView name = findViewById(R.id.group_description);
        name.setText(groupName + ": " + groupActivity + "\n\t At " + groupLocation + "\n\nMembers:");

        ImageView image = findViewById(R.id.image);

        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(image);
    }
}
