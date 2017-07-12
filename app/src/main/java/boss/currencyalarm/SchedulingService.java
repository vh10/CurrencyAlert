package boss.currencyalarm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;

public class SchedulingService extends IntentService {
    public SchedulingService() {
        super("SchedulingService");
    }

    public static final int NOTIFICATION_ID = 1;

    RateManager rateManager = new RateManager();
    ArrayList<String> hitRates = new ArrayList<>();
    String[] items;

    @Override
    protected void onHandleIntent(Intent intent) {
        restoreSavedData();

        AlarmService.completeWakefulIntent(intent);
    }

    private void restoreSavedData() {
        String csvList = getSharedPreferences("boss", MODE_PRIVATE).getString("myList", "boss");
        if(csvList.equals("boss"))
            return;

        items = csvList.split(",");

        if(items.length == 0)
            return;

        int i;
        for(i = 0; i < items.length; i += 3) {
            if(Math.abs(Double.parseDouble(items[i + 2])) >= 0.0000001)
                break;
        }
        if(i >= items.length) {
            AlarmService alarm = new AlarmService();
            alarm.setAlarm(getApplicationContext());
            alarm.cancelAlarm(getApplicationContext());
            return;
        }

        rateManager.updateRates(this);
    }

    void onUpdateCompleted() {
        for(int i=0; i < items.length; i += 3)
            checkRate(items[i], items[i + 1], Double.parseDouble(items[i + 2]));

        if(hitRates.size() > 0) {
            SharedPreferences sharedPrefs = PreferenceManager
                    .getDefaultSharedPreferences(this);

            if(sharedPrefs.getBoolean("prefNotificationVibration", true)) {
                Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);
            }

            if(sharedPrefs.getBoolean("prefNotificationSound", true)) {
                try {
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                    r.play();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            String text = hitRates.get(0) + " - " +  hitRates.get(1) + ", and " +
                    hitRates.size() / 2 + " more have hit alarm rate.";
            if(hitRates.size() == 2)
                text = hitRates.get(0) + " - " +  hitRates.get(1) + " hit alarm rate.";

            sendNotification(text);
        }
    }

    void checkRate(String el1, String el2, double rate) {
        if(Math.abs(rate) < 0.0000001)
            return;

        if((rate < 0 && rateManager.getRate(el1, el2) <= rate) ||
           (rate > 0 && rateManager.getRate(el1, el2) >= rate)) {
                hitRates.add(el1);
                hitRates.add(el2);
        }
    }

    private void sendNotification(String msg) {
        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, CurrencyRates.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Currency rate alarm!")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
