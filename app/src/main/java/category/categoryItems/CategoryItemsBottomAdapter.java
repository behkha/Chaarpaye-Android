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

import utils.Fonts;

/**
 * Created by User on 3/31/2018.
 */

public class CategoryItemsBottomAdapter extends RecyclerView.Adapter<CategoryItemsBottomAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<CategoryItem> mCategoryItems;

    public CategoryItemsBottomAdapter(Context mContext, ArrayList<CategoryItem> mCategoryItems) {
        this.mContext = mContext;
        this.mCategoryItems = mCategoryItems;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTitle;
        ImageView mImage;
        public ViewHolder(View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.category_items_bottom_item_title);
            mImage = itemView.findViewById(R.id.category_items_bottom_item_image);
            mTitle.setTypeface(Fonts.get_iran_sans_font(mContext));
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(mContext).inflate(R.layout.category_items_bottom_item_layout , parent , false)
        );
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CategoryItem categoryItem = mCategoryItems.get(position);
        switch (categoryItem.getObjectType()){
            case "event":
                holder.mTitle.setText(categoryItem.getEvent().getTitle());
                Glide.with(mContext).load( "http://" + categoryItem.getEvent().getMedias().get(0).getPath() ).centerCrop().into( holder.mImage );
                break;
            case "place":
                holder.mTitle.setText( categoryItem.getPlace().getName() );
                Glide.with(mContext).load( "http://" + categoryItem.getPlace().getMedias().get(0).getPath() ).centerCrop().into( holder.mImage );
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mCategoryItems.size();
    }
}
