package com.bridgelabz.note.view;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.bridgelabz.note.R;
import com.bridgelabz.note.addnotes.view.AddActivity;
import com.bridgelabz.note.editnote.view.EditNote;
import com.bridgelabz.note.notefragment.View.NoteFragment;
import com.bridgelabz.note.reminderfragment.view.ReminderFragment;

public class NotifyService extends Service{

    public class ServiceBinder extends Binder {
        NotifyService getService() {
            return NotifyService.this;
        }
    }

    private static final int NOTIFICATION = 123;

    public static final String INTENT_NOTIFY = "com.bridgelabz.note.view.INTENT_NOTIFY";

    private NotificationManager mNM;

    @Override
    public void onCreate() {
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IBinder mBinder = new ServiceBinder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent.getBooleanExtra(INTENT_NOTIFY, false))
            showNotification();

        return START_NOT_STICKY;
    }

    private void showNotification() {

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        CharSequence title = "Alarm!!";

        int icon = R.mipmap.ic_launcher;

        CharSequence text = "Your notification time is upon us.";

        long time = System.currentTimeMillis();

        Notification notification = new Notification(icon, text, time);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        mBuilder.setStyle(new NotificationCompat.InboxStyle());

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, NoteFragment.class), PendingIntent.FLAG_ONE_SHOT);

        notification = mBuilder
                .setContentTitle(AddActivity.title)
                .setContentText(AddActivity.decs)
                .setTicker("Note Reminder Alert!!")
                .setContentIntent(contentIntent)
                .setSound(uri)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(AddActivity.decs))
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();

        mNM.notify(NOTIFICATION, notification);

        stopSelf();

    }
}
