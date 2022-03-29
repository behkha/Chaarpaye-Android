package event;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.behkha.chaarpaye.R;
import com.bikomobile.circleindicatorpager.CircleIndicatorPager;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wang.avi.AVLoadingIndicatorView;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import collection.addToCollection.AddToCollectionActivity;
import database.DatabaseController;
import place.PlaceActivity;
import utils.CustomViewPager;
import utils.Fonts;

/**
 * Created by User on 3/19/2018.
 */

public class EventActivity extends AppCompatActivity {

    private Context mContext;
    private RequestQueue mRequestQueue;
    private final String TAG = "COLLECTION_REQUEST_QUEUE_TAG";
    private Event mEvent;
    private ImageView mBookmark;

    private TextView mTag;
    private Button mLink;
    private MapView mMap;
    private TextView mAuthors;
    private TextView mPhotographers;
    private Button mEventPlace;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_activity_layout);
        mContext = getApplication();
        AVLoadingIndicatorView mProgressBar = (AVLoadingIndicatorView) findViewById(R.id.event_progress_bar);
        mProgressBar.setVisibility(View.VISIBLE);
        ScrollView mScrollView = (ScrollView) findViewById(R.id.event_scroll_view);
        mScrollView.setVisibility(View.GONE);

        ImageView mBack = (ImageView) findViewById(R.id.event_back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mBookmark = (ImageView) findViewById(R.id.event_bookmark);
        mBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBookmarkClick();
            }
        });

        mEvent = getIntent().getParcelableExtra("EVENT");
        sendFabricEvent(mEvent);

        Typeface font = Fonts.get_iran_sans_font(mContext);

        EventViewPagerAdapter adapter = new EventViewPagerAdapter(mContext, mEvent.getMedias());
        CustomViewPager mViewPager = (CustomViewPager) findViewById(R.id.event_view_pager);
        mViewPager.setAdapter(adapter);
        CircleIndicatorPager indicatorPager = (CircleIndicatorPager) findViewById(R.id.event_view_pager_indicator);
        indicatorPager.setViewPager(mViewPager);


        TextView mTitle = (TextView) findViewById(R.id.event_title);
        mTag = (TextView) findViewById(R.id.event_tag);
        HtmlTextView mContent = (HtmlTextView) findViewById(R.id.event_content);
        TextView mShareText = (TextView) findViewById(R.id.event_share_text);

        ImageView mAuthorsIcon = (ImageView) findViewById(R.id.event_authors_icon);
        mAuthors = (TextView) findViewById(R.id.event_authors);

        ImageView mPhotographersIcon = (ImageView) findViewById(R.id.event_photographers_icon);
        mPhotographers = (TextView) findViewById(R.id.event_photographers);

        mEventPlace = (Button) findViewById(R.id.event_place);
        mLink = (Button) findViewById(R.id.event_link);

        mTitle.setText(mEvent.getTitle());
        mContent.setHtml(mEvent.getContent());

        mTitle.setTypeface(font);
        mTag.setTypeface(font);
        mContent.setTypeface(font);
        mShareText.setTypeface(font);
        mAuthors.setTypeface(font);
        mPhotographers.setTypeface(font);
        mEventPlace.setTypeface(font);
        mLink.setTypeface(font);

        mMap = (MapView) findViewById(R.id.event_map);
        mMap.onCreate(savedInstanceState);
        setLocation(mEvent);
        setTag(mEvent);
        setAuthors(mEvent,mAuthorsIcon);
        setPhotographers(mEvent,mPhotographersIcon);
        setEventPlace(mEvent);
        setLink(mEvent);
        setBookmark(mEvent);

        mProgressBar.setVisibility(View.GONE);
        mScrollView.setVisibility(View.VISIBLE);
    }

    public void onBackClick(View view) {
        onBackPressed();
    }

    private void setTag(Event event) {
        if (event.getTags() == null || event.getTags().size() == 0)
                mTag.setVisibility(View.GONE);
         else
            mTag.setText(event.getTags().get(0).getName());
    }
    private void setLocation(final Event event) {
        if (event.getPlace() == null || event.getPlace().getLocation() == null) {
            mMap.setVisibility(View.GONE);
            return;
        }
        mMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                LatLng coordinates = new LatLng( event.getPlace().getLocation().getLatitude() , event.getPlace().getLocation().getLongitude() );
                googleMap.addMarker( new MarkerOptions().position(coordinates) );
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15));
                mMap.onResume();
            }
        });
    }
    private void setAuthors(Event event,ImageView icon){
        if (event.getAuthors() == null || event.getAuthors().size() == 0){
            mAuthors.setVisibility(View.GONE);
            icon.setVisibility(View.GONE);
            return;
        }
        String text = "متن از چارپایه نشین: ";
        for (int i = 0; i < event.getAuthors().size(); i++) {
            if (i == 0)
                text = text.concat(event.getAuthors().get(i).getName());
            else
                text = text.concat( " , " + event.getAuthors().get(i).getName());
        }
        mAuthors.setText(text);
    }
    private void setPhotographers(Event event,ImageView icon){
        if (event.getPhotographers() == null || event.getPhotographers().size() == 0){
            mPhotographers.setVisibility(View.GONE);
            icon.setVisibility(View.GONE);
            return;
        }
        String text = "عکس از چارپایه نشین: ";
        for (int i = 0; i < event.getPhotographers().size(); i++) {
            if (i == 0)
                text = text.concat(event.getPhotographers().get(i).getName());
            else
                text = text.concat( " , " + event.getPhotographers().get(i).getName());
        }
        mPhotographers.setText(text);
    }
    private void setEventPlace(final Event event){
        if (event.getPlace() == null){
            mEventPlace.setVisibility(View.GONE);
            return;
        }
        String eventPlaceText =  " مکان: " + event.getPlace().getName();
        mEventPlace.setText(eventPlaceText);
        mEventPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PlaceActivity.class);
                intent.putExtra("PLACE",event.getPlace());
                startActivity(intent);
            }
        });
    }
    private void setLink(Event event){
        if (event.getLink() == null){
            mLink.setVisibility(View.GONE);
            return;
        }
        mLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData( Uri.parse(mEvent.getLink().getWebSite()) );
                startActivity(intent);
            }
        });
    }
    private void setBookmark(Event event){
        DatabaseController db = new DatabaseController(mContext);
        if (db.hasEvent(event.getId()))
            mBookmark.setImageResource( R.drawable.ic_small_tick );
        else
            mBookmark.setImageResource( R.drawable.ic_add_circle_outline_white_24dp );
        db.close();
    }

    public void onShareClick(){
        share();
    }
    public void onBookmarkClick(){
        Intent intent = new Intent(mContext,AddToCollectionActivity.class);
        intent.putExtra("EVENT",mEvent);
        startActivity(intent);
    }


    private void share(){
        Intent shareIntent = new Intent();
        String shareText =  "چارپایه" + "\n\n" +
                mEvent.getTitle() + "\n" +
                mEvent.getContent() + "\n" +
                "https://t.me/chaarpaye" + "\n" +
                "http://instagram.com/chaarpaye";
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT , shareText);
        shareIntent.setType("text/plain");
        startActivity( Intent.createChooser( shareIntent , "ارسال به ..." ) );
    }

    private void sendFabricEvent(Event event) {
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentId(event.getId())
                .putContentName(event.getTitle())
                .putContentType("Event")
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMap.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMap.onStart();
        if (mEvent != null)
            setBookmark(mEvent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMap.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMap.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMap.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMap.onSaveInstanceState(outState);
    }
}
