package io.boolio.android.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.ArrayList;
import java.util.List;

import io.boolio.android.MainActivity;
import io.boolio.android.R;
import io.boolio.android.callbacks.QuestionsCallback;
import io.boolio.android.models.Question;
import io.boolio.android.network.clients.BoolioQuestionClient;
import io.boolio.android.network.helpers.BoolioCallback;

/**
 * Created by Chris on 5/2/15.
 */
public class GCMService extends IntentService {
    public static final int GCM = 2939;
    public static final int QUESTION_UPDATE_ID = 1;
    public static final int FEED_UPDATE_ID = 2;

    public GCMService() {
        super("GCMService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            switch (messageType) {
                case GoogleCloudMessaging.
                        MESSAGE_TYPE_SEND_ERROR:
                    Log.e("GCM Error Message", "Error message received");
                    break;
                case GoogleCloudMessaging.
                        MESSAGE_TYPE_DELETED:
                    Log.w("GCM Unknown message", "Never seen this kind before");
                    // If it's a regular GCM message, do some work.
                    break;
                case GoogleCloudMessaging.
                        MESSAGE_TYPE_MESSAGE:
                    sendNotification(extras);
                    break;
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GCMReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(final Bundle bundle) {
        if (bundle == null)
            return;
        final NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        final PendingIntent contentIntent;
        Intent intent =  new Intent(this, MainActivity.class);

        switch (bundle.getString("collapse_key")) {
            case "new-feed":
                intent.setAction("new-feed");
                contentIntent = PendingIntent.getActivity(this, GCM + FEED_UPDATE_ID,
                        intent, 0);
               BoolioQuestionClient.api().getQuestionFeed(new ArrayList<String>(0), new BoolioCallback<List<Question>>() {
                   @Override
                   public void handle(List<Question> questionList) {
                       buildFeedUpdate(mNotificationManager, contentIntent, questionList.size());
                   }
               });
                break;
            default:
            case "boolio-question":
                intent.setAction("boolio-question");
                contentIntent = PendingIntent.getActivity(this, GCM + QUESTION_UPDATE_ID,
                        intent, 0);
                buildQuestionUpdate(mNotificationManager, contentIntent, bundle);
                break;
        }
    }

    /**
     * New Questions in Feed
     */
    private void buildFeedUpdate(NotificationManager mNotificationManager, PendingIntent contentIntent, int questions) {
        String message = "You've got new questions in your feed!";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.bear_square)
                .setContentTitle(questions + " New Questions!")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setContentText(message);

        builder.setContentIntent(contentIntent);
        mNotificationManager.notify(FEED_UPDATE_ID, builder.build());
    }

    /**
     * Question Answered
     */
    private void buildQuestionUpdate(NotificationManager mNotificationManager, PendingIntent contentIntent, Bundle bundle) {
        String question = bundle.getString("question");
        String message = bundle.getString("leftCount") + " say " + bundle.getString("left") + " |\n" + bundle.getString("rightCount") + " say " + bundle.getString("right");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.bear_square)
                .setContentTitle(question)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setContentText(message);

        builder.setContentIntent(contentIntent);
        mNotificationManager.notify(bundle.getString("id"), QUESTION_UPDATE_ID, builder.build());
    }


    public static void clearQuestionUpdate(Context context) {
        clear(context, QUESTION_UPDATE_ID);
    }

    public static void clearFeedUpdate(Context context) {
        clear(context, FEED_UPDATE_ID);
    }

    private static void clear(Context context, int id) {
        final NotificationManager mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.cancel(id);
    }

    public static void clearAll(Context context) {
        final NotificationManager mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.cancelAll();
    }

}
