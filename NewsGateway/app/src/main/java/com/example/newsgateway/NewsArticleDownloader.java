package com.example.newsgateway;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class NewsArticleDownloader implements Runnable {

    private static final String TAG = "NewsArticle Runnable";
    private String source;
    //  private Source source;

    private NewsService.ServiceReceiver newsService;  // for passing object back to MainActivity
    private static final String DATA_URL = "https://newsapi.org/v2/top-headlines?pageSize=10&sources=";
    private static final String API_KEY = "e4d90f9c3de947d791d759bc1facabf1";
    private String source_id;
    private ArrayList<Article> articles = new ArrayList<>();
    public NewsArticleDownloader(NewsService.ServiceReceiver newsService, String source) {
        this.newsService = newsService;
        this.source = source;
    }

    private void processResults(String s) {

      //  final HashMap<String, HashSet<String>> sourceMap = parseJSON(s);
        articles = parseJSON(s);
        newsService.setArticles(this.articles);
    }

    @Override
    public void run() {
        Uri dataUri = Uri.parse(DATA_URL); // turns url to uri
        String urlToUse = dataUri.toString() + source + "&apiKey=" + API_KEY; // converts uri to string
        Log.d(TAG, "run: " + urlToUse);
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




    private ArrayList<Article> parseJSON(String s) {
        ArrayList<Article> articleArrayList = new ArrayList<>();
        try {
            JSONObject jObjMain = new JSONObject(s);
            JSONArray normalized = jObjMain.getJSONArray("articles");
            // Here we only want to regions and subregions
            for (int i = 0; i < normalized.length(); i++) {
                JSONObject sourcesObject = normalized.getJSONObject(i);
                String article_author = sourcesObject.getString("author");

                String article_title = sourcesObject.getString("title");
                String article_description = sourcesObject.getString("description");
                String article_url = sourcesObject.getString("url");
                String article_urlToImage = sourcesObject.getString("urlToImage");
                String article_publishedAt = sourcesObject.getString("publishedAt");

                Log.d(TAG, "parseJSON: \n"
                        + "author: " + article_author + "\n"
                        + "title: " + article_title + "\n"
                        + "description: " + article_description + "\n"
                        + "urlToImage: " + article_urlToImage + "\n"
                        + "publishedAt: " + article_publishedAt
                );

                articleArrayList.add(new Article(article_author, article_title, article_description, article_url, article_urlToImage, article_publishedAt));
            }
            return articleArrayList;
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }

        return null;
    }
}