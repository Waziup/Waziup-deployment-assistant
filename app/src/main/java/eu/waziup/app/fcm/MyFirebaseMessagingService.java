package eu.waziup.app.fcm;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import eu.waziup.app.R;
import eu.waziup.app.ui.notification.NotificationActivity;

import static eu.waziup.app.utils.AppConstants.PREF_KEY_FCM_ACCESS_TOKEN_NAME;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyGcmListenerService";

    @Override
    public void onMessageReceived(RemoteMessage message) {

        String image = message.getNotification().getIcon();
        String title = message.getNotification().getTitle();
        String text = message.getNotification().getBody();
        String sound = message.getNotification().getSound();
        String clickAcition = message.getNotification().getClickAction();

        int id = 0;
        Object obj = message.getData().get("id");
        if (obj != null) {
            id = Integer.valueOf(obj.toString());
        }

        this.sendNotification(new NotificationData(image, id, title, text, sound));
    }


    private void sendNotification(NotificationData notificationData) {


//        String logged_in = PreferenceManager.getDefaultSharedPreferences(this).getString("access_token", null);

//        if(logged_in != null) {

            Intent intent = new Intent(this, NotificationActivity.class);
            intent.putExtra(NotificationData.TEXT, notificationData.getTextMessage());

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            NotificationCompat.Builder notificationBuilder = null;

            String notification_message = notificationData.getTextMessage();
            String notification_title = notificationData.getTitle();
            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(notification_title)
                    .setContentText(notification_message)
                    .setAutoCancel(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentIntent(pendingIntent);


            if (notificationBuilder != null) {
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(notificationData.getId(), notificationBuilder.build());
            } else {
                Log.d(TAG, "could not create notification builder");
            }

//        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        String token = s;
        Log.v("token_refresh : ", s);

        //todo check out where the fcm token being changed other scenarios
        //saving the FCM token to sharePreference
        SharedPreferences.Editor editor = getSharedPreferences(PREF_KEY_FCM_ACCESS_TOKEN_NAME, MODE_PRIVATE).edit();
        editor.putString("FcmToken", s).apply();

        // when new token is generate, patch user model in the db
//        API_Access.setContext(getApplicationContext());
//        Retrofit retrofit = API_Access.getRetrofitInstance();
//        UserService userService = retrofit.create(UserService.class);

        String customer_id = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("user_id", null);
        String access_token = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("access_token", null);

//        CustomerUpdateInfo customerUpdateInfo = new CustomerUpdateInfo();
//        customerUpdateInfo.setDeviceId(token);

//        Call<Customer> customerCall =  userService.updateCustomerInfo(customer_id,customerUpdateInfo,access_token);
//        customerCall.enqueue(new Callback<Customer>() {
//            @Override
//            public void onResponse(Call<Customer> call, Response<Customer> response) {
//                Log.v("FCM token","FCM_token updated successfuly");
//            }
//
//            @Override
//            public void onFailure(Call<Customer> call, Throwable t) {
//                Log.v("FCM token", "unable to update FCM token");
//            }
//        });

    }
}