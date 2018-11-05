package groupup.com.groupup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class Homepage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //comment my own change asdfgdp
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        Button GroupSearch_Button = findViewById(R.id.GroupSearch_Button);
        Button CreateGroup_Button = findViewById(R.id.CreateGroup_Button);
        Button GroupProfile_Button = findViewById(R.id.GroupProfile_Button);

        GroupSearch_Button.setOnClickListener(new PageTransitionListener(this, GroupSearch.class));
        CreateGroup_Button.setOnClickListener(new PageTransitionListener(this, CreateGroup.class));
        GroupProfile_Button.setOnClickListener(new PageTransitionListener(this, GroupProfile.class));

        this.setupUserView((TextView) findViewById(R.id.userInformation));
    }

    private void setupUserView(final TextView view) {
        GroupQuery.getUserWithID(FirebaseAuth.getInstance().getCurrentUser().getUid(), new Callback() {
            @Override
            public void onCallback(Object value) {
                User user = (User) value;

                String text = "";
                text += "Name: " + user.getName() + "\n";
                text += "Email: " + user.getEmail() + "\n";
                text += "Profile Image: " + user.getProfileImage() + "\n";
                view.setText(text);
            }
        });
    }
}