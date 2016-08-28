package com.eficksan.placingmessages;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Aleksei Ivshin
 * on 28.08.2016.
 */
public class PlaceMessage implements Parcelable {
    public final String id;
    public final double latitude;
    public final double longitude;
    public final String message;
    public final String userId;
    public final long timeStamp;

    public PlaceMessage(String id, double latitude, double longitude, String message, String userId, long timeStamp) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.message = message;
        this.userId = userId;
        this.timeStamp = timeStamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.message);
        dest.writeString(this.userId);
        dest.writeLong(this.timeStamp);
    }

    protected PlaceMessage(Parcel in) {
        this.id = in.readString();
        this.latitude = in.readInt();
        this.longitude = in.readInt();
        this.message = in.readString();
        this.userId = in.readString();
        this.timeStamp = in.readLong();
    }

    public static final Parcelable.Creator<PlaceMessage> CREATOR = new Parcelable.Creator<PlaceMessage>() {
        @Override
        public PlaceMessage createFromParcel(Parcel source) {
            return new PlaceMessage(source);
        }

        @Override
        public PlaceMessage[] newArray(int size) {
            return new PlaceMessage[size];
        }
    };
}
