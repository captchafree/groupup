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

    public FirebaseUser getCurrentUser() {
        return this.currentUser;
    }

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

    public void signOut() {
        auth.signOut();
    }
}
