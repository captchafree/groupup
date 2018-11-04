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
        if(getIntent().hasExtra("image_url") && getIntent().hasExtra("group_name"))
        {
            Log.d(TAG, "getIncomingIntent: found intent extras.");

            String imageUrl = getIntent().getStringExtra("image_url");
            String groupName = getIntent().getStringExtra("group_name");

            setImage(imageUrl, groupName);
        }
    }

    private void setImage(String imageUrl, String groupName){
        Log.d(TAG, "setImage: setting the image and name to widgets");

        TextView name = findViewById(R.id.group_description);
        name.setText(groupName);

        ImageView image = findViewById(R.id.image);
        
        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(image);
    }
}
