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

public class GroupSearch extends AppCompatActivity {

    private static final String TAG = "GroupSearch";

    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mIDs = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();

    private ArrayList<Group> results = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_search);

        Log.d(TAG, "onCreate: started.");
        initImageBitmaps();

        final EditText searchField = (EditText) findViewById(R.id.groupSearchBar);

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

        System.out.println("Getting current user");
        GroupQuery.getCurrentUser();
    }

    private void initImageBitmaps() {
        Log.d(TAG, "initBitmaps: preparing bitmaps");

        initRecyclerView();
    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: init recyclerView.");
        RecyclerView recyclerView = findViewById(R.id.recyclerv_view);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, mNames, mIDs, mImageUrls);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void refreshView() {
        mNames.clear();
        mImageUrls.clear();

        for(Group g : results) {
            mNames.add(g.getName());
            mImageUrls.add("https://goo.gl/JTSgZX");
        }

        this.initRecyclerView();
    }

    private void search(String activity) {
        GroupQuery.getGroupsWithActivity(this, activity, results);
    }
}
