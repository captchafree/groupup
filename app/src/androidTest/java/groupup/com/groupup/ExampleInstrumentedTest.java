package groupup.com.groupup;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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

    private volatile boolean passed = false;

    @Test
    public void getUserWithID_isCorrect() throws Exception {

        DatabaseManager manager = DatabaseManager.getInstance();

        final CountDownLatch latch = new CountDownLatch(1);

        manager.getUserWithIdentifier(UserKeys.ID, "-LR8a7T87UJmupF9soSJ", new GetDataListener() {
            @Override
            public void onSuccess(DataSnapshot data) {
                User user = data.getValue(User.class);
                fail("Failed");
                if(user.getEmail().equals("josh@beck.com")) {
                    passed = true;
                }

                latch.countDown();
            }

            @Override
            public void onFailure(DatabaseError error) {

            }
        });

        latch.await(5, TimeUnit.SECONDS);
    }
}
