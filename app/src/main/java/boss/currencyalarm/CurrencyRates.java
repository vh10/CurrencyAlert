package boss.currencyalarm;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static boss.currencyalarm.R.id.fab;

public class CurrencyRates extends AppCompatActivity {

    final Context context = this;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    RateManager rateManager;
    DataManager pairData;

    static ArrayList<String> currency =  new ArrayList<>(Arrays.asList("USD", "EUR", "AUD", "CAD", "CHF", "CNH", "CZK", "DKK", "GBP", "HKD",
            "HUF", "INR", "JPY", "MXN", "NOK", "NZD", "PLN", "RON", "RUB", "SEK", "TRY", "ZAR"));

    static String[] longCurrency = {"USD", "AUD", "EUR", "GBP", "INR", "RON", "RUB", "ZC", "XAU", "NG", "CL",
            "XPD", "XPT", "XAG", "NZD", "CAD", "CHF", "JPY", "MXN", "PLN", "SGD", "TRY", "DKK",
            "HKD", "NOK", "SEK", "CNH", "CZK", "HUF", "ZAR"};

    Spinner currency1, currency2;
    LinearLayout content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_rates);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        content = (LinearLayout) findViewById(R.id.content);
        pairData = new DataManager(context, content);
        rateManager = new RateManager(pairData);

        Button refreshButton = (Button) findViewById(R.id.button_refresh);
        refreshButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rateManager.updateRates();
            }
        });

        FloatingActionButton addPair = (FloatingActionButton) findViewById(fab);
        addPair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.add_view);
                dialog.setTitle("Add new currency pair");

                currency1 = (Spinner)dialog.findViewById(R.id.currency11);
                currency2 = (Spinner)dialog.findViewById(R.id.currency22);
                currency1.setAdapter(new CurrencyAdapter(context, R.layout.currency_select, currency));
                currency2.setAdapter(new CurrencyAdapter(context, R.layout.currency_select, currency));

                Button okButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                Button cancelButton = (Button) dialog.findViewById(R.id.dialogButtonCancel);

                okButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addNewCurrency(currency.get(currency1.getSelectedItemPosition()),
                                       currency.get(currency2.getSelectedItemPosition()));
                        dialog.dismiss();
                    }
                });

                cancelButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        restoreSavedData();
    }

    private void restoreSavedData() {
        String csvList = getSharedPreferences("boss", MODE_PRIVATE).getString("myList", "boss");
        if(csvList.equals("boss"))
            return;

        String[] items = csvList.split(",");

        for(int i=0; i < items.length; i += 2)
            pairData.add(items[i], items[i + 1]);

        rateManager.updateRates();
    }

    @Override
    public void onPause() {
        super.onPause();

        if(pairData.size() == 0)
            return;

        List<String> list = new ArrayList<>();
        for(PairData i : pairData.x) {
            list.add(i.el1);
            list.add(i.el2);
        }

        StringBuilder csvList = new StringBuilder();
        for(String s : list){
            csvList.append(s);
            csvList.append(",");
        }

        SharedPreferences.Editor editor = getSharedPreferences("boss", MODE_PRIVATE).edit();
        editor.putString("myList", csvList.toString());
        editor.commit();
    }

    private class CurrencyAdapter extends ArrayAdapter<String> {

        ArrayList<String> elem;

        public CurrencyAdapter(Context context, int textViewResourceId,
                               ArrayList<String> objects) {
            super(context, textViewResourceId, objects);
            this.elem = objects;
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {
//TODO ADD ICONS
            LayoutInflater inflater=getLayoutInflater();
            View row=inflater.inflate(R.layout.currency_select, parent, false);
            TextView label=(TextView)row.findViewById(R.id.currency);
            label.setText(elem.get(position));

            ImageView icon=(ImageView)row.findViewById(R.id.icon);

            String name = elem.get(position);
            if(position == 0) {
                name = "ic_statistics";
                int id = getResources().getIdentifier(name, "drawable", getPackageName());
                icon.setImageResource(id);
            }

            return row;
        }
    }

    private void addNewCurrency(String n1, String n2) {
        if(n1.equals(n2)) {
            // TODO message
            return;
        }
        for(PairData i : pairData.x) {
            if(i.el1.equals(n1) && i.el2.equals(n2)) {
                //TODO MESSAGE
                return;
            }
        }

        pairData.add(n1, n2);
        rateManager.updateRates();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_currency_rates, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // TODO Settings button clicked
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
