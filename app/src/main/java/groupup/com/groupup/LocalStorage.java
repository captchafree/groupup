package groupup.com.groupup;

import android.app.Activity;
import android.content.SharedPreferences;

public class LocalStorage {

    private SharedPreferences pref;

    public LocalStorage(Activity activity) {
        pref = activity.getSharedPreferences(LocalDataKeys.PREFERENCES.toString(), activity.MODE_PRIVATE);
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key) {
        return pref.getString(key, null);
    }
}
