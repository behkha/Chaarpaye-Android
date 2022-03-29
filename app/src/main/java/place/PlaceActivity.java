package place;

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
import android.widget.TextView;

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

import org.sufficientlysecure.htmltextview.HtmlTextView;

import collection.addToCollection.AddToCollectionActivity;
import database.DatabaseController;
import utils.CustomViewPager;
import utils.Fonts;

/**
 * Created by User on 3/19/2018.
 */

public class PlaceActivity extends AppCompatActivity {

    private Context mContext;
    private Place mPlace;

    private ImageView mBookmark;
    private TextView mTag;
    private MapView mMap;
    private Button mLink;
    private TextView mAuthors;
    private TextView mPhotographers;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_activity_layout);
        mContext = getApplicationContext();

        mPlace = getIntent().getParcelableExtra("PLACE");
        sendFabricEvent(mPlace);

        ImageView mBack = (ImageView) findViewById(R.id.place_back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Typeface font = Fonts.get_iran_sans_font(mContext);
        ImageView mShare = (ImageView) findViewById(R.id.place_share);
        TextView mShareText = (TextView) findViewById(R.id.place_share_text);
        mShareText.setTypeface(font);
        mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onShareClick();
            }
        });
        mShareText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onShareClick();
            }
        });


        PlaceViewPagerAdapter adapter = new PlaceViewPagerAdapter(mContext, mPlace.getMedias());
        CustomViewPager mViewPager = (CustomViewPager) findViewById(R.id.place_view_pager);
        mViewPager.setAdapter(adapter);
        CircleIndicatorPager indicatorPager = (CircleIndicatorPager) findViewById(R.id.place_view_pager_indicator);
        indicatorPager.setViewPager(mViewPager);

        TextView mName = (TextView) findViewById(R.id.place_name);
        mName.setText(mPlace.getName());
        mName.setTypeface(font);

        HtmlTextView mContent = (HtmlTextView) findViewById(R.id.place_content);
        mContent.setHtml(mPlace.getContent());
        mContent.setTypeface(font);

        mLink = (Button) findViewById(R.id.place_link);
        mLink.setTypeface(font);
        setLink(mPlace);

        mBookmark = (ImageView) findViewById(R.id.place_bookmark);
        mBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBookmarkClick();
            }
        });
        setBookmark(mPlace);


        mMap = (MapView) findViewById(R.id.place_map);
        mMap.onCreate(savedInstanceState);
        setLocation(mPlace);

        ImageView mAddressIcon = (ImageView) findViewById(R.id.place_address_icon);
        TextView mAddress = (TextView) findViewById(R.id.place_address);
        mAddress.setTypeface(font);
        setAddress(mPlace,mAddress,mAddressIcon);

        mTag = (TextView) findViewById(R.id.place_tag);
        setTag(mPlace);
        mTag.setTypeface(font);

        ImageView mAuthorsIcon = (ImageView) findViewById(R.id.place_authors_icon);
        mAuthors = (TextView) findViewById(R.id.place_authors);
        setAuthors(mPlace,mAuthorsIcon);
        mAuthors.setTypeface(font);

        ImageView mPhotographersIcon = (ImageView) findViewById(R.id.place_photograghers_icon);
        mPhotographers = (TextView) findViewById(R.id.place_photograghers);
        setPhotographers(mPlace,mPhotographersIcon);
        mPhotographers.setTypeface(font);
    }

    private void setPhotographers(Place place , ImageView icon){
        if (place.getPhotographers() == null || place.getPhotographers().size() == 0){
            mPhotographers.setVisibility(View.GONE);
            icon.setVisibility(View.GONE);
            return;
        }
        String text = "عکس از چارپایه نشین: ";
        for (int i = 0; i < place.getPhotographers().size(); i++) {
            if (i == 0)
                text = text.concat(place.getPhotographers().get(i).getName());
            else
                text = text.concat( " , " + place.getPhotographers().get(i).getName());
        }
        mPhotographers.setText(text);
    }

    private void setAuthors(Place place,ImageView icon) {
        if (place.getAuthors() == null || place.getAuthors().size() == 0) {
            mAuthors.setVisibility(View.GONE);
            icon.setVisibility(View.GONE);
            return;
        }
        String text = "متن از چارپایه نشین: ";
        for (int i = 0; i < place.getAuthors().size(); i++) {
            if (i == 0)
                text = text.concat(place.getAuthors().get(i).getName());
            else
                text = text.concat( " , " + place.getAuthors().get(i).getName());
        }
        mAuthors.setText(text);
    }

    private void setLocation(final Place place) {
        if (place.getLocation() == null) {
            mMap.setVisibility(View.GONE);
            return;
        }
        mMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                LatLng coordinates = new LatLng( place.getLocation().getLatitude() , place.getLocation().getLongitude() );
                googleMap.addMarker( new MarkerOptions().position(coordinates).title( place.getName() ) );
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15));
                mMap.onResume();
            }
        });
    }

    private void setAddress(Place place , TextView address , ImageView icon){
        if (place.getAddress() == null || place.getAddress().equals("null")){
            address.setVisibility(View.GONE);
            icon.setVisibility(View.GONE);
            return;
        }
        address.setText( place.getAddress() );
    }

    private void setTag(Place place) {
        if (place.getTags() == null || place.getTags().size() == 0)
            mTag.setVisibility(View.GONE);
        else
            mTag.setText(place.getTags().get(0).getName());
    }

    private void setBookmark(Place place){
        DatabaseController db = new DatabaseController(mContext);
        if (db.hasPlace(place.getId()))
            mBookmark.setImageResource(R.drawable.ic_small_tick);
        else
            mBookmark.setImageResource(R.drawable.ic_add_circle_outline_white_24dp);
        db.close();
    }

    private void setLink(final Place place){
        if (place.getLink() == null){
            mLink.setVisibility(View.GONE);
            return;
        }
        mLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData( Uri.parse(place.getLink().getWebSite()) );
                startActivity(intent);
            }
        });

//        Glide.with(mContext)
//                .load( "http://" + mPlace.getMedias().get(0).getPath())
//                .asBitmap()
//                .into(new SimpleTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                        Palette.from(resource)
//                                .generate(new Palette.PaletteAsyncListener() {
//                                    @Override
//                                    public void onGenerated(Palette palette) {
//                                        Palette.Swatch textSwatch = palette.getVibrantSwatch();
//                                        if (textSwatch == null) {
//                                            return;
//                                        }
//                                        mLink.setBackgroundColor(textSwatch.getRgb());
//                                        mLink.setTextColor(textSwatch.getTitleTextColor());
//                                    }
//                                });
//                    }
//                });


    }

    private void sendFabricEvent(Place place){
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentId(place.getId())
                .putContentName(place.getName())
                .putContentType("Place")
        );
    }

    public void onBackClick(View view) {
        onBackPressed();
    }

    public void onShareClick(){
        share();
    }

    public void onBookmarkClick(){
        Intent intent = new Intent(mContext,AddToCollectionActivity.class);
        intent.putExtra("PLACE",mPlace);
        startActivity(intent);
    }

    private void share(){
        Intent shareIntent = new Intent();
        String shareText =  "چارپایه" + "\n\n" +
                mPlace.getName() + "\n" +
                mPlace.getContent() + "\n" +
                "https://t.me/chaarpaye" + "\n" +
                "http://instagram.com/chaarpaye";
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT , shareText);
        shareIntent.setType("text/plain");
        startActivity( Intent.createChooser( shareIntent , "ارسال به ..." ) );
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMap.onStop();
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
    protected void onStart() {
        super.onStart();
        mMap.onStart();
        if (mPlace != null)
            setBookmark(mPlace);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMap.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMap.onSaveInstanceState(outState);
    }
}
