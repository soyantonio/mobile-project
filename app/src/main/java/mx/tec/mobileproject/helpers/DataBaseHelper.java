package mx.tec.mobileproject.helpers;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.util.HashMap;
import java.util.Map;

import mx.tec.mobileproject.R;
import mx.tec.mobileproject.preferences.Preferences;

public class DataBaseHelper extends Application {
    private static final String API_END_POINT = "https://us-central1-devices-mobile-project.cloudfunctions.net/api/v0";
    private static final String API_LOGIN_POINT = "/users/username";
    private static final String API_DEVICES_POINT = "/devices";

    private final DataBaseInterface dataBaseInterface;
    private final Context context;

    public DataBaseHelper(Context context, DataBaseInterface dataBaseInterface) {
        this.context = context;
        this.dataBaseInterface = dataBaseInterface;
    }

    public DataBaseHelper(Context context) {
        this.context = context;
        dataBaseInterface = null;
    }

    public boolean isUserLogged() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            Preferences preferences = new Preferences(context);
            preferences.setUserEmail(user.getEmail());
            user.getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                @Override
                public void onSuccess(GetTokenResult getTokenResult) {
                    preferences.setTokenFirebase(getTokenResult.getToken());
                    getDevices();
                }
            });
        }
        return firebaseAuth.getCurrentUser() != null;
    }

    public void loginWithUsernameAndPassword(String email, String password) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null && task.getResult().getUser() != null) {
                    Preferences preferences = new Preferences(context);
                    FirebaseUser user = task.getResult().getUser();
                    preferences.setUserEmail(user.getEmail());
                    preferences.setTokenFirebase(user.getIdToken(false).toString());
                }
                if (dataBaseInterface != null) {
                    dataBaseInterface.onSuccess();
                }
            } else {
                if (dataBaseInterface != null) {
                    dataBaseInterface.onError();
                }
            }
        });
    }

    public void signUpWithUsernameAndPassword(String userName, String email, String password) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (dataBaseInterface != null) {
                    dataBaseInterface.onSuccess();
                }
                showMessage(context.getString(R.string.login_successful, userName));
                Preferences preferences = new Preferences(context);
                preferences.setUserEmail(email);
                preferences.setUserName(userName);
                if (task.getResult() != null && task.getResult().getUser() != null) {
                    FirebaseUser user = task.getResult().getUser();
                    preferences.setTokenFirebase(user.getIdToken(false).toString());
                }
            } else {
                if (dataBaseInterface != null) {
                    dataBaseInterface.onError();
                }
                showMessage("Error");
            }
        });
    }

    public void logout() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.getCurrentUser().delete();
    }

    public void updateUser(String userName, String email, String password) {

    }

    public void getDevices() {
        String url = API_END_POINT.concat(API_DEVICES_POINT);
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response != null) {
                            Log.d("Testing", "Response: " + response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headerMap = new HashMap<String, String>();
                headerMap.put("Content-Type", "application/json");
                headerMap.put("Authorization", "Bearer " + (new Preferences(context)).getTokenFirebase());
                Log.d("Testing", "Token: " + headerMap.get("Authorization"));
                return headerMap;
            }
        };
        queue.add(stringRequest);
    }

    private void showMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public interface DataBaseInterface {
        void onSuccess();
        void onError();
    }
}
