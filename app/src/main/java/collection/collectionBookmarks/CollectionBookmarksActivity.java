package collection.collectionBookmarks;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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

import event.Event;
import event.EventActivity;
import place.Place;
import place.PlaceActivity;
import posts.Author;
import posts.City;
import posts.Link;
import posts.Location;
import posts.Media;
import posts.Meta;
import posts.Photographer;
import posts.Tag;
import utils.Fonts;
import utils.RecyclerItemClickListener;
import utils.Server;
import utils.UserInfo;

/**
 * Created by User on 3/21/2018.
 */

public class CollectionBookmarksActivity extends AppCompatActivity {

    private Context mContext;
    private RequestQueue mRequestQueue;
    private final String TAG = "COLLECTION_BOOKMARKS_REQUEST_QUEUE_TAG";
    private String mCollectionId;

    private AVLoadingIndicatorView mProgressBar;
    private TextView mResponseMessage;

    private RecyclerView mCollectionBookmarksRecyclerView;
    private ArrayList<Bookmark> mBookmarks = new ArrayList<>();
    private BookmarksAdapter mBookmarksAdapter;
    private LinearLayoutManager mBookmarksLinearLayoutManager;
    private int mBookmarksPage = 1;
    private boolean mLoading = false;
    private String mNextPageUrl = "null";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collection_bookmarks_activity_layout);
        mContext = getApplicationContext();

        mProgressBar = (AVLoadingIndicatorView) findViewById(R.id.collection_bookmarks_progress_bar);
        ImageView mBack = (ImageView) findViewById(R.id.collection_bookmarks_back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        String title = getIntent().getStringExtra("COLLECTION_NAME");
        TextView mTitle = (TextView) findViewById(R.id.collection_bookmarks_title);
        mTitle.setText(title);
        mTitle.setTypeface(Fonts.get_iran_sans_bold_font(mContext));

        mCollectionId =  getIntent().getStringExtra("COLLECTION_ID");

        mResponseMessage = (TextView) findViewById(R.id.collection_bookmarks_response_message);
        mResponseMessage.setTypeface(Fonts.get_iran_sans_bold_font(mContext));

        mCollectionBookmarksRecyclerView = (RecyclerView) findViewById(R.id.collection_bookmarks_recycler_view);
        mBookmarksAdapter = new BookmarksAdapter(mContext,mBookmarks);
        mBookmarksLinearLayoutManager = new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
        mCollectionBookmarksRecyclerView.setAdapter(mBookmarksAdapter);
        mCollectionBookmarksRecyclerView.setLayoutManager(mBookmarksLinearLayoutManager);

        addOnBookmarksRecyclerViewScrollListener();
        addOnBookmarksRecyclerViewItemClickListener();

        if (savedInstanceState == null || !savedInstanceState.containsKey("BOOKMARKS"))
            initialzeBookmarks();
        else
            restoreBookmarks(savedInstanceState);
    }

    private void initialzeBookmarks(){
        mProgressBar.setVisibility(View.VISIBLE);
        mCollectionBookmarksRecyclerView.setVisibility(View.INVISIBLE);
        mLoading = true;
        getBookmarks(mBookmarksPage);
    }
    private void restoreBookmarks(Bundle savedInstanceState){
        mBookmarks = savedInstanceState.getParcelableArrayList("BOOKMARKS");
        int position = savedInstanceState.getInt("POSITION");
        mNextPageUrl = savedInstanceState.getString("NEXT_PAGE_URL");
        mBookmarksPage = savedInstanceState.getInt("PAGE");
        mBookmarksAdapter = new BookmarksAdapter(mContext,mBookmarks);
        mCollectionBookmarksRecyclerView.setAdapter(mBookmarksAdapter);
        mCollectionBookmarksRecyclerView.scrollToPosition(position);
    }


    private void getBookmarks(int page){
        String url = Server.DOMAIN + Server.API + "/user/collections/" + mCollectionId + "?page=" + page;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray data = response.getJSONArray("data");
                            addBookmarks(data);
                            Log.i("count",String.valueOf(mBookmarks.size()));
                            mLoading = false;
                            mProgressBar.setVisibility(View.GONE);
                            mCollectionBookmarksRecyclerView.setVisibility(View.VISIBLE);
                            if (mBookmarks.size() == 0){
                                mCollectionBookmarksRecyclerView.setVisibility(View.GONE);
                                mResponseMessage.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return UserInfo.getHeader(mContext);
            }};
        addToRequestQueue(request,TAG);
    }
    private void addBookmarks(JSONArray jsonArray) throws JSONException{
        int size = jsonArray.length();
        for (int i = 0; i < size; i++) {
            mBookmarks.add( getBookmark( jsonArray.getJSONObject(i) ) );
        }
        mBookmarksAdapter.notifyDataSetChanged();
    }
    private Bookmark getBookmark(JSONObject jsonObject) throws JSONException{
        Bookmark bookmark = new Bookmark();

        String collection_id = jsonObject.getString("collection_id");
        String user_id = jsonObject.getString("user_id");
        String bookmarlable_id = jsonObject.getString("bookmarkable_id");
        String bookmarkable_type = jsonObject.getString("bookmarkable_type");
        JSONObject bookmarkable = jsonObject.getJSONObject("bookmarkable");
        Place place = null;
        Event event = null;
        switch (bookmarkable_type){
            case "event":
                event = getEvent(bookmarkable);
                break;
            case "place":
                place = getPlace(bookmarkable);
                break;
        }

        bookmark.setCollection_id(collection_id);
        bookmark.setUser_id(user_id);
        bookmark.setBookmarkable_id(bookmarlable_id);
        bookmark.setBookamarkable_type(bookmarkable_type);
        bookmark.setPlace(place);
        bookmark.setEvent(event);

        return bookmark;
    }
    private Place getPlace(JSONObject jsonObject) throws JSONException{
        Place place = new Place();

        String id = jsonObject.getString("id");
        String name = jsonObject.getString("name");
        String content = jsonObject.getString("content");
        String address = jsonObject.getString("address");
        Location location = getLocation( jsonObject.getString("location") );
        ArrayList<Media> medias = getMedias( jsonObject.getJSONArray("media") );
        Link link;
        Meta meta;
        City city;
        ArrayList<Author> authors;
        ArrayList<Photographer> photographers;
        ArrayList<Tag> tags;
        try {
            link = getLink( jsonObject.getJSONObject("links") );
        }catch (JSONException ex){
            link = null;
        }
        try {
            meta = getMeta( jsonObject.getJSONObject("meta") );
        }catch (JSONException ex){
            meta = null;
        }
        try {
            city = getCity( jsonObject.getJSONObject("city") );
        } catch (JSONException ex) {
            city = null;
        }
        try {
            authors = getAuthors( jsonObject.getJSONArray("authors") );
        } catch (JSONException ex){
            authors = null;
        }
        try {
            photographers = getPhotographers( jsonObject.getJSONArray("photographers") );
        } catch (JSONException ex){
            photographers = null;
        }
        try {
            tags = getTags( jsonObject.getJSONArray("tags") );
        } catch (JSONException ex){
            tags = null;
        }

        place.setId(id);
        place.setName(name);
        place.setContent(content);
        place.setAddress(address);
        place.setLocation(location);
        place.setMedias(medias);
        place.setLink(link);
        place.setMeta(meta);
        place.setCity(city);
        place.setAuthors(authors);
        place.setPhotographers(photographers);
        place.setTags(tags);

        return place;
    }
    private Event getEvent(JSONObject jsonObject) throws JSONException{
        Event event = new Event();

        String id = jsonObject.getString("id");
        String title = jsonObject.getString("title");
        String content = jsonObject.getString("content");
        ArrayList<Media> medias = getMedias( jsonObject.getJSONArray("media") );
        Link link;
        Meta meta;
        Place place;
        ArrayList<Author> authors;
        ArrayList<Photographer> photographers;
        ArrayList<Tag> tags;
        try {
            link = getLink( jsonObject.getJSONObject("links") );
        }catch (JSONException ex){
            link = null;
        }
        try {
            meta = getMeta( jsonObject.getJSONObject("meta") );
        }catch (JSONException ex){
            meta = null;
        }
        try {
            place = getPlace( jsonObject.getJSONObject("place") );
        }catch (JSONException ex){
            ex.printStackTrace();
            place = null;
        }
        try {
            authors = getAuthors( jsonObject.getJSONArray("authors") );
        } catch (JSONException ex){
            authors = null;
        }
        try {
            photographers = getPhotographers( jsonObject.getJSONArray("photographers") );
        } catch (JSONException ex){
            photographers = null;
        }
        try {
            tags = getTags( jsonObject.getJSONArray("tags") );
        } catch (JSONException ex){
            tags = null;
        }


        event.setId(id);
        event.setTitle(title);
        event.setContent(content);
        event.setMedias(medias);
        event.setLink(link);
        event.setMeta(meta);
        event.setPlace(place);
        event.setAuthors(authors);
        event.setPhotographers(photographers);
        event.setTags(tags);

        return event;

    }
    private Location getLocation(String location){
        String[] ll = location.split(",");
        double longitude = Double.valueOf( ll[0] );
        double latitude = Double.valueOf( ll[1] );
        return new Location(longitude,latitude);
    }
    private City getCity(JSONObject jsonObject) throws JSONException{
        City city = new City();

        String id = jsonObject.getString("id");
        String name = jsonObject.getString("name");
        String image = jsonObject.getString("image");

        city.setId(id);
        city.setName(name);
        city.setImage(image);

        return city;
    }
    private ArrayList<Tag> getTags(JSONArray jsonArray) throws JSONException{
        int size = jsonArray.length();
        ArrayList<Tag> tags = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            tags.add( getTag( jsonArray.getJSONObject(i) ) );
        }
        return tags;
    }
    private Tag getTag(JSONObject jsonObject) throws JSONException{
        Tag tag = new Tag();

        String id = jsonObject.getString("id");
        String name = jsonObject.getString("name");
        String tagable_id = jsonObject.getJSONObject("pivot").getString("tagable_id");
        String tag_id = jsonObject.getJSONObject("pivot").getString("tag_id");
        String tagable_type = jsonObject.getJSONObject("pivot").getString("tagable_type");

        tag.setId(id);
        tag.setName(name);
        tag.setTagable_id(tagable_id);
        tag.setTag_id(tag_id);
        tag.setTagable_type(tagable_type);

        return tag;
    }
    private ArrayList<Photographer> getPhotographers(JSONArray jsonArray) throws JSONException{
        int size = jsonArray.length();
        ArrayList<Photographer> photographers = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            photographers.add( getPhotographer( jsonArray.getJSONObject(i) ) );
        }
        return photographers;
    }
    private Photographer getPhotographer(JSONObject jsonObject) throws JSONException{
        Photographer photographer = new Photographer();

        String id = jsonObject.getString("id");
        String name = jsonObject.getString("name");
        String photographerable_id = jsonObject.getJSONObject("pivot").getString("photographerable_id");
        String photographer_id = jsonObject.getJSONObject("pivot").getString("photographer_id");
        String photographerable_type = jsonObject.getJSONObject("pivot").getString("photographerable_type");

        photographer.setId(id);
        photographer.setName(name);
        photographer.setPhotographerable_id(photographerable_id);
        photographer.setPhotographer_id(photographer_id);
        photographer.setPhotographerable_type(photographerable_type);

        return photographer;
    }
    private ArrayList<Author> getAuthors(JSONArray jsonArray) throws JSONException{
        int size = jsonArray.length();
        ArrayList<Author> authors = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            authors.add( getAuthor( jsonArray.getJSONObject(i) ) );
        }
        return authors;
    }
    private Author getAuthor(JSONObject jsonObject) throws JSONException{
        Author author = new Author();

        String id = jsonObject.getString("id");
        String name = jsonObject.getString("name");
        String authorable_id = jsonObject.getJSONObject("pivot").getString("authorable_id");
        String author_id = jsonObject.getJSONObject("pivot").getString("author_id");
        String authorable_type = jsonObject.getJSONObject("pivot").getString("authorable_type");

        author.setId(id);
        author.setName(name);
        author.setAuthorable_id(authorable_id);
        author.setAuthor_id(author_id);
        author.setAuthorable_type(authorable_type);

        return author;
    }
    private Link getLink(JSONObject jsonObject) throws JSONException{
        Link link = new Link();
        String webSite = jsonObject.getJSONObject("website").getString("link");
        link.setWebSite(webSite);
        return link;
    }
    private Meta getMeta(JSONObject jsonObject) throws JSONException{
        Meta meta = new Meta();
        return meta;
    }
    private ArrayList<Media> getMedias(JSONArray jsonArray) throws JSONException{
        int size = jsonArray.length();
        ArrayList<Media> medias = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            medias.add( getMedia( jsonArray.getJSONObject(i) ) );
        }
        return medias;
    }
    private Media getMedia(JSONObject jsonObject) throws JSONException{
        Media media = new Media();
        String type = jsonObject.getString("type");
        String name = jsonObject.getString("name");
        String path = jsonObject.getString("path");
        String preview_path;
        try {
            preview_path = jsonObject.getString("preview_path");
        } catch (JSONException ex) {
            preview_path = null;
        }
        media.setType(type);
        media.setName(name);
        media.setPath(path);
        media.setPreview_path(preview_path);
        return media;
    }

    private void addOnBookmarksRecyclerViewScrollListener(){
        mCollectionBookmarksRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (mLoading)
                    return;
                if (mNextPageUrl.equals("null"))
                    return;

                int visibleItemCount = mBookmarksLinearLayoutManager.getChildCount();
                int totalItemCount = mBookmarksLinearLayoutManager.getItemCount();
                int pastVisibleItems = mBookmarksLinearLayoutManager.findFirstVisibleItemPosition();
                if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                    //End of list
                    getBookmarks(++mBookmarksPage);
                    mLoading = true;
                }
            }
        });
    }
    private void addOnBookmarksRecyclerViewItemClickListener(){
        mCollectionBookmarksRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Bookmark bookmark = mBookmarks.get(position);
                switch (bookmark.getBookamarkable_type()){
                    case "event":
                        Intent eventIntent = new Intent(mContext , EventActivity.class);
                        eventIntent.putExtra("EVENT",bookmark.getEvent());
                        startActivity(eventIntent);
                        break;
                    case "place":
                        Intent placeIntent = new Intent(mContext , PlaceActivity.class);
                        placeIntent.putExtra("PLACE",bookmark.getPlace());
                        startActivity(placeIntent);
                        break;
                }
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
    protected void onSaveInstanceState(Bundle outState) {
        if (mBookmarks.size() > 0){
            outState.putParcelableArrayList("BOOKMARKS",mBookmarks);
            outState.putInt("POSITION",mBookmarksLinearLayoutManager.findFirstVisibleItemPosition());
            outState.putString("NEXT_PAGE_URL",mNextPageUrl);
            outState.putInt("PAGE",mBookmarksPage);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        cancelAllRequests(TAG);
    }
}
