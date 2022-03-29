package posts;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by User on 3/19/2018.
 */

public class Media implements Parcelable {

    private String type;
    private String name;
    private String path;
    private String preview_path;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPreview_path() {
        return preview_path;
    }

    public void setPreview_path(String preview_path) {
        this.preview_path = preview_path;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.type);
        dest.writeString(this.name);
        dest.writeString(this.path);
        dest.writeString(this.preview_path);
    }

    public Media() {
    }

    protected Media(Parcel in) {
        this.type = in.readString();
        this.name = in.readString();
        this.path = in.readString();
        this.preview_path = in.readString();
    }

    public static final Creator<Media> CREATOR = new Creator<Media>() {
        @Override
        public Media createFromParcel(Parcel source) {
            return new Media(source);
        }

        @Override
        public Media[] newArray(int size) {
            return new Media[size];
        }
    };
}
