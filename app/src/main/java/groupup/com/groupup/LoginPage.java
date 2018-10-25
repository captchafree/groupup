package groupup.com.groupup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.SyncFailedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        Button button = findViewById(R.id.loginButton);
        button.setOnClickListener(new PageTransitionListener(this, Homepage.class));

        this.testUserSave();
    }

    private void testUserSave() {
        User joe = new User("Joe");
        joe.addGroup("soccer", "lacrosse");
        joe.setBio("Hi, I'm Joe");
        joe.setProfileImage("Base64EncodedImage_Joe");

        User jane = new User("Jane");
        jane.addGroup("cooking", "singing", "yoga");
        jane.setBio("Hi, I'm Jane");
        jane.setProfileImage("Base64EncodedImage_Jane");

        UserStorage userDatabase = UserStorage.getInstance();

        userDatabase.addUser(joe);
        userDatabase.addUser(jane);
    }
}
