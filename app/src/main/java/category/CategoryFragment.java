package category;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.behkha.chaarpaye.R;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import category.categoryItems.CategoryItemsActivity;
import utils.RecyclerItemClickListener;
import utils.Server;

/**
 * Created by User on 3/6/2018.
 */

public class CategoryFragment extends Fragment {

    private Context mContext;
    private RequestQueue mRequestQueue;
    private final String TAG = "CATEGORY_REQUEST_QUEUE_TAG";
    private RecyclerView mCategoryRecyclerView;
    private CategoryAdapter mCategoryAdapter;
    private GridLayoutManager mCategoryGridLayoutManager;
    private ArrayList<Category> mCategories = new ArrayList<>();
    AVLoadingIndicatorView mProgressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.category_fragment_layout , container , false);
        this.mContext = view.getContext();

        mProgressBar = view.findViewById(R.id.category_fragment_progress_bar);

        mCategoryRecyclerView = view.findViewById(R.id.category_fragment_recycler_view);
        mCategoryAdapter = new CategoryAdapter(mContext,mCategories);
        mCategoryRecyclerView.setAdapter(mCategoryAdapter);
        mCategoryGridLayoutManager = new GridLayoutManager(mContext,2,GridLayoutManager.VERTICAL,false);
        mCategoryRecyclerView.setLayoutManager(mCategoryGridLayoutManager);
        mCategoryRecyclerView.setHasFixedSize(true);
        mCategoryGridLayoutManager.setAutoMeasureEnabled(true);
        addOnCategoriesRecyclerViewItemClickListener();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState == null || !savedInstanceState.containsKey("CATEGORIES"))
            initializeCategory();
        else
            restoreCategory(savedInstanceState);
    }

    private void initializeCategory() {
        mProgressBar.setVisibility(View.VISIBLE);
        mCategoryRecyclerView.setVisibility(View.GONE);
        getCategories();
    }
    private void restoreCategory(Bundle saveInstanceState){
        mCategories = saveInstanceState.getParcelableArrayList("CATEGORIES");
        int position = saveInstanceState.getInt("POSITION");
        mCategoryAdapter = new CategoryAdapter(mContext,mCategories);
        mCategoryRecyclerView.setAdapter(mCategoryAdapter);
        mCategoryRecyclerView.scrollToPosition(position);
    }

    private void getCategories(){
        String url = Server.DOMAIN + Server.API + "/categories";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            addCategories(response);
                            mProgressBar.setVisibility(View.GONE);
                            mCategoryRecyclerView.setVisibility(View.VISIBLE);
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
                });
        addToRequestQueue(request,TAG);
    }
    private void addCategories(JSONArray jsonArray) throws JSONException {
        int size = jsonArray.length();
        for (int i = 0; i < size; i++) {
            mCategories.add( getCategory( jsonArray.getJSONObject(i) ) );
        }
        mCategoryAdapter.notifyDataSetChanged();
    }
    private Category getCategory(JSONObject jsonObject) throws JSONException {
        Category category = new Category();

        String id = jsonObject.getString("id");
        String image = jsonObject.getString("image");
        String name =jsonObject.getString("name");
        int position = jsonObject.getInt("position");

        category.setId(id);
        category.setImage(image);
        category.setName(name);
        category.setPosition(position);

        return category;
    }

    private void addOnCategoriesRecyclerViewItemClickListener(){
        mCategoryRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Category category = mCategories.get(position);
                Intent intent = new Intent(mContext,CategoryItemsActivity.class);
                intent.putExtra("CATEGORY",category);
                startActivity(intent);
            }
        }));
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
        if (mCategories.size() > 0){
            outState.putParcelableArrayList("CATEGORIES",mCategories);
            outState.putInt("POSITION", mCategoryGridLayoutManager.findFirstVisibleItemPosition());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
        cancelAllRequests(TAG);
    }
}
