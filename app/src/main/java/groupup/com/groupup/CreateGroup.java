package groupup.com.groupup;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class CreateGroup extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextActivity;
    private EditText editTextLocation;
    private EditText editTextPicture;
    private Button buttonAdd;

    DatabaseReference databaseGroups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        /*
         * Saves data to "groups" tab in Firebase
         */
        databaseGroups = FirebaseDatabase.getInstance().getReference("groups");

        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextActivity = (EditText) findViewById(R.id.editTextActivity);
        editTextLocation = (EditText) findViewById(R.id.editTextLocation);
        editTextPicture = (EditText) findViewById(R.id.editTextPicture);
        buttonAdd = findViewById(R.id.buttonAdd);

        /*
         * When "Create Group" button is clicked, do addGroup();
         */
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGroup();
            }
        });
    }

    /*
     * Takes in the string from name, activity, and location lines along with a randomly
     * generate id and stores it in a Group object. Saves the group object under "groups"
     * If any of the string from name, activity or location are missing, return "Please fill in..."
     */
    private void addGroup(){
        String name = editTextName.getText().toString().trim();
        String activity = editTextActivity.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();
        String imageURL = editTextPicture.getText().toString().trim();
        String picture = (imageURL.length() == 0) ? "https://goo.gl/JTSgZX" : imageURL;

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(activity) && !TextUtils.isEmpty(location)){
            final String id = databaseGroups.push().getKey();
            Group group = new Group(id, name, activity.toUpperCase(), location, picture);
            FirebaseUser athUs = FirebaseAuth.getInstance().getCurrentUser();
            group.setOwner(athUs.getUid());
            group.addMember(athUs.getUid());
            GroupQuery.getUserWithID(FirebaseAuth.getInstance().getCurrentUser().getUid(), new Callback() {
                @Override
                public void onCallback(Object value) {
                    User user = (User) value;
                    user.addGroup(id);
                    GroupQuery.updateUserWithID(user.getID(),user);
                }
            });
            databaseGroups.child(id).setValue(group);
            Toast.makeText(this, "Group successfully added", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this, "Please fill in the missing information", Toast.LENGTH_LONG).show();
        }
    }


}
