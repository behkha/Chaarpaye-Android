package collection.addToCollection;

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

import collection.Collection;
import database.DatabaseController;
import event.Event;
import place.Place;
import utils.Fonts;

/**
 * Created by User on 3/23/2018.
 */

public class AddToCollectionAdapter extends RecyclerView.Adapter<AddToCollectionAdapter.AddToCollectionViewHolder> {

    private Context mContext;
    private ArrayList<Collection> mCollections;
    private Event mEvent;
    private Place mPlace;

    public AddToCollectionAdapter(Context mContext, ArrayList<Collection> mCollections , Event event , Place mPlace) {
        this.mContext = mContext;
        this.mCollections = mCollections;
        this.mEvent = event;
        this.mPlace = mPlace;
    }

    protected class AddToCollectionViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name;
        TextView bookmarkCount;
        ImageView bookmark;
        public AddToCollectionViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.add_to_collection_dialog_recycler_view_item_image);
            name = itemView.findViewById(R.id.add_to_collection_dialog_recycler_view_item_name);
            bookmarkCount = itemView.findViewById(R.id.add_to_collection_dialog_recycler_view_item_bookmark_count);
            bookmark = itemView.findViewById(R.id.add_to_collection_dialog_recycler_view_item_bookmark_icon);

            name.setTypeface(Fonts.get_iran_sans_font(mContext));
            bookmarkCount.setTypeface(Fonts.get_iran_sans_fa_num_font(mContext));
        }
    }

    @Override
    public AddToCollectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AddToCollectionViewHolder(
                LayoutInflater.from(mContext).inflate(R.layout.add_to_collection_activity_recycler_view_item_layout, parent , false));
    }

    @Override
    public void onBindViewHolder(AddToCollectionViewHolder holder, int position) {
        Collection collection = mCollections.get(position);
        holder.name.setText( collection.getName() );
        setCover(collection,holder);
        setBookmarkCount(collection,holder);
        setBookmark(collection,holder);
        onBookmarkClick(collection,holder);
    }

    private void setCover(Collection collection , AddToCollectionViewHolder holder) {
        if (collection.getImage() == null || collection.getImage().equals("null"))
            return;
        Glide.with(mContext).load( "http://" + collection.getImage() ).into( holder.image );
    }
    private void setBookmarkCount(Collection collection , AddToCollectionViewHolder holder){
        String text = collection.getBookmarked_count() + " مورد ";
        holder.bookmarkCount.setText(text);
    }
    private void setBookmark(Collection collection , AddToCollectionViewHolder holder){
        DatabaseController db = new DatabaseController(mContext);
        if (mEvent != null){
            if (db.hasEvent(mEvent.getId() , collection.getId())) {
                holder.bookmark.setImageResource(R.drawable.ic_small_tick);
                collection.setMarked(true);
            } else {
                holder.bookmark.setImageResource(R.drawable.ic_add_circle_outline_white_24dp);
                collection.setMarked(false);
            }
        } else {
            if (db.hasPlace(mPlace.getId() , collection.getId())) {
                holder.bookmark.setImageResource(R.drawable.ic_small_tick);
                collection.setMarked(true);
            } else {
                holder.bookmark.setImageResource(R.drawable.ic_add_circle_outline_white_24dp);
                collection.setMarked(false);
            }
        }
        db.close();
    }
    private void onBookmarkClick(final Collection collection , final AddToCollectionViewHolder holder){
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (collection.isMarked()){
                    holder.bookmark.setImageResource(R.drawable.ic_add_circle_outline_white_24dp);
                    collection.setMarked(false);
                    removeFromDatabase(collection);
                } else {
                    holder.bookmark.setImageResource(R.drawable.ic_small_tick);
                    collection.setMarked(true);
                    addToDatabase(collection);
                }
            }
        });
    }

    private void removeFromDatabase(Collection collection){
        DatabaseController db = new DatabaseController(mContext);
        if (mEvent != null){
            db.removeEvent(mEvent.getId(),collection.getId());
        } else {
            db.removePlace(mPlace.getId(),collection.getId());
        }
        db.close();
    }
    private void addToDatabase(Collection collection){
        DatabaseController db = new DatabaseController(mContext);
        if (mEvent != null){
            db.addEvent(mEvent.getId(),collection.getId());
        } else
            db.addPlace(mPlace.getId(),collection.getId());
        db.close();
    }

    @Override
    public int getItemCount() {
        return mCollections.size();
    }
}
