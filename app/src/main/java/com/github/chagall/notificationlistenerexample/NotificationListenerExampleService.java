package com.github.chagall.notificationlistenerexample;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Arrays;
import java.util.List;

/**
 * MIT License
 *
 *  Copyright (c) 2016 Fábio Alves Martins Pereira (Chagall)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class NotificationListenerExampleService extends NotificationListenerService {

    /*
        These are the package names of the apps. for which we want to
        listen the notifications
     */
    private static final class ApplicationPackageNames {
        public static final String FACEBOOK_PACK_NAME = "com.facebook.katana";
        public static final String FACEBOOK_MESSENGER_PACK_NAME = "com.facebook.orca";
        public static final String WHATSAPP_PACK_NAME = "com.whatsapp";
        public static final String INSTAGRAM_PACK_NAME = "com.instagram.android";
    }

    /*
        These are the return codes we use in the method which intercepts
        the notifications, to decide whether we should do something or not
     */
    public static final class InterceptedNotificationCode {
        public static final int FACEBOOK_CODE = 1;
        public static final int WHATSAPP_CODE = 2;
        public static final int INSTAGRAM_CODE = 3;
        public static final int OTHER_NOTIFICATIONS_CODE = 4; // We ignore all notification with code == 4
    }


    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        int notificationCode = matchNotificationCode(sbn);

        if(notificationCode != InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE){
            Intent intent = new  Intent("com.thoughtifies.rajaram");
            intent.putExtra("Notification Code", notificationCode);
            sendBroadcast(intent);
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn){
        int notificationCode = matchNotificationCode(sbn);

        if(notificationCode != InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE) {

            StatusBarNotification[] activeNotifications = this.getActiveNotifications();

            if(activeNotifications != null && activeNotifications.length > 0) {
                for (int i = 0; i < activeNotifications.length; i++) {
                    if (notificationCode == matchNotificationCode(activeNotifications[i])) {
                        Intent intent = new  Intent("com.thoughtifies.rajaram");
                        intent.putExtra("Notification Code", notificationCode);
                        sendBroadcast(intent);
                        break;
                    }
                }
            }
        }
    }

    private int matchNotificationCode(StatusBarNotification sbn) {
        String TAG = "Actionn";
        String packageName = sbn.getPackageName();
        Log.d("hai", "matchNotificationCode: " + sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT).toString());
        RobotServices myRobo = new RobotServices(this);
        myRobo.postNotification(sbn);
        NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender(sbn.getNotification());
        List<NotificationCompat.Action> actions = wearableExtender.getActions();
        for(NotificationCompat.Action act : actions) {
            try{
                Log.d(TAG, "matchNotificationCode: trying to send " + act.getTitle());
                RemoteInput[] remoteInputs = act.getRemoteInputs();
                Log.d(TAG, "matchNotificationCode: length :"+remoteInputs.length);
                Intent localIntent = new Intent();
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle localBundle = sbn.getNotification().extras;
                int i = 0;
                for(RemoteInput remoteIn : act.getRemoteInputs()){
                    Log.d(TAG, "matchNotificationCode: i = " + i);
                    remoteInputs[i] = remoteIn;
                    localBundle.putCharSequence(remoteInputs[i].getResultKey(), "hi.....");//This work, apart from Hangouts as probably they need additional parameter (notification_tag?)
                    i++;
                }
                RemoteInput.addResultsToIntent(remoteInputs, localIntent, localBundle);
                act.actionIntent.send(this, 0, localIntent);
            }
            catch (PendingIntent.CanceledException e){
                Log.d(TAG, "matchNotificationCode: "+e);
            }
        }

        if(packageName.equals(ApplicationPackageNames.FACEBOOK_PACK_NAME)
                || packageName.equals(ApplicationPackageNames.FACEBOOK_MESSENGER_PACK_NAME)){
            return(InterceptedNotificationCode.FACEBOOK_CODE);
        }
        else if(packageName.equals(ApplicationPackageNames.INSTAGRAM_PACK_NAME)){
            return(InterceptedNotificationCode.INSTAGRAM_CODE);
        }
        else if(packageName.equals(ApplicationPackageNames.WHATSAPP_PACK_NAME)){
            return(InterceptedNotificationCode.WHATSAPP_CODE);
        }
        else{
            return(InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE);
        }
    }
}
