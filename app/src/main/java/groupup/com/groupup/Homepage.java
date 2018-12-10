package groupup.com.groupup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import groupup.com.groupup.Authentication.Authenticator;
import groupup.com.groupup.Database.DatabaseManager;
import groupup.com.groupup.Database.GetDataListener;
import groupup.com.groupup.Database.GroupKeys;
import groupup.com.groupup.Database.UserKeys;
import groupup.com.groupup.LocationServices.LocationServiceManager;
import groupup.com.groupup.LocationServices.Permissions;

public class Homepage extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private static final String TAG = "Homepage";
    private ArrayList<Group> results = new ArrayList<>();
    TextView view;

    /**
     * Initializes the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        this.view = findViewById(R.id.userInformation);
        this.setupUserView(view);

        setTitle(" Homepage ");
    }

    /**
     * Refreshes the page after a child activity finishes
     */
    @Override
    public void onRestart() {  // After a pause OR at startup
        super.onRestart();
        Log.d(TAG, "onRestart: Refreshing user's list of groups");

        //Rebuild the list of groups the user is in
        this.view = findViewById(R.id.userInformation);
        results.clear();
        this.setupUserView(view);
    }

    /**
     * Displays a popup menu when the Navigate button is pressed
     */
    public void showPopup(View v){
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.nav_popup);
        popup.show();
    }

    /**
     * Listens for and handles a button in the popup menu to be clicked
     * @param item the button that was pressed in menu
     * @return
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Intent intent;
        switch(item.getItemId()){
            case R.id.search: //Navigate to the search page
                intent = new Intent(this, GroupSearch.class);
                break;
            case R.id.create: //Navigate to the group creation page
                intent = new Intent(this, CreateGroup.class);
                break;
            case R.id.edit_profile: //Navigate to the edit user profile page
                intent = new Intent(this, EditUser.class);
                break;
            default: //Refresh the homepage
                intent = new Intent(this, Homepage.class);

        }
        this.startActivity(intent);
        return true;
    }

    /**
     * Query firebase for the users information and display it on the screen.
     * @param view the text view to display the user's information in.
     */
    private void setupUserView(final TextView view) {
        final DatabaseManager db = DatabaseManager.getInstance();
        final String userID = Authenticator.getInstance().getCurrentUser().getUid();

        //Query firebase the user's information
        db.getUserWithIdentifier(UserKeys.ID, userID, new GetDataListener() {
            @Override
            public void onSuccess(DataSnapshot data) {
                User user = data.getValue(User.class);
                String text = "";

                if(user.getName().equals(""))
                {
                    user.setName("");
                    db.updateUserWithID(userID, user);
                    text += "Name: Anonymous" + "\n";
                }
                else
                    text += "Name: " + user.getName() + "\n";

                text += "Email: " + user.getEmail() + "\n";
                text += "Latitude: " + user.getLatitude() + "\n";
                text += "Longitude: " + user.getLongitude() + "\n";

                view.setText(text); //Display the user's information
                List<String> userGroupIDs = user.getGroups();

                //Query firebase to find the groups that the user is in
                for(String groupID : userGroupIDs) {
                    db.getGroupWithIdentifier(GroupKeys.ID, groupID, new GetDataListener() {
                        @Override
                        public void onSuccess(DataSnapshot data) {
                            //Add the group and refresh the page
                            results.add(data.getValue(Group.class));
                            refreshView();
                        }

                        @Override
                        public void onFailure(DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onFailure(DatabaseError error) {

            }
        });
    }

    /**
     * Refreshes the recyclerview
     */
    public void refreshView() {
        Log.d(TAG, "refreshView: " + results.size());
        this.initRecyclerView();
    }

    /**
     * Displays the user's list of groups in a recyclerview
     */
    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init recyclerView.");
        RecyclerView recyclerView = findViewById(R.id.homepage_recycview);

        //Create an adapter to display the groups' information in a recyclerview
        GroupRVA adapter = new GroupRVA(this, results, GroupProfile.class);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}