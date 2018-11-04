package groupup.com.groupup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateGroup extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextActivity;
    private EditText editTextLocation;
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

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(activity) && !TextUtils.isEmpty(location)){
            String id = databaseGroups.push().getKey();
            Group group = new Group(id, name, activity.toUpperCase(), location);
            databaseGroups.child(id).setValue(group);
            Toast.makeText(this, "Group successfully added", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this, "Please fill in the missing information", Toast.LENGTH_LONG).show();
        }
    }

}
