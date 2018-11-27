package groupup.com.groupup;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import java.util.ArrayList;

import groupup.com.groupup.Database.DatabaseManager;

public class GroupSearch extends AppCompatActivity {

    private static final String TAG = "GroupSearch";

    private ArrayList<Group> results = new ArrayList<>();

    @Override
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
    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init recyclerView.");
        RecyclerView recyclerView = findViewById(R.id.recyclerv_view);
        GroupRVA adapter = new GroupRVA(this, results, OutsiderGroupPage.class);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void refreshView() {
        this.initRecyclerView();
    }

    private void search(String activity) {
        DatabaseManager manager = DatabaseManager.getInstance();
        manager.getGroupsWithActivity(this, activity, results);
    }
}
