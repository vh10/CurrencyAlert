package boss.currencyalarm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

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
        Log.e("aa", "aa");
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

        rateManager.updateRates(this);
    }

    void onUpdateCompleted() {
        Log.w("onupdateCompleted " + Double.parseDouble(items[2]), "" + rateManager.getRate("USD", "EUR"));

        for(int i=0; i < items.length; i += 3) {
            checkRate(items[i], items[i + 1], Double.parseDouble(items[i + 2]));
        }

        if(hitRates.size() > 0) {
            sendNotification(hitRates.get(0) + " - " +  hitRates.get(1) + ", and " +
                    hitRates.size() / 2 + " more have hit alarm rate.");
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
                        .setContentTitle("boss")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
