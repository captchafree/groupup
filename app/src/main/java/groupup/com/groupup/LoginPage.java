package groupup.com.groupup;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginPage extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mauth;
    private EditText em;
    private EditText pass;
    private TextView mess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        /*Button button = findViewById(R.id.loginButton);
        button.setOnClickListener(new PageTransitionListener(this, Homepage.class));*/
        findViewById(R.id.loginButton).setOnClickListener(this);
        findViewById((R.id.createAccountButton)).setOnClickListener(this);
        findViewById(R.id.contButton).setOnClickListener(new PageTransitionListener(this, Homepage.class));
        findViewById(R.id.signOutButton).setOnClickListener(this);

        em = findViewById(R.id.login_name);
        pass = findViewById(R.id.login_2);
        mess = findViewById((R.id.signedInMess));

        /*this.testUserSave();*/

        mauth = FirebaseAuth.getInstance();


    }

    public void onStart(){
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mauth.getCurrentUser();
        updateUI(currentUser);
    }

    private void createAccount(final String email, String password){
        final UserStorage userDatabase = UserStorage.getInstance().init(this);

        mauth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mauth.getCurrentUser();

                            User u = new User("");
                            u.setID(user.getUid());
                            u.setName(email);

                            userDatabase.addUser(u);
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginPage.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    public void signIn(String email, String password){
        mauth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mauth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginPage.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if(user!= null){
            findViewById(R.id.loginButton).setVisibility(View.GONE);
            findViewById((R.id.createAccountButton)).setVisibility(View.GONE);
            findViewById(R.id.login_name).setVisibility(View.GONE);
            findViewById(R.id.login_2).setVisibility(View.GONE);
            findViewById(R.id.signOutButton).setVisibility(View.VISIBLE);
            findViewById(R.id.contButton).setVisibility(View.VISIBLE);
            findViewById((R.id.signedInMess)).setVisibility(View.VISIBLE);
            String messout = "You are signed in as ";
            messout = messout + user.getEmail();
            mess.setText(messout);
        }
        else{
            findViewById(R.id.loginButton).setVisibility(View.VISIBLE);
            findViewById((R.id.createAccountButton)).setVisibility(View.VISIBLE);
            findViewById(R.id.login_name).setVisibility(View.VISIBLE);
            findViewById(R.id.login_2).setVisibility(View.VISIBLE);
            findViewById(R.id.signOutButton).setVisibility(View.GONE);
            findViewById(R.id.contButton).setVisibility(View.GONE);
            findViewById((R.id.signedInMess)).setVisibility(View.GONE);
        }
    }

    private void signOut() {
        mauth.signOut();
        updateUI(null);
    }

    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.createAccountButton) {
            createAccount(em.getText().toString(), pass.getText().toString());
        } else if (i == R.id.loginButton) {
            signIn(em.getText().toString(), pass.getText().toString());
        } else if (i == R.id.signOutButton) {
            signOut();
        }
        /*} else if (i == R.id.verifyEmailButton) {
            sendEmailVerification();
        }*/
    }

    private void testUserSave() {
        User joe = new User("Joe");
        joe.addGroup("soccer", "lacrosse");
        joe.setBio("Hi, I'm Joe");
        joe.setProfileImage("Base64EncodedImage_Joe");

        UserStorage userDatabase = UserStorage.getInstance().init(this);
        userDatabase.addUser(joe);

        System.out.println("Joe's UID: " + joe.getID());

        try {
            userDatabase.setCurrentUser(joe);
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        User test = null;
        try {
            test = userDatabase.getCurrentUser();
        } catch(Exception e) {
            System.out.println(e.toString());
        }

        if(test == null)
            System.out.println("Couldn't load user!");
        else
            System.out.println("Test user is " + test.getName());

        /*User jane = new User("Jane");
        jane.addGroup("cooking", "singing", "yoga");
        jane.setBio("Hi, I'm Jane");
        jane.setProfileImage("Base64EncodedImage_Jane");

        userDatabase.addUser(jane);

        System.out.println("Jane's UID: " + jane.getID());*/
    }
}
