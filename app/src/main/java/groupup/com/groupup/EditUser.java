package groupup.com.groupup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import groupup.com.groupup.Authentication.Authenticator;
import groupup.com.groupup.Database.DatabaseManager;
import groupup.com.groupup.Database.GetDataListener;
import groupup.com.groupup.Database.UserKeys;

public class EditUser extends AppCompatActivity implements View.OnClickListener {

    private EditText name, email;
    private Button save;

    private volatile User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        name = findViewById(R.id.edit_user_name);
        email = findViewById(R.id.edit_user_email);
        save = findViewById(R.id.edit_user_save);
        save.setOnClickListener(this);

        this.initView();
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
            }

            @Override
            public void onFailure(DatabaseError error) {}
        });
    }

    private void saveChanges() {
        currentUser.setName(name.getText().toString().trim());
        currentUser.setEmail(email.getText().toString().trim());

        DatabaseManager manager = DatabaseManager.getInstance();
        manager.updateUserWithID(currentUser.getID(), currentUser);
    }

    @Override
    public void onClick(View v) {
        this.saveChanges();
    }
}
