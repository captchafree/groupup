package groupup.com.groupup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import groupup.com.groupup.Database.DatabaseManager;
import groupup.com.groupup.Database.GetDataListener;
import groupup.com.groupup.Database.GroupKeys;

public class EditGroup extends AppCompatActivity implements View.OnClickListener {

    private EditText name, activity, location, picture;
    private Button save;

    private volatile Group currentGroup;

    private String groupID;

    @Override
    //called when the page is first created. contains set up stuff
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);

        name = findViewById(R.id.edit_group_name);
        activity = findViewById(R.id.edit_group_activity);
        location = findViewById(R.id.edit_group_location);
        picture = findViewById(R.id.edit_group_picture);

        save = findViewById(R.id.edit_group_save);
        save.setOnClickListener(this);

        groupID = getIntent().getStringExtra("GROUP_ID");

        this.initView();
        setTitle(" Edit Group ");

    }

    //creates the appropriate view information from the database
    private void initView() {
        DatabaseManager manager = DatabaseManager.getInstance();

        manager.getGroupWithIdentifier(GroupKeys.ID, groupID, new GetDataListener() {
            @Override
            public void onSuccess(DataSnapshot data) {
                currentGroup = data.getValue(Group.class);

                name.setText(currentGroup.getName());
                activity.setText(currentGroup.getActivity());
                location.setText(currentGroup.getLocation());
                picture.setText(currentGroup.getPicture());
            }

            @Override
            public void onFailure(DatabaseError error) {}
        });
    }

    //saves the applied changes to the database
    private void saveChanges() {
        currentGroup.setName(name.getText().toString().trim());
        currentGroup.setActivity(activity.getText().toString().trim());
        currentGroup.setLocation(location.getText().toString().trim());
        currentGroup.setPicture(picture.getText().toString().trim());

        DatabaseManager manager = DatabaseManager.getInstance();
        manager.updateGroupWithID(currentGroup.getID(), currentGroup);
        finish();
    }

    @Override
    //event listener for the save button on this page
    public void onClick(View v) {
        this.saveChanges();
    }
}
