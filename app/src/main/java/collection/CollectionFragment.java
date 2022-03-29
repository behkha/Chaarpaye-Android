package collection;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.behkha.chaarpaye.R;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import utils.Fonts;
import utils.Server;
import utils.UserInfo;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by User on 3/6/2018.
 */

public class CollectionFragment extends Fragment {

    private Context mContext;
    private RequestQueue mRequestQueue;
    private final String TAG = "COLLECTION_REQUEST_QUEUE_TAG";

    private RecyclerView mCollectionRecyclerView;
    private CollectionAdapter mCollectionsAdapter;
    private ArrayList<Collection> mCollections = new ArrayList<>();
    private LinearLayoutManager mCollectionLayoutManager;

    private AVLoadingIndicatorView mProgressBar;

    private TextView mResponseMessage;

    private boolean mLoading = false;
    private int mCollectionPage = 1;
    private String mCollectionNextPageUrl = "null";

    private TextView mCreateNewList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.collections_fragment_layout , container , false);
        this.mContext = view.getContext();

        mProgressBar = view.findViewById(R.id.collection_fragment_progress_bar);

        mResponseMessage = view.findViewById(R.id.collection_fragment_reponse_message);
        mCreateNewList = view.findViewById(R.id.collection_fragment_create_new_list);
        mCreateNewList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCreateNewListClick();
            }
        });

        mCreateNewList.setTypeface(Fonts.get_iran_sans_bold_font(mContext));
        mResponseMessage.setTypeface(Fonts.get_iran_sans_bold_font(mContext));

        mCollectionRecyclerView = view.findViewById(R.id.collections_fragment_recycler_view);
        mCollectionsAdapter = new CollectionAdapter(mContext,mCollections);
        mCollectionLayoutManager = new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
        mCollectionRecyclerView.setLayoutManager(mCollectionLayoutManager);
        mCollectionRecyclerView.setAdapter(mCollectionsAdapter);

        addOnCollectionsScrollListener();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState == null || !savedInstanceState.containsKey("COLLECTIONS"))
            initializeCollections();
        else
            restoreCollection(savedInstanceState);
    }

    private void initializeCollections(){
        mProgressBar.setVisibility(View.VISIBLE);
        mCollectionRecyclerView.setVisibility(View.GONE);
        mResponseMessage.setVisibility(View.GONE);
        getCollections(mCollectionPage);
    }
    private void restoreCollection(Bundle savedInstanceState){
        mCollections = savedInstanceState.getParcelableArrayList("COLLECTIONS");
        int position = savedInstanceState.getInt("POSITION");
        mCollectionPage = savedInstanceState.getInt("PAGE");
        mCollectionNextPageUrl = savedInstanceState.getString("NEXT_PAGE_URL");
        mCollectionsAdapter = new CollectionAdapter(mContext , mCollections);
        mCollectionRecyclerView.setAdapter(mCollectionsAdapter);
        mCollectionRecyclerView.scrollToPosition(position);
    }

    private void getCollections(int page){
        mLoading = true;
        String url = Server.DOMAIN + Server.API + "/user/collections?page=" + page;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            mProgressBar.setVisibility(View.GONE);
                            mCollectionRecyclerView.setVisibility(View.VISIBLE);
                            JSONArray data = response.getJSONArray("data");
                            UserInfo.mCollections.clear();
                            addCollections(data);
                            mLoading = false;
                            mCollectionNextPageUrl = response.getString("next_page_url");
                            Log.i("count",String.valueOf(mCollections.size()));
                            if (mCollections.size() == 0){
                                mResponseMessage.setVisibility(View.VISIBLE);
                                mProgressBar.setVisibility(View.GONE);
                                mCollectionRecyclerView.setVisibility(View.GONE);
                            } else {
                                mResponseMessage.setVisibility(View.GONE);
                                mProgressBar.setVisibility(View.GONE);
                                mCollectionRecyclerView.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            mProgressBar.setVisibility(View.GONE);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mProgressBar.setVisibility(View.GONE);
                        Server.errorHandler(mContext,error);
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return UserInfo.getHeader(mContext);
            }};
        addToRequestQueue(request,TAG);
    }
    private void addCollections(JSONArray jsonArray) throws JSONException{
        int size = jsonArray.length();
        for (int i = 0; i < size; i++) {
            Collection collection =  getCollection( jsonArray.getJSONObject(i));
            mCollections.add( collection );
            UserInfo.mCollections.add( collection );
        }
        mCollectionsAdapter.notifyDataSetChanged();
    }
    private Collection getCollection(JSONObject jsonObject) throws JSONException{
        Collection collection = new Collection();

        String id = jsonObject.getString("id");
        String name = jsonObject.getString("name");
        String image = jsonObject.getString("image");
        String user_id = jsonObject.getString("user_id");
        int bookmarked_count = jsonObject.getInt("bookmarks_count");

        collection.setId(id);
        collection.setName(name);
        collection.setImage(image);
        collection.setUser_id(user_id);
        collection.setBookmarked_count(bookmarked_count);

        return collection;
    }

    private void addOnCollectionsScrollListener(){
        mCollectionRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (mLoading)
                    return;
                if (mCollectionNextPageUrl.equals("null"))
                    return;

                int visibleItemCount = mCollectionLayoutManager.getChildCount();
                int totalItemCount = mCollectionLayoutManager.getItemCount();
                int pastVisibleItems = mCollectionLayoutManager.findFirstVisibleItemPosition();
                if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                    //End of list
                    getCollections(++mCollectionPage);
                    mLoading = true;
                }
            }
        });
    }

    public void onCreateNewListClick(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.create_list_dialog_layout,null);
        builder.setView(dialogView);

        final EditText editText = dialogView.findViewById(R.id.create_new_list_dialog_edittext);
        final AVLoadingIndicatorView progressBar = dialogView.findViewById(R.id.create_new_list_dialog_progress_bar);
        TextView create = dialogView.findViewById(R.id.create_new_list_dialog_create);

        editText.setTypeface(Fonts.get_iran_sans_font(mContext));
        editText.setCursorVisible(true);
        create.setTypeface(Fonts.get_iran_sans_bold_font(mContext));

        final AlertDialog dialog = builder.create();
        dialog.show();

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String collectionName = editText.getText().toString().trim();
                if (collectionName.isEmpty()){
                    Toast.makeText(mContext,"نام لیست نمی تواند خالی باشد",Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                createCollection(collectionName , dialog , progressBar);
            }
        });

    }

    private void createCollection(final String collectionName , final AlertDialog dialog , final AVLoadingIndicatorView mProgressBar){
        String url = Server.DOMAIN + Server.API + "/user/collections";
        JSONObject postParams = new JSONObject();
        try {
            postParams.put("name",collectionName);
        } catch (JSONException ex){
            ex.printStackTrace();
            return;
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, postParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                            if (message.equals("resource created successfully")){
                                dialog.dismiss();
                                mProgressBar.setVisibility(View.GONE);
                                mCollections.clear();
                                mCollectionPage = 1;
                                mCollectionNextPageUrl = "null";
                                UserInfo.getCollections().clear();
                                initializeCollections();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            dialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Server.errorHandler(mContext,error);
                        dialog.dismiss();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return UserInfo.getHeader(mContext);
            }};
        addToRequestQueue(request,TAG);
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
    public void onSaveInstanceState(Bundle outState) {
        if (mCollections.size() > 0){
            outState.putParcelableArrayList("COLLECTIONS",mCollections);
            outState.putInt("POSITION",mCollectionLayoutManager.findFirstVisibleItemPosition());
            outState.putInt("PAGE",mCollectionPage);
            outState.putString("NEXT_PAGE_URL",mCollectionNextPageUrl);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
        cancelAllRequests(TAG);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser)
            if (mCollections.size() == 0) {
                mProgressBar.setVisibility(View.GONE);
                mCollectionRecyclerView.setVisibility(View.GONE);
                mResponseMessage.setVisibility(View.VISIBLE);
            }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isAddToCollectionVisited()) {
            UserInfo.getCollections().clear();
            mCollections.clear();
            mCollectionRecyclerView.invalidate();
            initializeCollections();
            clearAddToCollectionVisitData();
        }
    }

    private boolean isAddToCollectionVisited(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("ACTIVITY_SWITCH_LISTENER",MODE_PRIVATE);
        return sharedPreferences.getBoolean("VISITED",false);
    }
    private void clearAddToCollectionVisitData(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("ACTIVITY_SWITCH_LISTENER",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("VISITED",false);
        editor.apply();
    }


}
