package boss.currencyalarm;

import android.view.View;
import android.widget.TextView;

public class PairData {
    public String el1, el2;
    public double rate;
    public int poz;
    public View data;

    public PairData(String e1, String e2, int po, View d) {
        el1 = e1;
        el2 = e2;
        poz = po;
        data = d;
    }

    public void displayData() {
        TextView t = (TextView) data.findViewById(R.id.pairData);

        String r = "" + rate;
        r = r.substring(0, Math.min(r.length(), 7));
        t.setText("1 " + el1 + " = " + r + " " + el2);
    }
}
