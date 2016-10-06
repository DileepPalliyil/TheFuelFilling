package in.thefleet.thefuelfilling.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import in.thefleet.thefuelfilling.FleetsDBOpenHelper;
import in.thefleet.thefuelfilling.FleetsDataSource;
import in.thefleet.thefuelfilling.MainActivity;

/**
 * Created by DILEEP on 04-10-2016.
 * To start alarm on boot to save pending filling locally saved
 */
public class BootReceiver extends BroadcastReceiver  {

    private static final String BOOT_COMPLETED =
            "android.intent.action.BOOT_COMPLETED";
    private static final String QUICKBOOT_POWERON =
            "android.intent.action.QUICKBOOT_POWERON";


    public static final String TAG = "BootReceiver";
    private static final int INTERVAL = 1000 * 60;
    private static final int DELAY = 5000;

    private Context context;


    final MainActivity mainActivity = ((MainActivity) context);
    String saveFilter;

    @Override
    public void onReceive(final Context context, Intent intent) {
        this.context = context;

       // Log.d(TAG,"In Boot received");
        String action = intent.getAction();
        if (action.equals(BOOT_COMPLETED) ){
            saveFilter = FleetsDBOpenHelper.FLEETS_CKM + " is not null" + " and "
                    + FleetsDBOpenHelper.FLEETS_ERRFLG + " is null";
            Cursor cursor = context.getContentResolver().query(FleetsDataSource.CONTENT_URI,
                    FleetsDBOpenHelper.ALL_COLUMNS, saveFilter, null, null);
            Log.d(TAG,"Cursor count :"+cursor.getCount());
            if (cursor.getCount() > 0) {
                scheduleAlarm3();
            }

        }
    }

    // Setup a recurring alarm
    public void scheduleAlarm3() {

        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(context, NetworkBroadcast.class);

        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(context, NetworkBroadcast.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Setup periodic alarm every 5 seconds
        long firstMillis = System.currentTimeMillis(); // alarm is set right away
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
        alarm.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, DELAY,
                INTERVAL, pIntent);
    }

    public void cancelAlarm3() {
        Intent intent2 = new Intent(mainActivity, NetworkBroadcast.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(mainActivity, NetworkBroadcast.REQUEST_CODE,
                intent2, PendingIntent.FLAG_UPDATE_CURRENT);
        if (pIntent != null) {
            AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarm.cancel(pIntent);
        }
        Log.d(TAG, "Alarm cancelled");
    }


}