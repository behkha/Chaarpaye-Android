package collection;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.bumptech.glide.Glide;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import collection.collectionBookmarks.CollectionBookmarksActivity;
import database.DatabaseController;
import utils.Fonts;
import utils.Server;
import utils.UserInfo;

import static android.view.Gravity.BOTTOM;

/**
 * Created by User on 3/17/2018.
 */

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.CollectionViewHolder> {

    private Context mContext;
    private RequestQueue mRequestQueue;
    private DatabaseController db;
    private final String TAG = "COLLECTIONS_TAG";
    private ArrayList<Collection> mCollections;

    public CollectionAdapter(Context mContext, ArrayList<Collection> mCollections) {
        this.mContext = mContext;
        this.mCollections = mCollections;
        db = new DatabaseController(mContext);
    }

    public class CollectionViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        TextView placesCount;
        ImageView more;

        public CollectionViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.collection_item_image);
            title = itemView.findViewById(R.id.collection_item_title);
            placesCount = itemView.findViewById(R.id.collection_item_place_count);
            more = itemView.findViewById(R.id.collection_item_more);

            Typeface font = Fonts.get_iran_sans_font(mContext);
            title.setTypeface(font);
            placesCount.setTypeface(Fonts.get_iran_sans_fa_num_font(mContext));

        }
    }

    @Override
    public CollectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CollectionViewHolder(LayoutInflater.from(mContext).inflate(R.layout.collection_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(CollectionViewHolder holder, int position) {
        final Collection collection = mCollections.get(position);

        setCover(collection , holder);
        holder.title.setText(collection.getName());
        String placeCountText = String.valueOf(collection.getBookmarked_count()) + " بوکمارک ";
        holder.placesCount.setText(placeCountText);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener(collection);
            }
        });
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMoreClick(collection);
            }
        });
    }

    private boolean isGif(String url){
        return url.endsWith(".gif");
    }
    private void setCover(Collection collection , CollectionViewHolder holder){
        if (collection.getImage() == null || collection.getImage().equals("null")){
            holder.image.setImageResource(R.drawable.temp);
            return;
        }
        if ( isGif( collection.getImage() ) )
            Glide.with(mContext).load( "http://" + collection.getImage()).asGif().placeholder(R.color.white).centerCrop().into(holder.image);
        else
            Glide.with(mContext).load( "http://" + collection.getImage()).placeholder(R.color.white).centerCrop().into(holder.image);
    }
    private void onMoreClick(final Collection collection){
//        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.collections_more_dialog_layout,null);
//        builder.setView(dialogView);
//
//        AlertDialog dialog = builder.create();
//        dialog.getWindow().setGravity(Gravity.BOTTOM);
//        dialog.getWindow().setLayout( WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT );
//        dialog.show();
        final Dialog dialog = new Dialog(mContext, R.style.DialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        TextView edit = dialogView.findViewById(R.id.collection_more_edit);
        final TextView delete = dialogView.findViewById(R.id.collection_more_delete);
        edit.setTypeface(Fonts.get_iran_sans_font(mContext));
        delete.setTypeface(Fonts.get_iran_sans_font(mContext));

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeCollection(collection);
                if (dialog.isShowing())
                    dialog.dismiss();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUpdateCollectionDialog(collection);
                if (dialog.isShowing())
                    dialog.dismiss();
            }
        });


        dialog.show();
    }
    private void onItemClickListener(Collection collection){
        Intent intent = new Intent(mContext, CollectionBookmarksActivity.class);
        intent.putExtra("COLLECTION_NAME",collection.getName());
        intent.putExtra("COLLECTION_ID",collection.getId());
        mContext.startActivity(intent);
    }
    private void removeCollection(final Collection collection){
        String url = Server.DOMAIN + Server.API + "/user/collections/" + collection.getId();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mCollections.remove(collection);
                        UserInfo.mCollections.remove(collection);
                        db.removeCollection(collection.getId()  );
                        notifyDataSetChanged();
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
    private void updateCollection(final String collectionName , final Collection collection , final AlertDialog dialog , final AVLoadingIndicatorView progressBar){
        String url = Server.DOMAIN + Server.API + "/user/collections/" + collection.getId();
        JSONObject putParams = new JSONObject();
        try {
            putParams.put("name", collectionName );
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, putParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        progressBar.setVisibility(View.GONE);
                        collection.setName(collectionName);
                        UserInfo.getCollections().clear();
                        UserInfo.getCollections().addAll( mCollections );
                        notifyItemChanged( mCollections.indexOf(collection) );
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
    private void showUpdateCollectionDialog(final Collection collection){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.create_list_dialog_layout,null);
        builder.setView(dialogView);

        final EditText editText = dialogView.findViewById(R.id.create_new_list_dialog_edittext);
        final AVLoadingIndicatorView progressBar = dialogView.findViewById(R.id.create_new_list_dialog_progress_bar);
        TextView create = dialogView.findViewById(R.id.create_new_list_dialog_create);

        editText.setTypeface(Fonts.get_iran_sans_font(mContext));
        editText.setText(collection.getName());
        create.setTypeface(Fonts.get_iran_sans_bold_font(mContext));
        create.setText(R.string.edit);

        final AlertDialog dialog = builder.create();
        dialog.show();

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String collectionName = editText.getText().toString().trim();
                if (collectionName.isEmpty()){
                    Toast.makeText(mContext,"نام لیست نمی تواند خالی باشد", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                updateCollection(collectionName,collection,dialog,progressBar);
            }
        });
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
    public int getItemCount() {
        return mCollections.size();
    }
}
