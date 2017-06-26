package boss.currencyalarm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class DataManager {
    ArrayList<PairData> x = new ArrayList<>();
    LinearLayout content;
    Context context;

    DataManager(Context co, LinearLayout lay) {
        content = lay;
        context = co;
    }

    public void add(String a, String b) {
        View child = LayoutInflater.from(context).inflate(R.layout.pair_data, null);
        content.addView(child);

        x.add(new PairData(a, b, x.size(), child));

        Button del = (Button) child.findViewById(R.id.deletePair);
        delSetOnClick(del, x.get(x.size() - 1));

        Button addAlert = (Button) child.findViewById(R.id.addAlert);
        alertClick(addAlert, x.get(x.size() - 1));
    }

    public void delSetOnClick(final Button btn, final PairData el){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((ViewManager)el.data.getParent()).removeView(el.data);
                x.remove(el.poz);
            }
        });
    }

    public void alertClick(final Button bm, final PairData el) {
        bm.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public int size() {
        return x.size();
    }
}
