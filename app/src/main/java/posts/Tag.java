package posts;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by User on 3/19/2018.
 */

public class Tag implements Parcelable {

    private String id;
    private String name;
    private String tagable_id;
    private String tag_id;
    private String tagable_type;

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

    public String getTagable_id() {
        return tagable_id;
    }

    public void setTagable_id(String tagable_id) {
        this.tagable_id = tagable_id;
    }

    public String getTag_id() {
        return tag_id;
    }

    public void setTag_id(String tag_id) {
        this.tag_id = tag_id;
    }

    public String getTagable_type() {
        return tagable_type;
    }

    public void setTagable_type(String tagable_type) {
        this.tagable_type = tagable_type;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.tagable_id);
        dest.writeString(this.tag_id);
        dest.writeString(this.tagable_type);
    }

    public Tag() {
    }

    protected Tag(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.tagable_id = in.readString();
        this.tag_id = in.readString();
        this.tagable_type = in.readString();
    }

    public static final Parcelable.Creator<Tag> CREATOR = new Parcelable.Creator<Tag>() {
        @Override
        public Tag createFromParcel(Parcel source) {
            return new Tag(source);
        }

        @Override
        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };
}
