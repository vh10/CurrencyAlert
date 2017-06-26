package boss.currencyalarm;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import javax.net.ssl.HttpsURLConnection;

public class RateManager {
    Map<String, Double> rates = new TreeMap<>();
    DataManager pairData;

    public RateManager(DataManager pa) {
        rates.put("USD", 1.0);

        pairData = pa;
    }

    private void add(String el) {
        if(!rates.keySet().contains(el) && !el.equals("USD"))
            rates.put(el, 0.0);
    }

    public void updateRates() {
        for(PairData i : pairData.x) {
            add(i.el1);
            add(i.el2);
        }

        new UpdateDataTask().execute(1);
    }

    private class UpdateDataTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... nr) {
            String response = "er";

            String Url = "https://forex.1forge.com/1.0.1/quotes?" + getPairs(pairData.x);

            InputStream stream;
            HttpsURLConnection connection;
            try {
                URL url = new URL(Url);

                connection = (HttpsURLConnection) url.openConnection();
                connection.setReadTimeout(3000);
                connection.setConnectTimeout(3000);
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
                Log.e("download ER:", e.toString());
            }

            return response;
        }

        String getPairs(ArrayList<PairData> pairs) {
            String url = "pairs=";

            for(String el : rates.keySet()) if(!el.equals("USD")) {
                url = url + "USD" + el + ",";
            }

            return url.substring(0, url.length() - 1);
        }

        @Override
        protected void onPostExecute(String response) {
            changeData(response);
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

    void changeData(String response) {
        int pos = 0;

        Log.e("res", response);

        for(int i = 1; i < rates.keySet().size(); ++i) {
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

        changeDisplayData();
    }

    void changeDisplayData() {
        for(PairData i : pairData.x) {
            i.rate = getRate(i.el1, i.el2);
            i.displayData();
        }
    }

    public double getRate(String e1, String e2) {
        return rates.get(e2) / rates.get(e1);
    }
}