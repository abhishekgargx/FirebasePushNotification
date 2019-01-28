package package_name;

import android.app.Notification;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 *
 * Author : Abhishek Garg
 *
 */


/**
 * This is notification service for whole app, it handles notification from Firebase (FCM)
 *  (whether you send it from backend or directly firebase)
 *
 */
public class NotificationService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //notification title
        String title = "";
        //notification body
        String body =  "";
        // notification custom data (i am getting activity name to open based on custom data i send)
        String activity_name = "";

        // HANDLING DATA FROM REMOTE MESSAGE FOR FIREBASE AND BACKEND
        // if notification object is like this ( Our Backend send this way and firebase send this way when app is background)
            //            {
            //                "to": "the device token"
            //                "data":{
            //                          "title":"New Notification!",
            //                          "body":"Test"
            //            },
            //                "priority":10
            //            }
            if(remoteMessage.getData() != null){
                JSONObject json = new JSONObject(remoteMessage.getData());
                Iterator itr = json.keys();

                while (itr.hasNext()) {
                    String key = (String) itr.next();
                    switch (key){
                        case "title" : {
                            try {
                                title = json.getString(key);
                            } catch (JSONException e) {
//                                Utility.showToast(this,"json exception in notification in title" );
                            }
                            break;
                        }
                        case "body" : {
                            try {
                                body = json.getString(key);
                            } catch (JSONException e) {
//                                Utility.showToast(this,"json exception in notification in body" );
                            }
                            break;
                        }
                        case "activity_name" : {
                            try {
                                activity_name = json.getString(key);
                            } catch (JSONException e) {
//                                Utility.showToast(this,"json exception in notification in activity name" );
                            }
                            break;
                        }
                    }

                }
            }

        // if notification object is like this (By Default Firebase (UI Version) send this way)
        //            {
        //                "to": "the device token"
        //                "notification":{
        //                                  "title":"New Notification!",
        //                                   "body":"Test"
        //            },
        //                "priority":10
        //            }
        if(remoteMessage.getNotification() != null){
            title = remoteMessage.getNotification().getTitle();
            body =  remoteMessage.getNotification().getBody();
        }


        // notification icon background color
        int notificationColor = getResources().getColor(R.color.color_orange);
        // notification icon
        int notificationIcon = getNotificationIcon();
        // notification sound
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        // notification channel id
        String channelId = "default"
        // notification id
        int notification_id = 123;
        //create notification for Oreo devices
        createNotificationChannel();
        // notification Action
        // open activity you want on press of notification
        Intent intent;
        PendingIntent pendingIntent = null;
         
        if (acitivity_name != null) {
                switch (activity_name){
                    case "open_pp_acitivity": {
                        intent = new Intent(this, PPActivity.class);
                        break;
                    }
                    case "open_google_com": {
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));
                        break;
                    }
                    default:{
                        intent = new Intent(this, MainActivity.class);
                    }
                }

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent,PendingIntent.FLAG_ONE_SHOT);
        }else{
            intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent,PendingIntent.FLAG_ONE_SHOT);
        }
        
        Notification notification = new NotificationCompat.Builder(this,channelId)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(notificationIcon)
                .setColor(notificationColor)
                .setSound(notificationSound)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .build();
        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
        //
        manager.notify(notification_id, notification);
    }
    
     @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        String channelId = getString(R.string.default_notification_channel_id);
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel_name";
            String description = "channel_discription";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // NOTIFICATION ICON ACCORDING TO CURRENT ANDROID VERSION
    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        // ic_stat_logo_black is logo generated for notification using tool : http://romannurik.github.io/AndroidAssetStudio/
        return useWhiteIcon ? R.drawable.ic_stat_logo_black : R.mipmap.ic_launcher;
    }
}
