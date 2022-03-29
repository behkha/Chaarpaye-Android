package collection;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by User on 3/15/2018.
 */

public class Collection implements Parcelable {

    private String id;
    private String name;
    private String image;
    private String user_id;
    private int bookmarked_count;
    private boolean isMarked = false;

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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getBookmarked_count() {
        return bookmarked_count;
    }

    public void setBookmarked_count(int bookmarked_count) {
        this.bookmarked_count = bookmarked_count;
    }

    public boolean isMarked() {
        return isMarked;
    }

    public void setMarked(boolean marked) {
        isMarked = marked;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.image);
        dest.writeString(this.user_id);
        dest.writeInt(this.bookmarked_count);
        dest.writeByte(this.isMarked ? (byte) 1 : (byte) 0);
    }

    public Collection() {
    }

    protected Collection(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.image = in.readString();
        this.user_id = in.readString();
        this.bookmarked_count = in.readInt();
        this.isMarked = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Collection> CREATOR = new Parcelable.Creator<Collection>() {
        @Override
        public Collection createFromParcel(Parcel source) {
            return new Collection(source);
        }

        @Override
        public Collection[] newArray(int size) {
            return new Collection[size];
        }
    };
}
