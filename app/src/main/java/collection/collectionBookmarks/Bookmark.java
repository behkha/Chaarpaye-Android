package collection.collectionBookmarks;

import android.os.Parcel;
import android.os.Parcelable;

import event.Event;
import place.Place;

/**
 * Created by User on 3/21/2018.
 */

public class Bookmark implements Parcelable {

    private String collection_id;
    private String user_id;
    private String bookmarkable_id;
    private String bookamarkable_type;
    private Place place;
    private Event event;

    public String getCollection_id() {
        return collection_id;
    }

    public void setCollection_id(String collection_id) {
        this.collection_id = collection_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getBookmarkable_id() {
        return bookmarkable_id;
    }

    public void setBookmarkable_id(String bookmarkable_id) {
        this.bookmarkable_id = bookmarkable_id;
    }

    public String getBookamarkable_type() {
        return bookamarkable_type;
    }

    public void setBookamarkable_type(String bookamarkable_type) {
        this.bookamarkable_type = bookamarkable_type;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.collection_id);
        dest.writeString(this.user_id);
        dest.writeString(this.bookmarkable_id);
        dest.writeString(this.bookamarkable_type);
        dest.writeParcelable(this.place, flags);
        dest.writeParcelable(this.event, flags);
    }

    public Bookmark() {
    }

    protected Bookmark(Parcel in) {
        this.collection_id = in.readString();
        this.user_id = in.readString();
        this.bookmarkable_id = in.readString();
        this.bookamarkable_type = in.readString();
        this.place = in.readParcelable(Place.class.getClassLoader());
        this.event = in.readParcelable(Event.class.getClassLoader());
    }

    public static final Parcelable.Creator<Bookmark> CREATOR = new Parcelable.Creator<Bookmark>() {
        @Override
        public Bookmark createFromParcel(Parcel source) {
            return new Bookmark(source);
        }

        @Override
        public Bookmark[] newArray(int size) {
            return new Bookmark[size];
        }
    };
}
