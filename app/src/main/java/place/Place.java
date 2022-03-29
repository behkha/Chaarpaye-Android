package place;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import posts.Author;
import posts.City;
import posts.Link;
import posts.Location;
import posts.Media;
import posts.Meta;
import posts.Photographer;
import posts.Tag;

/**
 * Created by User on 3/19/2018.
 */

public class Place implements Parcelable {

    private String id;
    private String name;
    private String content;
    private String address;
    private Location location;
    private ArrayList<Media> medias;
    private Link link;
    private Meta meta;
    private City city;
    private ArrayList<Author> authors;
    private ArrayList<Photographer> photographers;
    private ArrayList<Tag> tags;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
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
        dest.writeString(this.name);
        dest.writeString(this.content);
        dest.writeString(this.address);
        dest.writeParcelable(this.location, flags);
        dest.writeTypedList(this.medias);
        dest.writeParcelable(this.link, flags);
        dest.writeParcelable(this.meta, flags);
        dest.writeParcelable(this.city, flags);
        dest.writeTypedList(this.authors);
        dest.writeTypedList(this.photographers);
        dest.writeTypedList(this.tags);
    }

    public Place() {
    }

    protected Place(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.content = in.readString();
        this.address = in.readString();
        this.location = in.readParcelable(Location.class.getClassLoader());
        this.medias = in.createTypedArrayList(Media.CREATOR);
        this.link = in.readParcelable(Link.class.getClassLoader());
        this.meta = in.readParcelable(Meta.class.getClassLoader());
        this.city = in.readParcelable(City.class.getClassLoader());
        this.authors = in.createTypedArrayList(Author.CREATOR);
        this.photographers = in.createTypedArrayList(Photographer.CREATOR);
        this.tags = in.createTypedArrayList(Tag.CREATOR);
    }

    public static final Creator<Place> CREATOR = new Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel source) {
            return new Place(source);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };
}
