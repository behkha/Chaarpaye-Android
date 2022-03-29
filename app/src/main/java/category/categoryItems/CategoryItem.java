package category.categoryItems;

import android.os.Parcel;
import android.os.Parcelable;

import event.Event;
import place.Place;

/**
 * Created by User on 3/22/2018.
 */

public class CategoryItem implements Parcelable {

    private Place place;
    private Event event;
    private String objectType;

    public CategoryItem(Place place, Event event, String objectType) {
        this.place = place;
        this.event = event;
        this.objectType = objectType;
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

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.place, flags);
        dest.writeParcelable(this.event, flags);
        dest.writeString(this.objectType);
    }

    protected CategoryItem(Parcel in) {
        this.place = in.readParcelable(Place.class.getClassLoader());
        this.event = in.readParcelable(Event.class.getClassLoader());
        this.objectType = in.readString();
    }

    public static final Creator<CategoryItem> CREATOR = new Creator<CategoryItem>() {
        @Override
        public CategoryItem createFromParcel(Parcel source) {
            return new CategoryItem(source);
        }

        @Override
        public CategoryItem[] newArray(int size) {
            return new CategoryItem[size];
        }
    };
}
