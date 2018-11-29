package groupup.com.groupup;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import groupup.com.groupup.Authentication.Authenticator;
import groupup.com.groupup.Database.DatabaseManager;
import groupup.com.groupup.Database.GetDataListener;
import groupup.com.groupup.Database.UserKeys;
import groupup.com.groupup.LocationServices.LocationServiceManager;
import groupup.com.groupup.LocationServices.Permissions;

public class EditUser extends AppCompatActivity implements View.OnClickListener {

    private EditText name, email;
    private TextView bio;
    private Button save;

    private volatile User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        name = findViewById(R.id.edit_user_name);
        email = findViewById(R.id.edit_user_email);
        bio = findViewById(R.id.edit_user_bio);

        save = findViewById(R.id.edit_user_save);
        save.setOnClickListener(this);

        this.initView();
        setTitle(" Edit User ");

    }

    private void initView() {
        DatabaseManager manager = DatabaseManager.getInstance();
        String uid = Authenticator.getInstance().getCurrentUser().getUid();

        manager.getUserWithIdentifier(UserKeys.ID, uid, new GetDataListener() {
            @Override
            public void onSuccess(DataSnapshot data) {
                currentUser = data.getValue(User.class);

                name.setText(currentUser.getName());
                email.setText(currentUser.getEmail());
                bio.setText(currentUser.getBio());
            }

            @Override
            public void onFailure(DatabaseError error) {}
        });
    }

    private void saveChanges() {
        if(!Permissions.Check_FINE_LOCATION(this)) {
            Permissions.Request_FINE_LOCATION(this,22);
        }

        if(!Permissions.Check_COARSE_LOCATION(this)) {
            Permissions.Request_COARSE_LOCATION(this,22);
        }

        LocationServiceManager.LocationResult locationResult = new LocationServiceManager.LocationResult(){
            @Override
            public void gotLocation(Location location){

                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                currentUser.setLatitude(latitude);
                currentUser.setLongitude(longitude);

                currentUser.setName(name.getText().toString().trim());
                currentUser.setEmail(email.getText().toString().trim());
                currentUser.setBio(bio.getText().toString().trim());

                DatabaseManager manager = DatabaseManager.getInstance();
                manager.updateUserWithID(currentUser.getID(), currentUser);
            }
        };
        LocationServiceManager location = new LocationServiceManager();
        location.getLocation(this, locationResult);
    }

    @Override
    public void onClick(View v) {
        this.saveChanges();
    }
}
