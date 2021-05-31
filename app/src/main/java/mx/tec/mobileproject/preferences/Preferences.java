package mx.tec.mobileproject.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {
    private final Context context;

    private static final String TOKEN_FIREBASE = "TOKEN_FIREBASE";
    private static final String TIME_CONNECTED = "TIME_CONNECTED";
    private static final String USER_NAME = "USER_NAME";
    private static final String USER_EMAIL = "USER_EMAIL";

    public Preferences(Context context) {
        this.context = context;
    }

    public void setTokenFirebase(String tokenFirebase) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(TOKEN_FIREBASE, tokenFirebase);
        editor.apply();
    }

    public String getTokenFirebase() {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(TOKEN_FIREBASE, "");
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

    public void setUserEmail(String userEmail) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(USER_EMAIL, userEmail);
        editor.apply();
    }

    public String getUserEmail() {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(USER_EMAIL, "");
    }
}