package groupup.com.groupup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class Homepage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //comment my own change asdfg
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        Button button = findViewById(R.id.button);

        button.setOnClickListener(new PageTransitionListener(this, GroupSearch.class));
    }

}
