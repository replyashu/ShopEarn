package com.shopearn.tracker;

import android.content.Context;

import com.shopearn.global.AppController;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by apple on 15/01/17.
 */

public class GATracking {



    public static void trackClickEvents(Context context, String link){
        Tracker mTracker = AppController.getInstance().getDefaultTracker();

        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Click")
                .setAction(link)
                .build());
    }

    public static void trackScreenVisit(Context context, String screenName){
        Tracker mTracker = AppController.getInstance().getDefaultTracker();

        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Screen Visited")
                .setAction(screenName)
                .build());
    }
}
