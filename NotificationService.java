package package_name;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonObject;
import com.tutorhive.app.R;
import com.tutorhive.app.constants.ApiConstants;
import com.tutorhive.app.constants.ClassConstants;
import com.tutorhive.app.service.model.wrapper.GeneralResponseWrapper;
import com.tutorhive.app.service.remote.ApiHandler;
import com.tutorhive.app.service.remote.RetrofitInstance;
import com.tutorhive.app.util.ClassUtility;
import com.tutorhive.app.util.SharedPrefManager;
import com.tutorhive.app.view.startScreen.splash.SplashActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Author : Abhishek Garg
 */


/**
 * This is notification service for whole app, it handles notification from Firebase (FCM)
 * (whether you send it from backend or directly fcm)
 */
public class FirebaseNotificationService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        //notification title
        String title = "";
        //notification body
        String body = "";
        String activity_name = "";
        String chatRoomId = "";
        String chatName = "";
        String chatType = "";
        // open activity you want on press of notification
        Intent intent;
        PendingIntent pendingIntent = null;
        // chat
        if (remoteMessage.getData() != null && remoteMessage.getData().size() > 0) {
            JSONObject json = new JSONObject(remoteMessage.getData());
            Iterator itr = json.keys();

            while (itr.hasNext()) {
                String key = (String) itr.next();
                switch (key) {
                    case ClassConstants.Notification.TITLE:
                        title = json.optString(key);
                        break;
                    case ClassConstants.Notification.BODY:
                        body = json.optString(key);
                        break;
                    case ClassConstants.Notification.ACTIVITY_NAME:
                        activity_name = json.optString(key);
                        break;
                    case ClassConstants.Notification.CHAT_ROOM:
                        chatRoomId = json.optString(key);
                        break;
                    case ClassConstants.Notification.CHAT_NAME:
                        chatName = json.optString(key);
                        break;
                    case ClassConstants.Notification.CHAT_TYPE:
                        chatType = json.optString(key);
                        break;
                }
            }
        }

/*         if notification object is like this (By Default Firebase send this)
                    {
                        "to": "the device token"
                        "notification":{
                                          "title":"New Notification!",
                                           "body":"Test"
                    },
                        "priority":10
                    }*/
        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
        }

        // notification icon background color
        int notificationColor = getResources().getColor(R.color.yellow);
        // notification icon
        int notificationIcon = getNotificationIcon();
        // notification sound
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        // notification channel id
        String channelId = getString(R.string.notification_channel_id);
        // notification id
        int notification_id = 123;

        //create notification for Oreo devices
        createNotificationChannel();


        // wallet already created in pint and notification comes
        if (SharedPrefManager.getInstance(this).get(SharedPrefManager.Key.IS_SIGN_IN_COMPLETED, false)) {
            intent = ClassUtility.createIntentUsingClassName(FirebaseNotificationService.this, activity_name);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // sending data in a activity
            if(TextUtils.equals(activity_name, ClassConstants.Notification.CHAT_ACTIVITY)){
                intent.putExtra(ClassConstants.Bundle.ID, chatRoomId);
                intent.putExtra(ClassConstants.Bundle.NAME, chatName);
                intent.putExtra(ClassConstants.Bundle.TYPE, chatType);
            }
            // task builder
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntentWithParentStack(intent);
            pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT);
            // pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        }
        // app is installed but not sign in yet
        else {
            intent = new Intent(this, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        }

        // Notification Object
        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSmallIcon(notificationIcon)
                .setColor(notificationColor)
                .setShowWhen(true)
                .setSound(notificationSound)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .build();
        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());

        manager.notify(notification_id, notification);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        String channelId = getString(R.string.notification_channel_id);
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.notification_channel_name);
            String description = getString(R.string.notification_channel_discription);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    // NOTIFICATION ICON ACCORDING TO CURRENT ANDROID VERSION
    private int getNotificationIcon() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            return R.drawable.ic_notification_icon;
        }
        return R.mipmap.ic_launcher;
    }


    /**
     * additional code for Backend to register new token to server
     * onNewToken - if new token is generated by fcm it provide callback to onNewToken()
     * sendRegistrationToServer - api , send new token to backend
     */

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        // this method is also called, very first time, when app installed
         sendRegistrationToServer(s);
    }

    public void sendRegistrationToServer(String token) {
        // your backend api call to update token

    }


}
