package android.view;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.InputEvent;

import androidx.annotation.NonNull;

public final class MotionEvent extends InputEvent implements Parcelable {

    public void setDisplayId(int displayId) {

    }

    @Override
    public int getDeviceId() {
        return 0;
    }

    @Override
    public int getSource() {
        return 0;
    }

    @Override
    public long getEventTime() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {

    }
}
