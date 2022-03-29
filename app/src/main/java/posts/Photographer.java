package posts;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by User on 3/19/2018.
 */

public class Photographer implements Parcelable {

    private String id;
    private String name;
    private String photographerable_id;
    private String photographer_id;
    private String photographerable_type;

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

    public String getPhotographerable_id() {
        return photographerable_id;
    }

    public void setPhotographerable_id(String photographerable_id) {
        this.photographerable_id = photographerable_id;
    }

    public String getPhotographer_id() {
        return photographer_id;
    }

    public void setPhotographer_id(String photographer_id) {
        this.photographer_id = photographer_id;
    }

    public String getPhotographerable_type() {
        return photographerable_type;
    }

    public void setPhotographerable_type(String photographerable_type) {
        this.photographerable_type = photographerable_type;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.photographerable_id);
        dest.writeString(this.photographer_id);
        dest.writeString(this.photographerable_type);
    }

    public Photographer() {
    }

    protected Photographer(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.photographerable_id = in.readString();
        this.photographer_id = in.readString();
        this.photographerable_type = in.readString();
    }

    public static final Parcelable.Creator<Photographer> CREATOR = new Parcelable.Creator<Photographer>() {
        @Override
        public Photographer createFromParcel(Parcel source) {
            return new Photographer(source);
        }

        @Override
        public Photographer[] newArray(int size) {
            return new Photographer[size];
        }
    };
}
