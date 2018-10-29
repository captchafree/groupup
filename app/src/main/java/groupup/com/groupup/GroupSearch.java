package groupup.com.groupup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;

public class GroupSearch extends AppCompatActivity {

    private static final String TAG = "GroupSearch";

    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mIDs = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_search);

        Log.d(TAG, "onCreate: started.");
        initImageBitmaps();
    }

    private void initImageBitmaps(){
        Log.d(TAG, "initBitmaps: preparing bitmaps");
        mImageUrls.add("https://goo.gl/DMAvZ6");
        mNames.add("Basketball");

        mImageUrls.add("https://goo.gl/s4g5By");
        mNames.add("Study Group");

        mImageUrls.add("https://goo.gl/tBBuCU");
        mNames.add("Bingo");

        mImageUrls.add("https://goo.gl/JTSgZX");
        mNames.add("Dummy");
        mImageUrls.add("https://goo.gl/JTSgZX");
        mNames.add("Dummy1");
        mImageUrls.add("https://goo.gl/JTSgZX");
        mNames.add("Dummy2");
        mImageUrls.add("https://goo.gl/JTSgZX");
        mNames.add("Dummy3");
        mImageUrls.add("https://goo.gl/JTSgZX");
        mNames.add("Dummy4");
        mImageUrls.add("https://goo.gl/JTSgZX");
        mNames.add("Dummy5");

        initRecyclerView();
    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: init recyclerView.");
        RecyclerView recyclerView = findViewById(R.id.recyclerv_view);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, mNames, mIDs, mImageUrls);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

}
