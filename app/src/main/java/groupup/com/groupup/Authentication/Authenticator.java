package groupup.com.groupup.Authentication;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import groupup.com.groupup.Database.DatabaseManager;
import groupup.com.groupup.User;

/**
 * Handles firebase authentication
 */
public class Authenticator {

    private static final Authenticator instance = new Authenticator();

    public static Authenticator getInstance() {
        return instance;
    }

    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    private Authenticator() {
        this.auth = FirebaseAuth.getInstance();
        this.currentUser = auth.getCurrentUser();
    }

    /**
     * Returns the current logged-in user
     * @return The current user
     */
    public FirebaseUser getCurrentUser() {
        return this.currentUser;
    }

    /**
     * Creates a new account
     * @param email The email to be associated with the account
     * @param password The password to be associated with the account
     * @param listener A listener called when the account is finished being created
     */
    public void createAccount(final String email, final String password, final AuthCompletionListener listener) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            currentUser = auth.getCurrentUser();

                            User user = new User("");
                            user.setID(currentUser.getUid());
                            user.setEmail(email);

                            DatabaseManager manager = DatabaseManager.getInstance();
                            manager.addUser(user);

                            listener.onSuccess();
                        } else {
                            listener.onFailure();
                        }
                    }
                });
    }

    /**
     * Attempts to log in a user with the specified credentials
     * @param email The email address of the user
     * @param password The password of the user
     * @param listener A listener that is called when an attempt at signing in is completed
     */
    public void signIn(final String email, final String password, final AuthCompletionListener listener) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            currentUser = auth.getCurrentUser();
                            listener.onSuccess();
                        } else {
                            listener.onFailure();
                        }
                    }
                });
    }

    /**
     * Logs the current user out
     */
    public void signOut() {
        auth.signOut();
    }
}
