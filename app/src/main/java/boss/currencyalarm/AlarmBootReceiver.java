package boss.currencyalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmBootReceiver extends BroadcastReceiver {
    AlarmService alert = new AlarmService();
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                //alert.setAlarm(context); TODO CHANGE THIIS
        }
    }
}