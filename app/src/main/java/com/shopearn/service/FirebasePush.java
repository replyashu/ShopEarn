package com.shopearn.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.shopearn.R;
import com.shopearn.activity.MainActivity;
import com.shopearn.global.AppController;

/**
 * Created by apple on 17/01/17.
 */

public class FirebasePush extends FirebaseMessagingService {

    private static final String TAG = "FCM Service";
    private String pushText;

    private static int i  ;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        pushText = remoteMessage.getNotification().getBody();

        sendNotification(getApplicationContext(),remoteMessage.getNotification().getTitle(), remoteMessage.getData().get("url"));
    }

    private void sendNotification(Context context, String title , String content){
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        SharedPreferences sp = context.getSharedPreferences("user", 0);

        String email = sp.getString("email", "guest");

//        String extraParams = "&affExtParam1=" + AppController.getInstance().getAndroidId()
//                + "&affExtParam2=" + email;



        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.shop)
                .setContentTitle(title)
                .setSound(soundUri)
                .setAutoCancel(true)
                .setContentText(pushText);



        Intent resultIntent = new Intent();

        if(content.contains("flipkart")){

            String extraParams = "&affExtParam1=" + AppController.getInstance().getAndroidId()
                    + "&affExtParam2=" + email;
            PackageManager manager = context.getPackageManager();
            try {
                Intent i = manager.getLaunchIntentForPackage("com.flipkart.android");
                if (i == null) {
                    throw new PackageManager.NameNotFoundException();
                }
                i.addCategory(Intent.ACTION_VIEW);
                i.setData(Uri.parse(content + extraParams));
                resultIntent = i;

            } catch (PackageManager.NameNotFoundException e) {
                context.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://affiliate.flipkart.com/install-app?affid=ashuinbit"+ extraParams)));
            }
        }
        else{
            if(content.contains("amazon")) {
                try {
                    resultIntent.setData(Uri.parse(content + AppController.getInstance().getAndroidId() + "~~email~~" + email));
//                    context.startActivity(new Intent(Intent.ACTION_VIEW,
//                            Uri.parse(content + AppController.getInstance().getAndroidId() + "~~email~~" + email)));
                } catch (ActivityNotFoundException e) {
//                                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=in.amazon.mShop.android.shopping")));
                }
            }
        }

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        stackBuilder.addParentStack(MainActivity.class);

        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(i, builder.build());
    }
}
