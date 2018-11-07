package groupup.com.groupup;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import groupup.com.groupup.Authentication.AuthCompletionListener;
import groupup.com.groupup.Authentication.Authenticator;
import groupup.com.groupup.Database.DatabaseManager;
import groupup.com.groupup.Database.GetDataListener;
import groupup.com.groupup.Database.UserKeys;

public class LoginPage extends AppCompatActivity implements View.OnClickListener {

    private EditText em;
    private EditText pass;
    private TextView mess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        findViewById(R.id.loginButton).setOnClickListener(this);
        findViewById((R.id.createAccountButton)).setOnClickListener(this);
        findViewById(R.id.contButton).setOnClickListener(new PageTransitionListener(this, Homepage.class));
        findViewById(R.id.signOutButton).setOnClickListener(this);

        em = findViewById(R.id.login_name);
        pass = findViewById(R.id.login_2);
        mess = findViewById((R.id.signedInMess));

        this.runTests();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        updateUI(Authenticator.getInstance().getCurrentUser());
    }

    private void createAccount(final String email, String password) {
        final Authenticator auth = Authenticator.getInstance();
        auth.createAccount(email, password, new AuthCompletionListener() {
            @Override
            public void onSuccess() {
                updateUI(auth.getCurrentUser());
            }

            @Override
            public void onFailure() {
                Toast.makeText(LoginPage.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                updateUI(null);
            }
        });
    }

    public void signIn(String email, String password) {
        final Authenticator auth = Authenticator.getInstance();
        auth.signIn(email, password, new AuthCompletionListener() {
            @Override
            public void onSuccess() {
                updateUI(auth.getCurrentUser());
            }

            @Override
            public void onFailure() {
                updateUI(null);
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        int loggedIn, loggedOut;

        if (user != null) {
            loggedOut = View.GONE;
            loggedIn = View.VISIBLE;
            mess.setText("You are signed in as " + user.getEmail());
        } else {
            loggedOut = View.VISIBLE;
            loggedIn = View.GONE;
        }

        findViewById(R.id.loginButton).setVisibility(loggedOut);
        findViewById((R.id.createAccountButton)).setVisibility(loggedOut);
        findViewById(R.id.login_name).setVisibility(loggedOut);
        findViewById(R.id.login_2).setVisibility(loggedOut);
        findViewById(R.id.signOutButton).setVisibility(loggedIn);
        findViewById(R.id.contButton).setVisibility(loggedIn);
        findViewById((R.id.signedInMess)).setVisibility(loggedIn);
    }

    private void signOut() {
        Authenticator.getInstance().signOut();
        updateUI(null);
    }

    @Override
    public void onClick(View view) {
        int buttonID = view.getId();
        if (buttonID == R.id.createAccountButton) {
            createAccount(em.getText().toString(), pass.getText().toString());
        } else if (buttonID == R.id.loginButton) {
            signIn(em.getText().toString(), pass.getText().toString());
        } else if (buttonID == R.id.signOutButton) {
            signOut();
        }
        /*} else if (buttonID == R.id.verifyEmailButton) {
            sendEmailVerification();
        }*/
    }

    private void runTests() {
        DatabaseManager manager = DatabaseManager.getInstance();

        manager.getUserWithIdentifier(UserKeys.ID, "QpwspLnTQbWuoKfZBPrVYPg8UFg2", new GetDataListener() {
            @Override
            public void onSuccess(DataSnapshot data) {
                User user = data.getValue(User.class);
                System.out.println("Retrieved user \"" + user.getName() + "\"");
            }

            @Override
            public void onFailure(DatabaseError error) {}
        });
    }
}
