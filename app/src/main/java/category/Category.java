package category;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by User on 3/15/2018.
 */

public class Category implements Parcelable {

    private String id;
    private String image;
    private String name;
    private int position;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.image);
        dest.writeString(this.name);
        dest.writeInt(this.position);
    }

    public Category() {
    }

    protected Category(Parcel in) {
        this.id = in.readString();
        this.image = in.readString();
        this.name = in.readString();
        this.position = in.readInt();
    }

    public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel source) {
            return new Category(source);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
}
