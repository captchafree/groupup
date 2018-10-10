package groupup.com.groupup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class PageTransitionListener implements View.OnClickListener {

    private AppCompatActivity from;
    private Class to;

    public PageTransitionListener(AppCompatActivity from, Class to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public void onClick(View view) {
        this.from.startActivity(new Intent(this.from, this.to));
    }
}
