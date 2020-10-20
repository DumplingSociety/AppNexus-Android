package com.example.stock_watch;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class StockLoaderRunnable  implements Runnable {

    private static final String TAG = "StockLoaderRunnable";
    private MainActivity mainActivity;  // for passing object back to MainActivity
    private static final String DATA_URL = "https://cloud.iexapis.com/stable/stock/";
    private static final String API_KEY = "pk_7f77e8c72f484b2b95e05613f00b6559";
    private String stockSymbol;
 //   private static HashMap<String, String> stockInfo = new HashMap<>();

    public StockLoaderRunnable(MainActivity mainActivity, String stockSymbol) {
        this.mainActivity = mainActivity;
        this.stockSymbol = stockSymbol;
    }


    @Override
    public void run() {
        Uri dataUri = Uri.parse(DATA_URL); // turns url to uri
        String urlToUse = dataUri.toString() + stockSymbol + "/quote?token=" + API_KEY; // converts uri to string
        Log.d(TAG, "run: " + urlToUse);

        // builder up string version of JSON
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect(); //useing GET

            // check if it connected using respond code
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "run: HTTP ResponseCode NOT OK: " + conn.getResponseCode());
                handleResults(null);
                return;
            }


            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) { // if we read a line, add to sb object
                sb.append(line).append('\n');
            }

            Log.d(TAG, "run: " + sb.toString());

            conn.disconnect();
        } catch (Exception e) {
            Log.e(TAG, "run: ", e);
            handleResults(null);
            return;
        }

        handleResults(sb.toString());
    }




    // Download s
    private void handleResults(String s) {

        if (s == null) {
            Log.d(TAG, "handleResults: Failure in data download");
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.downloadFailed();
                }
            });
            return;
        }

        final Stock stock = parseJSON(s);
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mainActivity.addStock(stock);
            }
        });
    }



    //private ArrayList<Stock> parseJSON(String s) {
    private Stock parseJSON(String s) {

        ArrayList<Stock> StockList = new ArrayList<>();
        try {
            //JSONArray jObjMain = new JSONArray(s);
            JSONObject jObjMain = new JSONObject(s);

            String symbol = jObjMain.getString("symbol");
            String name = jObjMain.getString("companyName");
            Double price = jObjMain.getDouble("latestPrice");
            Double priceChange = jObjMain.getDouble("change");
            Double changePercent = jObjMain.getDouble("changePercent");

            Stock stock = new Stock(symbol, name, price, priceChange, changePercent);

            return stock;
            //       return StockList;
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

}
