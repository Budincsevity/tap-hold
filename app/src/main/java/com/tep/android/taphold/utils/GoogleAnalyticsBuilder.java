package com.tep.android.taphold.utils;

import android.content.Context;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class GoogleAnalyticsBuilder {
    private static GoogleAnalyticsBuilder mInstance;

    public void sendActivity(String activityName, Context context) {
        Tracker t = GoogleAnalyticsHandler.getInstance().getTracker(
                GoogleAnalyticsHandler.TrackerName.APP_TRACKER, context);

        t.enableExceptionReporting(true);
        t.enableAdvertisingIdCollection(true);
        t.setScreenName(activityName);

        t.send(new HitBuilders.AppViewBuilder().build());
    }

    public static synchronized GoogleAnalyticsBuilder getInstance() {
        if (null == mInstance) {
            mInstance = new GoogleAnalyticsBuilder();
        }
        return mInstance;
    }
}
