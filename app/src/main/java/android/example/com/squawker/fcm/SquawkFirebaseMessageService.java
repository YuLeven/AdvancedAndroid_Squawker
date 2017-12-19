package android.example.com.squawker.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.example.com.squawker.MainActivity;
import android.example.com.squawker.R;
import android.example.com.squawker.provider.SquawkContract;
import android.example.com.squawker.provider.SquawkProvider;
import android.example.com.squawker.util.Util;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by Yuri Levenhagen on 2017-12-19 as part
 * of the Udacity-Google Advanced Android App Development course.
 * <p>
 * The base example code belongs to The Android Open Source Project under the Apache 2.0 licence
 * All code further implemented as part of the course is under the same licence.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class SquawkFirebaseMessageService extends FirebaseMessagingService {

    private static final String LOG_TAG = SquawkFirebaseMessageService.class.getCanonicalName();
    private String mAuthor;
    private String mMessage;
    private String mDate;
    private String mAuthorKey;

    /**
     * This is called when a firebase data message is pushed to this app
     * @param remoteMessage - The FCM data message
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();

        if (data.size() > 0) {
            Log.d(LOG_TAG, "Received payload: " + data);
            setData(data);
            persistSquawk();
            broadcastSquawkNotification();
        }
    }

    /**
     * Sets the received squawk data to this class' properties
     * @param data - A Map<String, String> containing squawk data
     */
    private void setData(Map<String, String> data) {
        mAuthor = data.get(SquawkContract.COLUMN_AUTHOR);
        mMessage = data.get(SquawkContract.COLUMN_MESSAGE);
        mDate = data.get(SquawkContract.COLUMN_DATE);
        mAuthorKey = data.get(SquawkContract.COLUMN_AUTHOR_KEY);
    }

    /**
     * Persists a new squawk to our SQLite database
     */
    private void persistSquawk() {
       new Thread(new Runnable() {
           @Override
           public void run() {

               // Return early if we hadn't the needed information to persist.
               if (TextUtils.isEmpty(mAuthor)        ||
                       TextUtils.isEmpty(mMessage)   ||
                       TextUtils.isEmpty(mDate)      ||
                       TextUtils.isEmpty(mAuthorKey))
               {
                   Log.d(LOG_TAG, "Failed to persist squawk due to missing required information. Did you call setData()?");
                   return;
               }

               ContentValues newSquawk = new ContentValues();
               newSquawk.put(SquawkContract.COLUMN_AUTHOR, mAuthor);
               newSquawk.put(SquawkContract.COLUMN_MESSAGE, mMessage);
               newSquawk.put(SquawkContract.COLUMN_DATE, mDate);
               newSquawk.put(SquawkContract.COLUMN_AUTHOR_KEY, mAuthorKey);
               getContentResolver().insert(SquawkProvider.SquawkMessages.CONTENT_URI, newSquawk);
           }
       }).start();
    }

    /**
     * Broadcasts a notification when a new squawk is receved
     */
    private void broadcastSquawkNotification() {

        // Return early if we haven't an author and message to broadcase
        if (TextUtils.isEmpty(mAuthor) || TextUtils.isEmpty(mMessage)) { return; }

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Create the pending intent to launch the activity
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT
        );

        String author = mAuthor;
        // Clips the message to it fits the notification bar
        String message = Util.clipString(mMessage, 30);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_duck)
                .setContentTitle(String.format(getString(R.string.notification_message), author))
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Return early if the notification manager couldn't be fetched
        if (notificationManager == null) { return; }

        notificationManager.notify(0, notificationBuilder.build());
    }
}
