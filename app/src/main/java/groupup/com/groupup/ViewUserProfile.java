package groupup.com.groupup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class ViewUserProfile extends AppCompatActivity {
    private static final String TAG = "ViewUserProfile";
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_profile);

        getIncomingIntent();
    }

    private void getIncomingIntent(){
        Log.d(TAG, "getIncomingIntent: checking for incoming intents");
        if(getIntent().hasExtra("user"))
        {
            Log.d(TAG, "getIncomingIntent: found intent extras.");

            this.user = (User) getIntent().getSerializableExtra("user");
            createView();
        }
    }

    private void createView(){
        String text = "";
        text += "Name: " + user.getName() + "\n";
        text += "Email: " + user.getEmail() + "\n";
        text += "Bio: \n\n" + user.getBio();

        TextView view = findViewById(R.id.user_info);
        view.setText(text); //Display the user's information
    }
}
