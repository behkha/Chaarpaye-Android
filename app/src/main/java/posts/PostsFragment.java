package posts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

import event.Event;
import event.EventActivity;
import place.Place;
import place.PlaceActivity;
import utils.Fonts;
import utils.RecyclerItemClickListener;
import utils.Server;

/**
 * Created by User on 3/6/2018.
 */

public class PostsFragment extends Fragment  {

    private Context mContext;
    private RequestQueue mRequestQueue;
    private final String TAG = "POSTS_REQUEST_QUEUE_TAG";
    private AVLoadingIndicatorView mProgressBar;
    private DrawerLayout mDrawerLayout;
    private ConstraintLayout mContentLayout;
    private ArrayList<TextView> mWeekDaysButtons = new ArrayList<>();
    private RecyclerView mPostsRecyclerView;
    private PostsAdapter mPostsAdapter;
    private LinearLayoutManager mPostsLinearLayoutManager;
//    private ArrayList<Post> mPostsAndDates = new ArrayList<>();
    private ArrayList<Post> mPosts = new ArrayList<>();
    private boolean mPostsLoadingFlag = false;
    private int mPostsPage = 1;
    private String mPostsNextPageUrl = "null";
//    private ArrayList<String> mDates = new ArrayList<>();
//    private ArrayList<Integer> mIndexOfDates = new ArrayList<>();

    TextView mTryAgain;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.posts_fragment_layout, container, false);
        this.mContext = view.getContext();

        mTryAgain = view.findViewById(R.id.posts_fragment_try_again);
        mTryAgain.setTypeface(Fonts.get_iran_sans_font(mContext));
        mTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTryAgainClick();
            }
        });

        mDrawerLayout = view.findViewById(R.id.news_drawer_layout);
        mContentLayout = view.findViewById(R.id.news_content_layout);
        mProgressBar = view.findViewById(R.id.posts_fragment_progress_bar);
//        addDrawerAnimationListener();
//
//        TextView button1 = view.findViewById(R.id.day_1);
//        TextView button2 = view.findViewById(R.id.day_2);
//        TextView button3 = view.findViewById(R.id.day_3);
//        TextView button4 = view.findViewById(R.id.day_4);
//        TextView button5 = view.findViewById(R.id.day_5);
//        TextView button6 = view.findViewById(R.id.day_6);
//        TextView button7 = view.findViewById(R.id.day_7);
//
//        addButtonsToArrayList(button1, button2, button3, button4, button5, button6, button7);
//        changeWeeksFont();


        mPostsRecyclerView = view.findViewById(R.id.posts_fragment_recycler_view);
        mPostsAdapter = new PostsAdapter(mContext, mPosts);
        mPostsLinearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mPostsRecyclerView.setAdapter(mPostsAdapter);
        mPostsRecyclerView.setLayoutManager(mPostsLinearLayoutManager);
        mPostsRecyclerView.setHasFixedSize(true);
        mPostsLinearLayoutManager.setAutoMeasureEnabled(true);
        addOnPostRecyclerViewItemClickListener();
        addOnPostRecyclerViewScrollListener();

//        onViewPagerStateChangeListener();


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState == null || !savedInstanceState.containsKey("POSTS"))
            initializePosts();
        else
            restorePosts(savedInstanceState);
//        else
//            restorePosts(savedInstanceState);
    }

    private void initializePosts(){
        mTryAgain.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mPostsRecyclerView.setVisibility(View.GONE);
        getPosts(mPostsPage);
    }
    private void restorePosts(Bundle savedInstanceState){
        mProgressBar.setVisibility(View.VISIBLE);
        mPostsRecyclerView.setVisibility(View.GONE);
//        mIndexOfDates = savedInstanceState.getIntegerArrayList("INDEX_OF_DATES");
        mPosts = savedInstanceState.getParcelableArrayList("POSTS");
//        mDates = savedInstanceState.getStringArrayList("DATES");
//        for(Post post : mPosts)
//            mPostsAndDates.add(post);
//        int i = 0;
//        for (String date : mDates){
//            mPostsAndDates.add( mIndexOfDates.get( mDates.indexOf(date) ) , date );
//            setDayButtonText(date,i);
//            i++;
//        }
        int position = savedInstanceState.getInt("POSITION");
        mPostsAdapter = new PostsAdapter(mContext, mPosts);
        mPostsRecyclerView.setAdapter(mPostsAdapter);
        mPostsRecyclerView.scrollToPosition(position);
        mPostsNextPageUrl = savedInstanceState.getString("NEXT_PAGE_URL");
        mPostsPage = savedInstanceState.getInt("PAGE");
//        changeWeekColor( 0 );
//        addOnWeeksClickListener();
//        addOnViewPagerPageChanged();
//        addOnPostRecyclerViewItemClickListener();
        mProgressBar.setVisibility(View.GONE);
        mPostsRecyclerView.setVisibility(View.VISIBLE);
    }

    private void getPosts(int page){
        String url = Server.DOMAIN  + "/v2/posts?page=" + page;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray data = response.getJSONArray("data");
                            addPosts(data);
//                            addDatesIndex();
//                            changeWeekColor(0);
//                            addOnWeeksClickListener();
//                            addOnViewPagerPageChanged();
//                            mPostsAdapter.notifyDataSetChanged();
//                            addOnPostRecyclerViewItemClickListener();
//                            addOnPostRecyclerViewScrollListener();
                            mPostsNextPageUrl = response.getString("next_page_url");
                            mPostsLoadingFlag = false;
                            mProgressBar.setVisibility(View.GONE);
                            mPostsRecyclerView.setVisibility(View.VISIBLE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            mProgressBar.setVisibility(View.GONE);
                            mPostsRecyclerView.setVisibility(View.GONE);
                            mTryAgain.setVisibility(View.VISIBLE);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Server.errorHandler(mContext,error);
                        mProgressBar.setVisibility(View.GONE);
                        mPostsRecyclerView.setVisibility(View.GONE);
                        mDrawerLayout.setVisibility(View.GONE);
                        mTryAgain.setVisibility(View.VISIBLE);
                    }
                });
        addToRequestQueue(request,TAG);
    }
    private void addPosts(JSONArray jsonArray) throws JSONException{
//        Iterator<String> days = jsonObject.keys();
//        int i = 0;
//        while (days.hasNext()){
//            String day = days.next();
//            if (day.equals("weekDays"))
//                return;
//            mPostsAndDates.add( day );
//            mDates.add(day);
//            setDayButtonText( day , i);
//            ArrayList<Post> posts = getPosts( jsonObject.getJSONArray( day ) );
//            mPostsAndDates.addAll( posts );
//            mPosts.addAll( posts );
//            i++;
//        }
        int size = jsonArray.length();
        for (int i = 0; i < size; i++) {
            mPosts.add( getPost( jsonArray.getJSONObject(i) ) );
        }
        mPostsAdapter.notifyDataSetChanged();
    }
    private ArrayList<Post> getPosts(JSONArray jsonArray) throws JSONException{
        int size = jsonArray.length();
        ArrayList<Post> posts = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            posts.add( getPost( jsonArray.getJSONObject(i) ) );
        }
        return posts;
    }
    private Post getPost(JSONObject jsonObject) throws JSONException{
        Post post = new Post();

        String id = jsonObject.getString("id");
        String postable_id = jsonObject.getString("postable_id");
        String postable_type = jsonObject.getString("postable_type");
        JSONObject postJson = jsonObject.getJSONObject("post");
        Event event = null;
        Place place = null;
        switch (postable_type){
            case "event":
                event = getEvent( postJson );
                break;
            case "place":
                place = getPlace( postJson );
                break;
        }
        ArrayList<Tag> tags = getTags( jsonObject.getJSONArray("tags") );

        post.setId(id);
        post.setPostable_id(postable_id);
        post.setPostable_type(postable_type);
        post.setEvent(event);
        post.setPlace(place);
        post.setTags(tags);

        return post;
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
        ArrayList<Author> authors = getAuthors( jsonObject.getJSONArray("authors") );
        ArrayList<Photographer> photographers = getPhotographers( jsonObject.getJSONArray("photographers") );
        ArrayList<Tag> tags = getTags( jsonObject.getJSONArray("tags") );

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



//    private String getPersianWeekName(String date){
//        switch (date){
//            case "شنبه":
//                return date;
//            case "یکشنبه":
//                return "1شنبه";
//            case "دوشنبه":
//                return "2شنبه";
//            case "سه شنبه":
//                return "3شنبه";
//            case "چهارشنبه":
//                return "4شنبه";
//            case "پنجشنبه":
//                return "5شنبه";
//            case "جمعه":
//                return date;
//        }
//        return null;
//    }
//    private void addDatesIndex(){
//        int i = -1;
//        for (Object obj : mPostsAndDates) {
//            i++;
//            if (obj instanceof String)
//                mIndexOfDates.add(i);
//        }
//    }
//    private void addButtonsToArrayList(TextView... buttons) {
//        Collections.addAll(mWeekDaysButtons, buttons);
//    }
//    private void setDayButtonText(String text , int i){
//        mWeekDaysButtons.get(i).setText(text);
//    }
//    private void changeWeekColor(int pos) {
//        for (int i = 0; i < mWeekDaysButtons.size(); i++) {
//            if (i == pos) {
//                mWeekDaysButtons.get(i).setBackground(ContextCompat.getDrawable(mContext, R.drawable.week_days_selected_background));
//                mWeekDaysButtons.get(i).setTextColor(ContextCompat.getColor(mContext, R.color.white));
//            } else {
//                mWeekDaysButtons.get(i).setBackground(ContextCompat.getDrawable(mContext, R.drawable.week_days_unselected_background));
//                mWeekDaysButtons.get(i).setTextColor(ContextCompat.getColor(mContext, R.color.grey));
//            }
//        }
//    }
//    private void changeWeeksFont() {
//        for (TextView button : mWeekDaysButtons)
//            button.setTypeface(Fonts.get_iran_sans_bold_font(mContext));
//    }
//    private void addOnWeeksClickListener() {
//        for (final TextView button : mWeekDaysButtons)
//            button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    int buttonIndex = mWeekDaysButtons.indexOf(button);
//                    changeWeekColor( buttonIndex );
//                    mDrawerLayout.closeDrawer(GravityCompat.START);
//                    mPostsRecyclerView.smoothScrollToPosition( mIndexOfDates.get( buttonIndex ));
//                }
//            });
//    }
//    private void addOnViewPagerPageChanged() {
//        ViewPager viewPager = getActivity().findViewById(R.id.view_pager);
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
//                    mDrawerLayout.closeDrawer(GravityCompat.START);
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
//    }
//    private void addDrawerAnimationListener() {
//        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, R.string.open, R.string.close) {
//            @Override
//            public void onDrawerSlide(View drawerView, float slideOffset) {
//                super.onDrawerSlide(drawerView, slideOffset);
//                float slideX = drawerView.getWidth() * slideOffset;
//                mContentLayout.setTranslationX(slideX);
//            }
//        };
//        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);
//    }
//    private void onViewPagerStateChangeListener() {
//        final ViewPager viewPager = getActivity().findViewById(R.id.view_pager);
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//                if (viewPager.getCurrentItem() != 1 && viewPager.getCurrentItem() != 2)
//                    if (state == 1)
//                        if (!mDrawerLayout.isDrawerOpen(GravityCompat.START))
//                            mDrawerLayout.openDrawer(GravityCompat.START);
//            }
//        });
//    }
    private void addOnPostRecyclerViewItemClickListener(){
        mPostsRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                if (mPostsAndDates.get(position) instanceof String)
//                    return;
                Post post = mPosts.get(position);
                switch (post.getPostable_type()){
                    case "event":
                        Intent eventIntent = new Intent(mContext , EventActivity.class);
                        eventIntent.putExtra("EVENT",post.getEvent());
                        startActivity(eventIntent);
                        break;
                    case "place":
                        Intent placeIntent = new Intent(mContext , PlaceActivity.class);
                        placeIntent.putExtra("PLACE",post.getPlace());
                        startActivity(placeIntent);
                        break;
                }
            }
        }));
    }
    private void addOnPostRecyclerViewScrollListener(){
        mPostsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (mPostsLoadingFlag)
                    return;

                if (mPostsNextPageUrl.equals("null"))
                    return;

                int visibleItemCount = mPostsLinearLayoutManager.getChildCount();
                int totalItemCount = mPostsLinearLayoutManager.getItemCount();
                int pastVisibleItems = mPostsLinearLayoutManager.findFirstVisibleItemPosition();
                if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                    //End of list
                    getPosts(++mPostsPage);
                    mPostsLoadingFlag = true;
                }
            }
        });
    }

    public void onTryAgainClick(){
        initializePosts();
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (mPosts.size() > 0) {
//            outState.putIntegerArrayList("INDEX_OF_DATES",mIndexOfDates);
            outState.putParcelableArrayList("POSTS",mPosts);
//            outState.putStringArrayList("DATES",mDates);
            outState.putInt("POSITION",mPostsLinearLayoutManager.findFirstVisibleItemPosition());
            outState.putString("NEXT_PAGE_URL",mPostsNextPageUrl);
            outState.putInt("PAGE",mPostsPage);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
        cancelAllRequests(TAG);
    }

}
