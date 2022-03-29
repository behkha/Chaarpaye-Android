package collection.addToCollection;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.behkha.chaarpaye.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import collection.Collection;
import database.DatabaseController;
import event.Event;
import place.Place;
import utils.Fonts;
import utils.Server;
import utils.UserInfo;

/**
 * Created by User on 3/23/2018.
 */

public class AddToCollectionActivity extends AppCompatActivity {

    private Context mContext;
    private Event mEvent;
    private Place mPlace;
    private RequestQueue mRequestQueue;
    private final String TAG = "ADD_TO_COLLECTION_REQUEST_QUEUE_TAG";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_to_collection_activity_layout);
        mContext = getApplicationContext();
        addToCollectionVisited();

        mEvent = getIntent().getParcelableExtra("EVENT");
        mPlace = getIntent().getParcelableExtra("PLACE");

        TextView title = (TextView) findViewById(R.id.add_to_collection_dialog_title);
        title.setTypeface(Fonts.get_iran_sans_bold_font(mContext));

        TextView action = (TextView) findViewById(R.id.add_to_collection_dialog_action);
        action.setTypeface(Fonts.get_iran_sans_font(mContext));
        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onActionClick();
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.add_to_collection_dialog_recycler_view);
        AddToCollectionAdapter adapter = new AddToCollectionAdapter(mContext, UserInfo.getCollections(),mEvent,mPlace);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }


    public void onActionClick(){
        ArrayList<Collection> collections = UserInfo.getCollections();
        for (int i = 0; i < collections.size(); i++) {

            Collection collection = collections.get(i);
            if (collection.isMarked()){
                bookmark(collection.getId());
            } else {
                if (mEvent != null)
                    deleteEventBookmark(collection.getId(),mEvent.getId());
                else
                    deletePlaceBookmark(collection.getId(),mPlace.getId());
            }

        }
        finish();
    }
    private void deleteEventBookmark(final String collection_id , String event_id){
        String url = Server.DOMAIN + Server.API + "/user/collections/" + collection_id + "/bookmarks/events/" + event_id;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                            if (message.equals("resource unbookmarked successfully")){
                                removeFromDatabase(collection_id);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Server.errorHandler(mContext,error);
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return UserInfo.getHeader(mContext);
            }};
        addToRequestQueue(request,TAG);
    }
    private void deletePlaceBookmark(final String collection_id , String place_id){
        String url = Server.DOMAIN + Server.API + "/user/collections/" + collection_id + "/bookmarks/places/" + place_id;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                            if (message.equals("resource unbookmarked successfully")){
                                removeFromDatabase(collection_id);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Server.errorHandler(mContext,error);
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return UserInfo.getHeader(mContext);
            }};
        addToRequestQueue(request,TAG);
    }
    private void bookmark(final String collection_id){
        String url = Server.DOMAIN + Server.API + "/user/collections/" + collection_id + "/bookmarks";
        JSONObject postParams = new JSONObject();
        try {

            if (mEvent != null){
                postParams.put("id",mEvent.getId());
                postParams.put("type","event");
            } else {
                postParams.put("id",mPlace.getId());
                postParams.put("type","place");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, postParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                            if (message.equals("resource bookmarked successfully")){
                                addToDatabase(collection_id);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Server.errorHandler(mContext,error);
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return UserInfo.getHeader(mContext);
            }};
        addToRequestQueue(request,TAG);
    }

    private void removeFromDatabase(String collection_id){
        DatabaseController db = new DatabaseController(mContext);
        if (mEvent != null){
            db.removeEvent(mEvent.getId(),collection_id);
        } else {
            db.removePlace(mPlace.getId(),collection_id);
        }
        db.close();
    }
    private void addToDatabase(String collection_id){
        DatabaseController db = new DatabaseController(mContext);
        if (mEvent != null){
            db.addEvent(mEvent.getId(),collection_id);
        } else
            db.addPlace(mPlace.getId(),collection_id);
        db.close();
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

    private void addToCollectionVisited(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("ACTIVITY_SWITCH_LISTENER",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("VISITED",true);
        editor.apply();
    }

    @Override
    protected void onStop() {
        super.onStop();
        cancelAllRequests(TAG);
    }
}
