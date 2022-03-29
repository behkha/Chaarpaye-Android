package category.categoryItems;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.behkha.chaarpaye.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import event.Event;
import place.Place;
import utils.Fonts;

/**
 * Created by User on 3/22/2018.
 */

public class CategoryItemsAdapter extends RecyclerView.Adapter<CategoryItemsAdapter.CategoryItemsViewHolder> {

    private Context mContext;
    private ArrayList<CategoryItem> mCategoryItems;

    public CategoryItemsAdapter(Context mContext, ArrayList<CategoryItem> mPlaces) {
        this.mContext = mContext;
        this.mCategoryItems = mPlaces;
    }

    protected class CategoryItemsViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView image;
        TextView tag;
        public CategoryItemsViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.category_items_item_title);
            image = itemView.findViewById(R.id.category_items_item_image);
            tag = itemView.findViewById(R.id.category_items_item_tag);

            title.setTypeface(Fonts.get_iran_sans_bold_font(mContext));
            tag.setTypeface(Fonts.get_iran_sans_font(mContext));
        }
    }

    @Override
    public CategoryItemsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CategoryItemsViewHolder(
                LayoutInflater.from(mContext).inflate(R.layout.category_items_item_layout , parent , false));
    }

    @Override
    public void onBindViewHolder(CategoryItemsViewHolder holder, int position) {
        CategoryItem categoryItem = mCategoryItems.get(position);
        String objecType = categoryItem.getObjectType();
        switch (objecType){
            case "place":
                setCover(categoryItem.getPlace(),holder);
                setTitle(categoryItem.getPlace(),holder);
                setTag(categoryItem.getPlace(),holder);
                break;
            case "event":
                setCover(categoryItem.getEvent(),holder);
                setTitle(categoryItem.getEvent(),holder);
                setTag(categoryItem.getEvent(),holder);
                break;
        }

    }

    private boolean isGif(String url){
        return url.endsWith(".gif");
    }
    private void setCover(Place place , CategoryItemsViewHolder holder){
        if (place.getMedias() == null || place.getMedias().size() == 0)
            return;
        String coverUrl = place.getMedias().get(0).getPath();
        if ( isGif(coverUrl) )
            Glide.with(mContext).load( "http://" + coverUrl ).asGif().placeholder(R.drawable.temp).centerCrop().into( holder.image );
        else
            Glide.with(mContext)
                    .load( "http://" + coverUrl )
                    .into( holder.image );
    }
    private void setTitle(Place place , CategoryItemsViewHolder holder){
        holder.title.setText( place.getName() );
    }
    private void setTag(Place place , CategoryItemsViewHolder holder){
        if (place.getTags() == null || place.getTags().size() == 0){
            holder.tag.setVisibility(View.GONE);
            return;
        }
        holder.tag.setText( place.getTags().get(0).getName() );
    }
    private void setCover(Event event , CategoryItemsViewHolder holder){
        if (event.getMedias() == null || event.getMedias().size() == 0)
            return;
        String coverUrl = event.getMedias().get(0).getPath();
        if ( isGif(coverUrl) )
            Glide.with(mContext).load( "http://" + coverUrl ).asGif().placeholder(R.drawable.temp).centerCrop().into( holder.image );
        else
            Glide.with(mContext)
                    .load( "http://" + coverUrl )
                    .into( holder.image );
    }
    private void setTitle(Event event , CategoryItemsViewHolder holder){
        holder.title.setText( event.getTitle() );
    }
    private void setTag(Event event , CategoryItemsViewHolder holder){
        if (event.getTags() == null || event.getTags().size() == 0){
            holder.tag.setVisibility(View.GONE);
            return;
        }
        holder.tag.setText( event.getTags().get(0).getName() );
    }

    @Override
    public int getItemCount() {
        return mCategoryItems.size();
    }
}
