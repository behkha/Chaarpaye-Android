package splash;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.behkha.chaarpaye.MainActivity;
import com.behkha.chaarpaye.R;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import database.DatabaseController;
import reigster.Register;
import utils.Fonts;
import utils.Server;
import utils.UserInfo;

/**
 * Created by User on 3/22/2018.
 */

public class SplashActivity extends AppCompatActivity{

    private Context mContext;
    private RequestQueue mRequestQueue;
    private final String TAG = "BOOKMARKS_REQUEST_TAG";
    private DatabaseController db;

    private TextView mTryAgain;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.splash_activity_layout);
        mContext = getApplicationContext();
        db = new DatabaseController(mContext);
        mTryAgain = (TextView) findViewById(R.id.splash_try_again);
        mTryAgain.setTypeface(Fonts.get_iran_sans_bold_font(mContext));
        mTryAgain.setVisibility(View.GONE);
        mTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTryAgainClick();
            }
        });
        if (UserInfo.hasToken(mContext))
            getBookmarks();
        else
            showSplash();
    }

    private void getBookmarks(){
        String url = Server.DOMAIN + Server.API + "/user/bookmarks";
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            db.removeAllEvents();
                            db.removeAllPlaces();
                            JSONArray eventsArray = response.getJSONArray("events");
                            JSONArray placesArray = response.getJSONArray("places");
                            addEventsBookmarks(eventsArray);
                            addPlacesBookmarks(placesArray);
                            db.close();
                            gotoNextActivity();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            mTryAgain.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Server.errorHandler(mContext,error);
                        mTryAgain.setVisibility(View.VISIBLE);
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return UserInfo.getHeader(mContext);
            }};
        addToRequestQueue(request,TAG);
    }
    private void addPlacesBookmarks(JSONArray jsonArray) throws JSONException{
        int size = jsonArray.length();
        for (int i = 0; i < size; i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String id = jsonObject.getString("id");
            String collection_id = jsonObject.getString("collection_id");
            db.addPlace(id,collection_id);
        }
    }
    private void addEventsBookmarks(JSONArray jsonArray) throws JSONException{
        int size = jsonArray.length();
        for (int i = 0; i < size; i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String id = jsonObject.getString("id");
            String collection_id = jsonObject.getString("collection_id");
            db.addEvent(id,collection_id);
        }
    }
    private void gotoNextActivity(){
        if (UserInfo.hasToken(mContext)){
            Intent intent = new Intent(mContext, MainActivity.class);
            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity(intent);
        } else {
            Intent intent = new Intent(mContext, Register.class);
            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity(intent);
        }
        finish();
    }
    private void showSplash(){
        long SPLASH_TIME_OUT = 3000;
        new Handler().postDelayed(new Runnable() {

			/*
			 * Showing splash screen with a timer. This will be useful when you
			 * want to show case your app logo / company
			 */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                if (UserInfo.hasToken(mContext)){
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(mContext, Register.class);
                    intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                    startActivity(intent);
                }
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    public void onTryAgainClick(){
        getBookmarks();
        mTryAgain.setVisibility(View.GONE);
    }

    private RequestQueue getRequestQueue(){
        if (mRequestQueue == null)
            mRequestQueue = Volley.newRequestQueue(mContext);
        return mRequestQueue;
    }
    private void addToRequestQueue(Request request , String tag){
        request.addMarker(tag);
        getRequestQueue().add(request);
    }
    private void cancelAllRequests(String tag){
        getRequestQueue().cancelAll(tag);
    }

    @Override
    protected void onStop() {
        super.onStop();
        cancelAllRequests(TAG);
    }
}
