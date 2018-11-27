package groupup.com.groupup;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CompletableFuture;

import groupup.com.groupup.Database.DatabaseManager;
import groupup.com.groupup.Database.GetDataListener;
import groupup.com.groupup.Database.UserKeys;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("groupup.com.groupup", appContext.getPackageName());
    }

    @Test
    public void getUserWithID_isCorrect() {
        DatabaseManager manager = DatabaseManager.getInstance();
        final CompletableFuture<String> future = new CompletableFuture<>();

        manager.getUserWithIdentifier(UserKeys.ID, "-LR8a7T87UJmupF9soSJ", new GetDataListener() {
            @Override
            public void onSuccess(DataSnapshot data) {
                User user = data.getValue(User.class);

                future.complete(user.getName());
                assertEquals(user.getEmail(), "josh@beck.com");
            }

            @Override
            public void onFailure(DatabaseError error) {

            }
        });

        //assertEquals(future.get(), "Josh");
    }
}
