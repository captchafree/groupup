package groupup.com.groupup;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;

import groupup.com.groupup.Database.DatabaseManager;

public class GroupSearch extends AppCompatActivity {

    private static final String TAG = "GroupSearch";

    private ArrayList<Group> results = new ArrayList<>();

    private ImageButton searchBtn;

    @Override
    //called on intial creation of the activity. contains set up and initiation things
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_search);

        Log.d(TAG, "onCreate: started.");

        final EditText searchField = findViewById(R.id.groupSearchBar);

        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                results.clear();
                refreshView();
                search(searchField.getText().toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        search("");
        setTitle(" Group Search ");


        searchBtn = (ImageButton) findViewById(R.id.searchButton);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                results.clear();
                refreshView();
                search(searchField.getText().toString().trim());
            }
        });

    }

    //creates/updates the viewer object to handle the list of the groups being displayed
    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init recyclerView.");
        RecyclerView recyclerView = findViewById(R.id.recyclerv_view);
        GroupRVA adapter = new GroupRVA(this, results, OutsiderGroupPage.class);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    //called when the list needs to be refreshed
    public void refreshView() {
        this.initRecyclerView();
    }

    //called as text is added to the search bar, this function calls the database manager to search the database for groups with matching activities
    private void search(String activity) {
        DatabaseManager manager = DatabaseManager.getInstance();
        manager.getGroupsWithActivity(this, activity, results);
    }
}
