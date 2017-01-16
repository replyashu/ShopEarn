package com.shopearn.referrer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Created by apple on 16/01/17.
 */

public class InstallReceiver extends BroadcastReceiver {

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    @Override
    public void onReceive(Context context, Intent intent) {
        String referrer = intent.getStringExtra("referrer");

        sp = context.getSharedPreferences("refer", 0);
        editor = sp.edit();
        String refer = sp.getString("ref", "none");

        if(refer != null)
            editor.putString("ref", referrer);

        //Use the referrer
    }
}
