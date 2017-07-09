package sasd97.java_blog.xyz.yandexweather.navigation.activities;

import android.app.Activity;
import android.content.Intent;

import java.lang.ref.WeakReference;

/**
 * Created by alexander on 09/07/2017.
 */

public class MoveForward extends ActivityCommandDecorator {

    private Class<?> destinationActivity;

    public MoveForward(Activity currentActivity, Class<?> destinationActivity) {
        super(currentActivity);
        this.destinationActivity = destinationActivity;
    }

    @Override
    protected void onApply(Activity activity) {
        activity.startActivity(obtainIntent(activity, destinationActivity));
    }
}
