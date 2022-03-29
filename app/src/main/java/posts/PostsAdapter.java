package posts;

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
 * Created by User on 3/11/2018.
 */

public class PostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int DATE_TYPE_ITEM = 0;
    private final int POST_TYPE_ITEM = 1;
    private Context mContext;
    private ArrayList<Post> mPosts;

    public PostsAdapter(Context context, ArrayList<Post> mPosts) {
        this.mContext = context;
        this.mPosts = mPosts;
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView title;
        TextView tag;

        public PostViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.post_item_image);
            title = itemView.findViewById(R.id.post_item_title);
            tag = itemView.findViewById(R.id.post_item_tag);
            title.setTypeface(Fonts.get_iran_sans_bold_font(mContext));
            tag.setTypeface(Fonts.get_iran_sans_font(mContext));
        }
    }

    public class DateViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        public DateViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date_item_text);
            date.setTypeface(Fonts.get_iran_sans_bold_font(mContext));
        }
    }

//    @Override
//    public int getItemViewType(int position) {
//        if (mPosts.get(position) instanceof Post)
//            return POST_TYPE_ITEM;
//        else
//            return DATE_TYPE_ITEM;
//    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        switch (viewType){
//            case POST_TYPE_ITEM:
                return new PostViewHolder(LayoutInflater.from(mContext).inflate(R.layout.post_item_layout, parent, false));
//            case DATE_TYPE_ITEM:
//                return new DateViewHolder(LayoutInflater.from(mContext).inflate(R.layout.date_item_layout,parent,false));
//            default:
//                return null;
//        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


//        switch (holder.getItemViewType()){
//
//            case POST_TYPE_ITEM:
                Post post = mPosts.get(position);
                PostViewHolder postHolder = (PostViewHolder) holder;
                setCover(post,postHolder);
                setTag(post,postHolder);
//                break;
//
//            case DATE_TYPE_ITEM:
//                String day = (String) mPosts.get(position);
//                DateViewHolder dateViewHolder = (DateViewHolder) holder;
//                dateViewHolder.date.setText(day);
//                break;
//
//        }
    }

    private boolean isGif(String url){
        return url.endsWith(".gif");
    }
    private void setCover(Post post , PostViewHolder viewHolder){
        switch (post.getPostable_type()){
            case "event":
                Glide.with(mContext)
                        .load( "http://" +  post.getEvent().getMedias().get(0).getPath() )
                        .thumbnail(
                                Glide.with(mContext)
                                .load( "http://" + post.getEvent().getMedias().get(0).getPreview_path() )
                                .centerCrop()
                        )
                        .centerCrop()
                        .into( viewHolder.image );
                viewHolder.title.setText( post.getEvent().getTitle() );
                break;
            case "place":
                Glide.with(mContext)
                        .load( "http://" + post.getPlace().getMedias().get(0).getPath() )
                        .thumbnail(
                                Glide.with(mContext)
                                .load( "http://" + post.getPlace().getMedias().get(0).getPreview_path() )
                                .centerCrop()
                        )
                        .centerCrop()
                        .into( viewHolder.image );
                viewHolder.title.setText( post.getPlace().getName() );
                break;
        }
    }
    private void setTag(Post post , PostViewHolder viewHolder){
        if (post.getTags() == null || post.getTags().size() == 0){
            viewHolder.tag.setVisibility(View.GONE);
            return;
        }
        viewHolder.tag.setText(post.getTags().get(0).getName());
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }
}
