package posts;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import event.Event;
import place.Place;

/**
 * Created by User on 3/11/2018.
 */

public class Post implements Parcelable {

    private String id;
    private String postable_id;
    private String postable_type;
    private Place place;
    private Event event;
    private ArrayList<Tag> tags;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPostable_id() {
        return postable_id;
    }

    public void setPostable_id(String postable_id) {
        this.postable_id = postable_id;
    }

    public String getPostable_type() {
        return postable_type;
    }

    public void setPostable_type(String postable_type) {
        this.postable_type = postable_type;
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

    public ArrayList<Tag> getTags() {
        return tags;
    }

    public void setTags(ArrayList<Tag> tags) {
        this.tags = tags;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.postable_id);
        dest.writeString(this.postable_type);
        dest.writeParcelable(this.place, flags);
        dest.writeParcelable(this.event, flags);
        dest.writeTypedList(this.tags);
    }

    public Post() {
    }

    protected Post(Parcel in) {
        this.id = in.readString();
        this.postable_id = in.readString();
        this.postable_type = in.readString();
        this.place = in.readParcelable(Place.class.getClassLoader());
        this.event = in.readParcelable(Event.class.getClassLoader());
        this.tags = in.createTypedArrayList(Tag.CREATOR);
    }

    public static final Parcelable.Creator<Post> CREATOR = new Parcelable.Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel source) {
            return new Post(source);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };
}
