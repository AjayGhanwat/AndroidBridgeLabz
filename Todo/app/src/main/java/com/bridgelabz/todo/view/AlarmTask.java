package com.bridgelabz.todo.view;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import static com.bridgelabz.todo.view.NotifyService.INTENT_NOTIFY;

class AlarmTask implements Runnable {

    private final Calendar date;

    private final AlarmManager mAlarmManager;

    private final Context context;

    AlarmTask(Context context, Calendar date) {
        this.context = context;
        this.mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        this.date = date;
    }

    public void run() {
        Intent intent = new Intent(context, NotifyService.class);
        intent.putExtra(INTENT_NOTIFY, true);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

        mAlarmManager.set(AlarmManager.RTC, date.getTimeInMillis(), pendingIntent);
    }
}
