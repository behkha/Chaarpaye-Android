package category.categoryItems;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.behkha.chaarpaye.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import category.Category;
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

/**
 * Created by User on 3/22/2018.
 */

public class CategoryItemsActivity extends AppCompatActivity {

    private Context mContext;
    private RequestQueue mRequestQueue;
    private final String TAG = "CATEGORY_ITEMS_REQUEST_QUEUE_TAG";
    private Category mCategory;
    private ImageView mLom;
    private AVLoadingIndicatorView mProgressBar;
    private TextView mResponseMessage;

    private RecyclerView mCategoryItemsRecyclerView;
    private CategoryItemsAdapter mCategoryItemsAdapter;
    private LinearLayoutManager mCategoryItemsLinearLayoutManager;
    private ArrayList<CategoryItem> mCategoryItems = new ArrayList<>();
    private int mCategoryItemsPage = 1;
    private boolean mCategoryItemsLoading = false;
    private String mCategoryItemsNextPage = "null";

    private RecyclerView mCategoryItemsBottomRecyclerView;
    private CategoryItemsBottomAdapter mBottomAdapter;
    private ArrayList<CategoryItem> mBottomCategoryItems = new ArrayList<>();

    private MapView mCategoryItemsMapView;
    private ArrayList<Marker> mMarkers = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_items_activity_layout);
        mContext = getApplicationContext();

        ImageView mBack = (ImageView) findViewById(R.id.category_items_back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackClick();
            }
        });

        mCategory = getIntent().getParcelableExtra("CATEGORY");
        mProgressBar = (AVLoadingIndicatorView) findViewById(R.id.category_items_progress_bar);

        mLom = (ImageView) findViewById(R.id.category_items_lom);
        mLom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLomClick();
            }
        });

        TextView mTitle = (TextView) findViewById(R.id.category_items_title);
        mTitle.setText(mCategory.getName());
        mTitle.setTypeface(Fonts.get_iran_sans_font(mContext));

        mResponseMessage = (TextView) findViewById(R.id.category_items_response_message);
        mResponseMessage.setTypeface(Fonts.get_iran_sans_bold_font(mContext));

        mCategoryItemsRecyclerView = (RecyclerView) findViewById(R.id.category_items_recycler_view);
        mCategoryItemsAdapter = new CategoryItemsAdapter(mContext, mCategoryItems);
        mCategoryItemsLinearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mCategoryItemsRecyclerView.setAdapter(mCategoryItemsAdapter);
        mCategoryItemsRecyclerView.setLayoutManager(mCategoryItemsLinearLayoutManager);
        addOnCategoryItemsRecyclerViewItemClickListener();
        addOnCategoryItemsRecyclerViewScrollListener();

        mCategoryItemsMapView = (MapView) findViewById(R.id.category_items_map_view);
        mCategoryItemsMapView.onCreate(savedInstanceState);

        mCategoryItemsBottomRecyclerView = (RecyclerView) findViewById(R.id.category_items_bottom_recycler_view);
        mBottomAdapter = new CategoryItemsBottomAdapter(mContext, mBottomCategoryItems);
        LinearLayoutManager mBottomLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mCategoryItemsBottomRecyclerView.setAdapter(mBottomAdapter);
        mCategoryItemsBottomRecyclerView.setLayoutManager(mBottomLayoutManager);
        addOnCategoryBottomItemsRecyclerViewItemClickListener();
        setSnapHelper();


        if (savedInstanceState == null || !savedInstanceState.containsKey("PLACES"))
            initializeCategoryItems();
        else
            restoreCategoryItems(savedInstanceState);
    }

    private void initializeCategoryItems() {
        mProgressBar.setVisibility(View.VISIBLE);
        mCategoryItemsMapView.setVisibility(View.GONE);
        mCategoryItemsBottomRecyclerView.setVisibility(View.GONE);
        getCategoryItems(mCategoryItemsPage);
    }

    private void restoreCategoryItems(Bundle savedInstanceState) {
        mProgressBar.setVisibility(View.GONE);
        mCategoryItemsRecyclerView.setVisibility(View.VISIBLE);
        mCategoryItems = savedInstanceState.getParcelableArrayList("PLACES");
        int position = savedInstanceState.getInt("POSITION");
        mCategoryItemsAdapter = new CategoryItemsAdapter(mContext, mCategoryItems);
        mCategoryItemsRecyclerView.setAdapter(mCategoryItemsAdapter);
        mCategoryItemsRecyclerView.scrollToPosition(position);
    }

    private void getCategoryItems(int page) {
        String url = Server.DOMAIN + Server.API + "/categories/" + mCategory.getId() + "?page=" + page;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if (response.length() <= 0) {
                                mCategoryItemsNextPage = "finished";
                                return;
                            }
                            addCategoryItems(response);
                            initializeBottomCategoryItems();
                            if (mBottomCategoryItems.size() <= 0)
                                mLom.setVisibility(View.GONE);
                            mCategoryItemsLoading = false;
                            mProgressBar.setVisibility(View.GONE);
                            mCategoryItemsRecyclerView.setVisibility(View.VISIBLE);
                            if (mCategoryItems.size() <= 0) {
                                mCategoryItemsRecyclerView.setVisibility(View.GONE);
                                mCategoryItemsMapView.setVisibility(View.GONE);
                                mCategoryItemsBottomRecyclerView.setVisibility(View.GONE);
                                mResponseMessage.setVisibility(View.VISIBLE);
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
                        Server.errorHandler(mContext, error);
                    }
                });
        addToRequestQueue(request, TAG);
    }

    private void addCategoryItems(JSONArray jsonArray) throws JSONException {
        int size = jsonArray.length();
        for (int i = 0; i < size; i++) {
            mCategoryItems.add(getCategoryItem(jsonArray.getJSONObject(i)));
        }
        mCategoryItemsAdapter.notifyDataSetChanged();
    }

    private CategoryItem getCategoryItem(JSONObject jsonObject) throws JSONException {
        Event event = null;
        Place place = null;
        String object_type = jsonObject.getString("object_type");
        switch (object_type) {
            case "place":
                place = getPlace(jsonObject);
                break;
            case "event":
                event = getEvent(jsonObject);
                break;
        }
        return new CategoryItem(place, event, object_type);
    }

    private Place getPlace(JSONObject jsonObject) throws JSONException {
        Place place = new Place();

        String id = jsonObject.getString("id");
        String name = jsonObject.getString("name");
        String content = jsonObject.getString("content");
        String address = jsonObject.getString("address");
        Location location = getLocation(jsonObject.getString("location"));
        ArrayList<Media> medias = getMedias(jsonObject.getJSONArray("media"));
        Link link;
        Meta meta;
        City city;
        ArrayList<Author> authors;
        ArrayList<Photographer> photographers;
        ArrayList<Tag> tags;
        try {
            link = getLink(jsonObject.getJSONObject("links"));
        } catch (JSONException ex) {
            link = null;
        }
        try {
            meta = getMeta(jsonObject.getJSONObject("meta"));
        } catch (JSONException ex) {
            meta = null;
        }
        try {
            city = getCity(jsonObject.getJSONObject("city"));
        } catch (JSONException ex) {
            city = null;
        }
        try {
            authors = getAuthors(jsonObject.getJSONArray("authors"));
        } catch (JSONException ex) {
            authors = null;
        }
        try {
            photographers = getPhotographers(jsonObject.getJSONArray("photographers"));
        } catch (JSONException ex) {
            photographers = null;
        }
        try {
            tags = getTags(jsonObject.getJSONArray("tags"));
        } catch (JSONException ex) {
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

    private Event getEvent(JSONObject jsonObject) throws JSONException {
        Event event = new Event();

        String id = jsonObject.getString("id");
        String title = jsonObject.getString("title");
        String content = jsonObject.getString("content");
        ArrayList<Media> medias = getMedias(jsonObject.getJSONArray("media"));
        Link link;
        Meta meta;
        Place place;
        try {
            link = getLink(jsonObject.getJSONObject("links"));
        } catch (JSONException ex) {
            link = null;
        }
        try {
            meta = getMeta(jsonObject.getJSONObject("meta"));
        } catch (JSONException ex) {
            meta = null;
        }
        try {
            place = getPlace(jsonObject.getJSONObject("place"));
        } catch (JSONException ex) {
            place = null;
        }

        ArrayList<Author> authors = getAuthors(jsonObject.getJSONArray("authors"));
        ArrayList<Photographer> photographers = getPhotographers(jsonObject.getJSONArray("photographers"));
        ArrayList<Tag> tags = getTags(jsonObject.getJSONArray("tags"));

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

    private Location getLocation(String location) {
        String[] ll = location.split(",");
        double longitude = Double.valueOf(ll[0]);
        double latitude = Double.valueOf(ll[1]);
        return new Location(longitude, latitude);
    }

    private City getCity(JSONObject jsonObject) throws JSONException {
        City city = new City();

        String id = jsonObject.getString("id");
        String name = jsonObject.getString("name");
        String image = jsonObject.getString("image");

        city.setId(id);
        city.setName(name);
        city.setImage(image);

        return city;
    }

    private ArrayList<Tag> getTags(JSONArray jsonArray) throws JSONException {
        int size = jsonArray.length();
        ArrayList<Tag> tags = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            tags.add(getTag(jsonArray.getJSONObject(i)));
        }
        return tags;
    }

    private Tag getTag(JSONObject jsonObject) throws JSONException {
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

    private ArrayList<Photographer> getPhotographers(JSONArray jsonArray) throws JSONException {
        int size = jsonArray.length();
        ArrayList<Photographer> photographers = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            photographers.add(getPhotographer(jsonArray.getJSONObject(i)));
        }
        return photographers;
    }

    private Photographer getPhotographer(JSONObject jsonObject) throws JSONException {
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

    private ArrayList<Author> getAuthors(JSONArray jsonArray) throws JSONException {
        int size = jsonArray.length();
        ArrayList<Author> authors = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            authors.add(getAuthor(jsonArray.getJSONObject(i)));
        }
        return authors;
    }

    private Author getAuthor(JSONObject jsonObject) throws JSONException {
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

    private Meta getMeta(JSONObject jsonObject) throws JSONException {
        Meta meta = new Meta();
        return meta;
    }

    private ArrayList<Media> getMedias(JSONArray jsonArray) throws JSONException {
        int size = jsonArray.length();
        ArrayList<Media> medias = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            medias.add(getMedia(jsonArray.getJSONObject(i)));
        }
        return medias;
    }

    private Media getMedia(JSONObject jsonObject) throws JSONException {
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

    public void onBackClick() {
        onBackPressed();
    }

    private void addOnCategoryItemsRecyclerViewItemClickListener() {
        mCategoryItemsRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                CategoryItem categoryItem = mCategoryItems.get(position);
                switch (categoryItem.getObjectType()) {
                    case "event":
                        Intent eventIntent = new Intent(mContext, EventActivity.class);
                        eventIntent.putExtra("EVENT", categoryItem.getEvent());
                        startActivity(eventIntent);
                        break;
                    case "place":
                        Intent placeIntent = new Intent(mContext, PlaceActivity.class);
                        placeIntent.putExtra("PLACE", categoryItem.getPlace());
                        startActivity(placeIntent);
                        break;
                }
            }
        }));
    }

    private void addOnCategoryItemsRecyclerViewScrollListener() {
        mCategoryItemsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (mCategoryItemsLoading)
                    return;
                if (mCategoryItemsNextPage.equals("finished"))
                    return;

                int visibleItemCount = mCategoryItemsLinearLayoutManager.getChildCount();
                int totalItemCount = mCategoryItemsLinearLayoutManager.getItemCount();
                int pastVisiblesItems = mCategoryItemsLinearLayoutManager.findFirstVisibleItemPosition();

                if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                    getCategoryItems(++mCategoryItemsPage);
                    mCategoryItemsLoading = true;
                }
            }
        });
    }

    private void addOnCategoryBottomItemsRecyclerViewItemClickListener() {
        mCategoryItemsBottomRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                CategoryItem categoryItem = mBottomCategoryItems.get(position);
                switch (categoryItem.getObjectType()) {
                    case "event":
                        Intent eventIntent = new Intent(mContext, EventActivity.class);
                        eventIntent.putExtra("EVENT", categoryItem.getEvent());
                        startActivity(eventIntent);
                        break;
                    case "place":
                        Intent placeIntent = new Intent(mContext, PlaceActivity.class);
                        placeIntent.putExtra("PLACE", categoryItem.getPlace());
                        startActivity(placeIntent);
                        break;
                }
            }
        }));
    }

    private void initializeMap() {
        if (mCategoryItems.size() == 0)
            return;
        mCategoryItemsMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMarkers.clear();
                for (CategoryItem categoryItem : mBottomCategoryItems) {
                    LatLng coordinates;
                    MarkerOptions markerOptions;
                    switch (categoryItem.getObjectType()) {
                        case "event":
                            coordinates = new LatLng(categoryItem.getEvent().getPlace().getLocation().getLatitude(), categoryItem.getEvent().getPlace().getLocation().getLongitude());
                            markerOptions = new MarkerOptions().position(coordinates).title(categoryItem.getEvent().getPlace().getName());
                            mMarkers.add(googleMap.addMarker(markerOptions));
                            break;
                        case "place":
                            coordinates = new LatLng(categoryItem.getPlace().getLocation().getLatitude(), categoryItem.getPlace().getLocation().getLongitude());
                            markerOptions = new MarkerOptions().position(coordinates).title(categoryItem.getPlace().getName());
                            mMarkers.add(googleMap.addMarker(markerOptions));
                            break;
                    }
                }
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (Marker marker : mMarkers) {
                    builder.include(marker.getPosition());
                }
                if (mMarkers.size() == 0 || mMarkers == null) {
                    mCategoryItemsMapView.setVisibility(View.GONE);
                    mCategoryItemsBottomRecyclerView.setVisibility(View.GONE);
                    mCategoryItemsRecyclerView.setVisibility(View.VISIBLE);
                    mLom.setVisibility(View.GONE);
                    return;
                }
                LatLngBounds bounds = builder.build();
                int padding = 100; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                googleMap.animateCamera(cu);
                mCategoryItemsMapView.onResume();
            }
        });
    }

    private void initializeBottomCategoryItems() {
        mBottomCategoryItems.clear();
        for (CategoryItem categoryItem : mCategoryItems) {
            switch (categoryItem.getObjectType()) {
                case "event":
                    if (categoryItem.getEvent().getPlace() != null) {
                        if (categoryItem.getEvent().getPlace().getLocation() != null) {
                            mBottomCategoryItems.add(categoryItem);
                        }
                    }
                    break;
                case "place":
                    if (categoryItem.getPlace() != null) {
                        if (categoryItem.getPlace().getLocation() != null) {
                            mBottomCategoryItems.add(categoryItem);
                        }
                    }
                    break;
            }
        }
        mBottomAdapter.notifyDataSetChanged();
    }

    private void onLomClick() {
        if (mCategoryItemsRecyclerView.getVisibility() == View.VISIBLE) {
            mCategoryItemsRecyclerView.setVisibility(View.GONE);
            mCategoryItemsMapView.setVisibility(View.VISIBLE);
            mCategoryItemsBottomRecyclerView.setVisibility(View.VISIBLE);
            mLom.setImageResource(R.drawable.ic_list_white_24dp);
            initializeMap();
        } else {
            mCategoryItemsRecyclerView.setVisibility(View.VISIBLE);
            mCategoryItemsMapView.setVisibility(View.GONE);
            mCategoryItemsBottomRecyclerView.setVisibility(View.GONE);
            mLom.setImageResource(R.drawable.ic_map_white_24dp);
        }
    }

    private void setSnapHelper() {
        LinearSnapHelper snapHelper = new LinearSnapHelper() {
            @Override
            public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
                View centerView = findSnapView(layoutManager);
                if (centerView == null)
                    return RecyclerView.NO_POSITION;

                int position = layoutManager.getPosition(centerView);
                int targetPosition = -1;
                if (layoutManager.canScrollHorizontally()) {
                    if (velocityX < 0) {
                        targetPosition = position - 1;
                    } else {
                        targetPosition = position + 1;
                    }
                }

                if (layoutManager.canScrollVertically()) {
                    if (velocityY < 0) {
                        targetPosition = position - 1;
                    } else {
                        targetPosition = position + 1;
                    }
                }

                final int firstItem = 0;
                final int lastItem = layoutManager.getItemCount() - 1;
                targetPosition = Math.min(lastItem, Math.max(targetPosition, firstItem));
//                for (int i = 0; i < mMarkers.size(); i++) {
//                    if (targetPosition == i)
//                        mMarkers.get(i).showInfoWindow();
//                }
                for (int i = 0; i < mMarkers.size(); i++) {
                    if (targetPosition == i) {
                        final Marker marker = mMarkers.get(i);
                        marker.showInfoWindow();
                        mCategoryItemsMapView.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap googleMap) {
                                googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                                mCategoryItemsMapView.onResume();
                            }
                        });
                    }
                }
                return targetPosition;
            }
        };
        snapHelper.attachToRecyclerView(mCategoryItemsBottomRecyclerView);
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null)
            mRequestQueue = Volley.newRequestQueue(mContext);
        return mRequestQueue;
    }

    private void addToRequestQueue(Request request, String tag) {
        request.addMarker(tag);
        getRequestQueue().add(request);
    }

    private void cancelAllRequests(String tag) {
        getRequestQueue().cancelAll(tag);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mCategoryItems.size() > 0) {
            outState.putParcelableArrayList("PLACES", mCategoryItems);
            outState.putInt("POSITION", mCategoryItemsLinearLayoutManager.findFirstVisibleItemPosition());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCategoryItemsMapView.onStop();
        cancelAllRequests(TAG);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCategoryItemsMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCategoryItemsMapView.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mCategoryItemsMapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCategoryItemsMapView.onResume();
    }
}
