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


public class NameLoaderRunnable  implements Runnable {

    private static final String TAG = "NameLoaderRunnable";
    private MainActivity mainActivity;  // for passing object back to MainActivity
    private static final String DATA_URL = "https://api.iextrading.com/1.0/ref-data/symbols";
    private static HashMap<String, String> stockInfo = new HashMap<>();
    private DatabaseHandler databaseHandler;

    public NameLoaderRunnable(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void run() {
        Uri dataUri = Uri.parse(DATA_URL); // turns url to uri
        String urlToUse = dataUri.toString(); // converts uri to string
        Log.d(TAG, "run: " + urlToUse);

        // builder up string version of Json
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

       // final ArrayList<Stock> StockList = parseJSON(s);
        stockInfo = parseJSON(s);
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
            //    if (StockList != null)
              //      Toast.makeText(mainActivity, "Loaded " + StockList.size() + " Stocks.", Toast.LENGTH_LONG).show();
                mainActivity.updateData(stockInfo);
            }
        });
    }


    // Add symble & names to symbol:name HashMap
    //private ArrayList<Stock> parseJSON(String s) {
    private HashMap<String, String> parseJSON(String s) {

        //ArrayList<Stock> StockList = new ArrayList<>();
     //   HashMap<String, String> stockInfo = new HashMap<>();
        try {
            JSONArray jObjMain = new JSONArray(s);

            for (int i = 0; i < jObjMain.length(); i++) {
                JSONObject jStock = (JSONObject) jObjMain.get(i);

                String symbol = jStock.getString("symbol");
                String name = jStock.getString("name");
                if (symbol != null && name != null){
                stockInfo.put(symbol, name);
                }
 //               databaseHandler.addStock(name, symbol);
                /*
                StockList.add(
                        new Stock(symbol, name));
                */
            }
            return stockInfo;
            //       return StockList;
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }





}
