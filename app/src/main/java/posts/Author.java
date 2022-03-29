package posts;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by User on 3/19/2018.
 */

public class Author implements Parcelable {

    private String id;
    private String name;
    private String authorable_id;
    private String author_id;
    private String authorable_type;

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

    public String getAuthorable_id() {
        return authorable_id;
    }

    public void setAuthorable_id(String authorable_id) {
        this.authorable_id = authorable_id;
    }

    public String getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(String author_id) {
        this.author_id = author_id;
    }

    public String getAuthorable_type() {
        return authorable_type;
    }

    public void setAuthorable_type(String authorable_type) {
        this.authorable_type = authorable_type;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.authorable_id);
        dest.writeString(this.author_id);
        dest.writeString(this.authorable_type);
    }

    public Author() {
    }

    protected Author(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.authorable_id = in.readString();
        this.author_id = in.readString();
        this.authorable_type = in.readString();
    }

    public static final Parcelable.Creator<Author> CREATOR = new Parcelable.Creator<Author>() {
        @Override
        public Author createFromParcel(Parcel source) {
            return new Author(source);
        }

        @Override
        public Author[] newArray(int size) {
            return new Author[size];
        }
    };
}
