package boss.currencyalarm;

import android.app.Dialog;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class DataManager {
    ArrayList<PairData> x = new ArrayList<>();
    public LinearLayout content;
    public Context context;

    private AlarmService alarmService;

    private static int CHANGE_RATE = 500;

    DataManager(Context co, LinearLayout lay) {
        content = lay;
        context = co;
    }

    public void add(String a, String b, double alarmRate) {
        View child = LayoutInflater.from(context).inflate(R.layout.pair_data, null);
        content.addView(child);

        x.add(new PairData(a, b, x.size(), child));

        Button del = (Button) child.findViewById(R.id.deletePair);
        delSetOnClick(del, x.get(x.size() - 1));

        Button addAlert = (Button) child.findViewById(R.id.addAlert);
        alertClick(addAlert, x.get(x.size() - 1), alarmRate);

        ImageView icon1 = (ImageView) child.findViewById(R.id.icon1);
        String name = "flag_" + a.toLowerCase();
        int id = context.getResources().getIdentifier(name, "mipmap", context.getPackageName());
        icon1.setImageResource(id);

        ImageView icon2 = (ImageView) child.findViewById(R.id.icon2);
        name = "flag_" + b.toLowerCase();
        id = context.getResources().getIdentifier(name, "mipmap", context.getPackageName());
        icon2.setImageResource(id);
    }

    private void delSetOnClick(final Button btn, final PairData el){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((ViewManager)el.data.getParent()).removeView(el.data);
                x.remove(el.poz);
            }
        });
    }

    private void alertClick(final Button bm, final PairData el, double alarmRate) {
        el.alarmRate = alarmRate;
        if(Math.abs(alarmRate) > 0.0000001)
            addAlert();

        toggleButton(bm, el, alarmRate == 0 ? 1 : 2);

        bm.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((int)bm.getTag() == 2) {
                    toggleButton(bm, el, 1);
                    return;
                }

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.add_alert);
                dialog.setTitle("Select notification value:");

                TextView dialogTitle = (TextView)dialog.findViewById(R.id.addAlertTitle);
                dialogTitle.setText("Add alert for " + el.el1 + " - " + el.el2);

                final EditText rate = (EditText)dialog.findViewById(R.id.rateAlert);
                rate.setText(showInDialog("" + el.rate));

                final double cRate = el.rate;

                Button more = (Button) dialog.findViewById(R.id.higherAlert);
                more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        double newRate = Double.parseDouble(rate.getText().toString()) + cRate / CHANGE_RATE;
                        rate.setText(showInDialog("" + newRate));
                    }
                });
                Button less = (Button) dialog.findViewById(R.id.lowerAlert);
                less.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        double newRate = Double.parseDouble(rate.getText().toString()) - cRate / CHANGE_RATE;
                        rate.setText(showInDialog("" + newRate));
                    }
                });

                Button okButton = (Button) dialog.findViewById(R.id.alertAdd);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String r = rate.getText().toString();
                        for(int i = 0, nr = 0; i < r.length(); ++i) {
                            if(r.charAt(i) == '.') {
                                ++nr;
                                if(nr > 1) {
                                    Snackbar.make(content, "Wrong rate format!", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();

                                    dialog.dismiss();
                                    return;
                                }
                                continue;
                            }

                            if(!Character.isDigit(r.charAt(i))) {
                                Snackbar.make(content, "Wrong rate format!", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();

                                dialog.dismiss();
                                return;
                            }
                        }

                        toggleButton(bm, el, 2);

                        el.setAlarmRate(Double.parseDouble(r));

                        addAlert();
                        dialog.dismiss();
                    }
                });

                Button cancelButton = (Button)dialog.findViewById(R.id.alertCancel);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
    }

    private boolean toggleButton(Button bm, PairData el, int toggle) {
        bm.setTag(toggle);
        if(toggle == 1) {
            cancelAlert(el);
            bm.setBackgroundResource(R.mipmap.ic_alert);

            return true;
        }
        else
            bm.setBackgroundResource(R.mipmap.ic_cancel_alarm);
        return false;
    }

    private void addAlert() {
        if(alarmService == null)
            alarmService = new AlarmService();
        alarmService.setAlarm(context);
    }

    private void cancelAlert(PairData el) {
        if(alarmService == null)
            return;

        el.setAlarmRate(0.0);
        for(int i = 0; i < x.size(); ++i) if(x.get(i).alarmRate != 0)
            return;

        alarmService.cancelAlarm(context);
    }

    private String showInDialog(String a) {
        a = a + "0000000";
        return a.substring(0, Math.min(a.length(), 9));
    }

    public int size() {
        return x.size();
    }
}
