package com.example.newsgateway;
import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
public class NewsSourceDownloader implements Runnable{

    private MainActivity mainActivity;
    private static final String dataURL = "https://newsapi.org/v2/sources?language=en&country=us&category=&apiKey=e4d90f9c3de947d791d759bc1facabf1";
    private ArrayList<Source> sourceList = new ArrayList<>();

    public NewsSourceDownloader(MainActivity ma) {
        mainActivity = ma;
    }

    private void processResults(String s) {

        final HashMap<String, HashSet<String>> sourceMap = parseJSON(s);
        if (sourceMap != null) {
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.setSources(sourceMap, sourceList);
                }
            });
        }
    }

    @Override
    public void run() {
        Uri dataUri = Uri.parse(dataURL);
        String urlToUse = dataUri.toString();

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.addRequestProperty("User-Agent", "");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        processResults(sb.toString());
    }

    private HashMap<String, HashSet<String>> parseJSON(String s) {

        HashMap<String, HashSet<String>> sourceMap = new HashMap<>();
        try {
            JSONObject jObjMain = new JSONObject(s);
            JSONArray normalized = jObjMain.getJSONArray("sources");
            // Here we only want to regions and subregions
            for (int i = 0; i < normalized.length(); i++) {
                JSONObject sourcesObject = normalized.getJSONObject(i);
                String Network_name = sourcesObject.getString("name");
                String Network_id = sourcesObject.getString("id");
                String News_category = sourcesObject.getString("category");

                if (Network_name.isEmpty())
                    continue;

                if (News_category.isEmpty())
                    continue;

                sourceList.add(new Source(Network_name, News_category, Network_id));

                // set  category of network as value
                if (!sourceMap.containsKey(News_category))
                    sourceMap.put(News_category, new HashSet<String>());

                HashSet<String> rSet = sourceMap.get(News_category);
                if (rSet != null) {
                    // set  name of network as key
                    rSet.add(Network_name);
                }
            }
            return sourceMap;
        } catch (
                Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
