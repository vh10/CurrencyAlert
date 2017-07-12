package boss.currencyalarm;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.net.ssl.HttpsURLConnection;

public class RateManager {
    private Map<String, Double> rates = new TreeMap<>();
    private long lastUpdated;
    private int UI = 1;
    public boolean hasData = false;
    private DataManager pairData;
    SchedulingService service;

    public RateManager() {
        rates.put("USD", 1.0);
        UI = 0;
    }

    public RateManager(DataManager pa) {
        rates.put("USD", 1.0);

        pairData = pa;
    }

    public void updateRates() {
        if(UI == 1) {
            lastUpdated = System.currentTimeMillis();
            TextView lastU = (TextView) ((Activity) (pairData.context)).findViewById(R.id.lastUpdatedTime);
            lastU.setText("Last Updated: " + DateFormat.getDateTimeInstance().format(new Date()));
        }

        new UpdateDataTask().execute(1);
    }

    void updateRates(SchedulingService a) {
        service = a;

        new UpdateDataTask().execute(1);
    }

    private class UpdateDataTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... nr) {
            String response = "er";

            String Url = "https://forex.1forge.com/1.0.1/quotes?" + getPairs();

            InputStream stream;
            HttpsURLConnection connection;
            try {
                URL url = new URL(Url);

                connection = (HttpsURLConnection) url.openConnection();
                connection.setReadTimeout(3000);
                connection.setConnectTimeout(7000);
                connection.setRequestMethod("GET");
                // Already true by default but setting just in case; needs to be true since this request
                // is carrying an input (response) body.
                connection.setDoInput(true);

                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode != HttpsURLConnection.HTTP_OK) {
                    throw new IOException("HTTP error code: " + responseCode);
                }
                // Retrieve the response body as an InputStream.
                stream = connection.getInputStream();
                if (stream != null) {
                    response = readStream(stream, 50000000);
                }

                if (stream != null) {
                    stream.close();
                }
                connection.disconnect();

            } catch(Exception e){
                Snackbar.make(pairData.content, "Network Error!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return "er";
            }

            return response;
        }

        String getPairs() {
            String url = "pairs=";

            for(int i = 0; i < CurrencyRates.currency.size(); ++i) {
                if (!CurrencyRates.currency.get(i).equals("USD"))
                    url = url + "USD" + CurrencyRates.currency.get(i) + ",";
            }

            return url.substring(0, url.length() - 1) + "&api_key=YDetohQ80tzgXGnwJEwDHXKcSh2dc4hu";
        }

        @Override
        protected void onPostExecute(String response) {
            if(response.equals("er")) {
                Snackbar.make(pairData.content, "Network Error!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return;
            }

            updateStoredRates(response);

            if(UI == 1)
                changeDisplayData();
        }
    }

    /**
     * Converts the contents of an InputStream to a String.
     */
    private String readStream(InputStream stream, int maxLength) throws IOException {
        String result = null;
        // Read InputStream using the UTF-8 charset.
        InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
        // Create temporary buffer to hold Stream data with specified max length.
        char[] buffer = new char[maxLength];
        // Populate temporary buffer with Stream data.
        int numChars = 0;
        int readSize = 0;
        while (numChars < maxLength && readSize != -1) {
            numChars += readSize;
            readSize = reader.read(buffer, numChars, buffer.length - numChars);
        }
        if (numChars != -1) {
            // The stream was not empty.
            // Create String that is actual length of response body if actual length was less than
            // max length.
            numChars = Math.min(numChars, maxLength);
            result = new String(buffer, 0, numChars);
        }
        return result;
    }

    private void updateStoredRates(String response) {
        int pos = 0;

        for(int i = 1; i < CurrencyRates.currency.size() - 1; ++i) {
            String cu = "";
            while(response.charAt(pos) != 'U')
                ++pos;
            pos += 3;

            while(response.charAt(pos) != '"') {
                cu = cu + response.charAt(pos);
                ++pos;
            }

            while(!Character.isDigit(response.charAt(pos)))
                ++pos;
            long lastUpdated = 0;
            while(Character.isDigit(response.charAt(pos))) {
                lastUpdated = lastUpdated * 10 +
                        Character.getNumericValue(response.charAt(pos++));
            }

            while(!Character.isDigit(response.charAt(pos)))
                ++pos;

            int siz = 0;
            while(response.charAt(pos + siz) == '.' ||
                    Character.isDigit(response.charAt(pos + siz)))
                ++siz;

            rates.put(cu, Double.parseDouble(response.substring(pos, pos + siz)));
            pos += siz;
        }

        if(service != null)
            service.onUpdateCompleted();
    }

    private void changeDisplayData() {
        for(PairData i : pairData.x) {
            i.rate = getRate(i.el1, i.el2);
            i.displayData();
        }
    }

    public double getRate(String e1, String e2) {
        return rates.get(e2) / rates.get(e1);
    }
}