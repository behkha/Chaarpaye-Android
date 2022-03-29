package event;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import posts.Author;
import posts.Link;
import posts.Media;
import posts.Meta;
import posts.Photographer;
import place.Place;
import posts.Tag;

/**
 * Created by User on 3/19/2018.
 */

public class Event implements Parcelable {

    private String id;
    private String title;
    private String content;
    private ArrayList<Media> medias;
    private Link link;
    private Meta meta;
    private Place place;
    private ArrayList<Author> authors;
    private ArrayList<Photographer> photographers;
    private ArrayList<Tag> tags;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<Media> getMedias() {
        return medias;
    }

    public void setMedias(ArrayList<Media> medias) {
        this.medias = medias;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public ArrayList<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(ArrayList<Author> authors) {
        this.authors = authors;
    }

    public ArrayList<Photographer> getPhotographers() {
        return photographers;
    }

    public void setPhotographers(ArrayList<Photographer> photographers) {
        this.photographers = photographers;
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
        dest.writeString(this.title);
        dest.writeString(this.content);
        dest.writeTypedList(this.medias);
        dest.writeParcelable(this.link, flags);
        dest.writeParcelable(this.meta, flags);
        dest.writeParcelable(this.place, flags);
        dest.writeTypedList(this.authors);
        dest.writeTypedList(this.photographers);
        dest.writeTypedList(this.tags);
    }

    public Event() {
    }

    protected Event(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.content = in.readString();
        this.medias = in.createTypedArrayList(Media.CREATOR);
        this.link = in.readParcelable(Link.class.getClassLoader());
        this.meta = in.readParcelable(Meta.class.getClassLoader());
        this.place = in.readParcelable(Place.class.getClassLoader());
        this.authors = in.createTypedArrayList(Author.CREATOR);
        this.photographers = in.createTypedArrayList(Photographer.CREATOR);
        this.tags = in.createTypedArrayList(Tag.CREATOR);
    }

    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel source) {
            return new Event(source);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
}
