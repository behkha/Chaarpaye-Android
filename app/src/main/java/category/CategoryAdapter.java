package category;

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
 * Created by User on 3/15/2018.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context mContext;
    private ArrayList<Category> mCateogries;


    public CategoryAdapter(Context mContext, ArrayList<Category> mCateogries) {
        this.mContext = mContext;
        this.mCateogries = mCateogries;
    }


    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView title;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.category_item_image);
            title = itemView.findViewById(R.id.category_item_title);
            title.setTypeface(Fonts.get_iran_sans_font(mContext));
        }
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(mContext).inflate(R.layout.category_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        Category category = mCateogries.get(position);
        if ( isGif( category.getImage() ) )
            Glide.with(mContext).load( "http://" + category.getImage()).asGif().placeholder(R.color.white).into(holder.image);
        else
            Glide.with(mContext).load( "http://" + category.getImage()).placeholder(R.color.white).into(holder.image);
        holder.title.setText(category.getName());
    }

    private boolean isGif(String url){
        return url.endsWith(".gif");
    }

    @Override
    public int getItemCount() {
        return mCateogries.size();
    }
}
