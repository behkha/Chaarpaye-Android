package posts;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by User on 3/19/2018.
 */

public class Link implements Parcelable {

    private String webSite;

    public String getWebSite() {
        return webSite;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.webSite);
    }

    public Link() {
    }

    protected Link(Parcel in) {
        this.webSite = in.readString();
    }

    public static final Creator<Link> CREATOR = new Creator<Link>() {
        @Override
        public Link createFromParcel(Parcel source) {
            return new Link(source);
        }

        @Override
        public Link[] newArray(int size) {
            return new Link[size];
        }
    };
}
