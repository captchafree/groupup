package groupup.com.groupup.Database;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public interface GetDataListener {
    void onSuccess(DataSnapshot data);
    void onFailure(DatabaseError error);
}
