package collection.collectionBookmarks;

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
 * Created by User on 3/21/2018.
 */

public class BookmarksAdapter extends RecyclerView.Adapter<BookmarksAdapter.BookmarkViewHolder> {

    private Context mContext;
    private ArrayList<Bookmark> mBookmarks;

    public BookmarksAdapter(Context mContext, ArrayList<Bookmark> mBookmarks) {
        this.mContext = mContext;
        this.mBookmarks = mBookmarks;
    }

    public class BookmarkViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView title;
        TextView tag;
        public BookmarkViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.collection_bookmark_item_image);
            title = itemView.findViewById(R.id.collection_bookmark_item_title);
            tag = itemView.findViewById(R.id.collection_bookmark_item_tag);

            title.setTypeface(Fonts.get_iran_sans_bold_font(mContext));
            tag.setTypeface(Fonts.get_iran_sans_font(mContext));
        }
    }

    @Override
    public BookmarkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BookmarkViewHolder(LayoutInflater.from(mContext).inflate(R.layout.collection_bookmarks_item_layout , parent , false));
    }

    @Override
    public void onBindViewHolder(BookmarkViewHolder holder, int position) {
        Bookmark bookmark = mBookmarks.get(position);
        setCover(bookmark,holder);
        setTitle(bookmark,holder);
        setTag(bookmark,holder);
    }

    private void setCover(Bookmark bookmark , BookmarkViewHolder holder){
        switch (bookmark.getBookamarkable_type()){
            case "event":
                Glide.with(mContext)
                        .load( "http://" + bookmark.getEvent().getMedias().get(0).getPath() )
                        .thumbnail(
                                Glide.with(mContext)
                                .load( "http://" + bookmark.getEvent().getMedias().get(0).getPreview_path() )
                                .centerCrop()
                        )
                        .centerCrop()
                        .into(holder.image);
                break;
            case "place":
                Glide.with(mContext)
                        .load( "http://" + bookmark.getPlace().getMedias().get(0).getPath() )
                        .thumbnail(
                                Glide.with(mContext)
                                .load( "http://" + bookmark.getPlace().getMedias().get(0).getPreview_path() )
                                .centerCrop()
                        )
                        .centerCrop()
                        .into(holder.image);
                break;
        }
    }
    private void setTitle(Bookmark bookmark , BookmarkViewHolder holder){
        switch (bookmark.getBookamarkable_type()){
            case "event":
                holder.title.setText( bookmark.getEvent().getTitle() );
                break;
            case "place":
                holder.title.setText( bookmark.getPlace().getName() );
                break;
        }
    }
    private void setTag(Bookmark bookmark , BookmarkViewHolder holder){
        switch (bookmark.getBookamarkable_type()){
            case "event":
                setEventTag(bookmark.getEvent(),holder);
                break;
            case "place":
                setPlaceTag(bookmark.getPlace(),holder);
                break;
        }
    }
    private void setEventTag(Event event, BookmarkViewHolder holder){
        if (event.getTags() == null || event.getTags().size() == 0)
            holder.tag.setVisibility(View.GONE);
        else
            holder.tag.setText( event.getTags().get(0).getName() );
    }
    private void setPlaceTag(Place place , BookmarkViewHolder holder){
        if (place.getTags() == null || place.getTags().size() == 0)
            holder.tag.setVisibility(View.GONE);
        else
            holder.tag.setText( place.getTags().get(0).getName() );
    }

    @Override
    public int getItemCount() {
        return mBookmarks.size();
    }
}
