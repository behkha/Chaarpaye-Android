package place;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;

import posts.Media;

/**
 * Created by User on 3/19/2018.
 */

public class PlaceViewPagerAdapter extends PagerAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private ArrayList<Media> mMedias;

    public PlaceViewPagerAdapter(Context mContext, ArrayList<Media> medias) {
        this.mContext = mContext;
        this.mMedias = medias;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

//        View view = inflater.inflate(R.layout.place_view_pager_item_layout,null);
//        ImageView image = view.findViewById(R.id.place_view_pager_item_image);
//        Glide.with(mContext).load( "http://" +  mMedias.get(position).getPath() ).placeholder(R.drawable.temp).centerCrop().into( image );
//        ViewPager vp = (ViewPager) container;
//        vp.addView(view,0);
//        return view;


        PhotoView slide_image = new PhotoView(container.getContext());
        Glide.with(mContext)
                .load( "http://" +  mMedias.get(position).getPath() )
                .thumbnail(
                        Glide.with(mContext)
                        .load( "http://" + mMedias.get(position).getPreview_path())
                        .fitCenter()
                )
                .into( slide_image );
        slide_image.setScaleType(ImageView.ScaleType.FIT_XY);
        container.addView( slide_image , ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT );
        return slide_image;
    }

    @Override
    public int getCount() {
        return mMedias.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        ViewPager vp = (ViewPager) container;
//        View view = (View) object;
//        vp.removeView(view);
        container.removeView( (View) object );
    }
}
