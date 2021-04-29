package mx.tec.mobileproject.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {
    private final Context context;

    private static final String TIME_CONNECTED = "TIME_CONNECTED";
    private static final String USER_NAME = "USER_NAME";

    public Preferences(Context context) {
        this.context = context;
    }

    public void setTimeConnected(String timeConnected) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(TIME_CONNECTED, timeConnected);
        editor.apply();
    }

    public String getTimeConnected() {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(TIME_CONNECTED, "00:00");
    }

    public void setUserName(String userName) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(USER_NAME, userName);
        editor.apply();
    }

    public String getUserName() {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(USER_NAME, "");
    }
}
