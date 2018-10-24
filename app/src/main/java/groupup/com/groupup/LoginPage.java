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

        this.testUserClass();
    }

    private void testUserClass() {
        User joe = new User("Joe");
        joe.addGroup("soccer", "lacrosse");
        joe.setBio("Hi, I'm Joe");
        joe.setProfileImage("Base64EncodedImage_Joe");

        User jane = new User("Jane");
        jane.addGroup("cooking", "singing", "yoga");
        jane.setBio("Hi, I'm Jane");
        jane.setProfileImage("Base64EncodedImage_Jane");

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("server/saving-data/user_test");

        DatabaseReference usersRef = ref.child("users");

        List<User> users = new ArrayList<>();
        users.add(joe);
        users.add(jane);

        usersRef.setValue(users, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    System.out.println("Data could not be saved " + databaseError.getMessage());
                } else {
                    System.out.println("Data saved successfully.");
                }
            }
        });
    }
}
