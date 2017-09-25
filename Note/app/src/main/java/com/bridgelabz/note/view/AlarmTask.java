package com.bridgelabz.note.view;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

import static android.app.Service.START_NOT_STICKY;
import static com.bridgelabz.note.view.NotifyService.INTENT_NOTIFY;

class AlarmTask implements Runnable {

    private final Calendar date;

    private final AlarmManager am;

    private final Context context;

    public AlarmTask(Context context, Calendar date) {
        this.context = context;
        this.am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        this.date = date;
    }

    public void run() {
        Intent intent = new Intent(context, NotifyService.class);
        intent.putExtra(INTENT_NOTIFY, true);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

        am.set(AlarmManager.RTC, date.getTimeInMillis(), pendingIntent);
    }
}
