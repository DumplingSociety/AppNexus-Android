package com.example.newsgateway;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.content.BroadcastReceiver;

import java.util.ArrayList;


public class NewsService extends Service {

    private static final String TAG = "NewsService";
    // important
    private boolean running = true;
    private static ArrayList<Article> articleArrayList =new ArrayList<>();;
    ServiceReceiver serviceReceiver;
    public NewsService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.i(TAG, "Service onBind");
        return null;  //  Don't want clients to bind to the service
    }


    // this is like onCreate for an Activity
    // This one is like onCreate for an Activity
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // create a ServiceReceiver object
        serviceReceiver = new ServiceReceiver();

        // create a IntentFilter for message from the service
        IntentFilter intentFilter = new IntentFilter(MainActivity.ACTION_MSG_TO_SERVICE);

        // register a ServiceReceiver broadcast reciver object using the intentFilter
        registerReceiver(serviceReceiver,intentFilter);
        Log.d(TAG, "onStartCommand: Service running");

        //Creating new thread for my service
        //ALWAYS write your long running tasks
        // in a separate thread, to avoid an ANR

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (running) {

                    try {
                        if(articleArrayList.isEmpty())
                        {
                            Thread.sleep(250);
                        }
                        else {
                            Intent intent = new Intent();
                            intent.setAction(MainActivity.ACTION_NEWS_STORY);
                            intent.putExtra(MainActivity.ARTICLE_LIST, articleArrayList);
                            sendBroadcast(intent);
                            articleArrayList.clear();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }

                Log.d(TAG, "run: Ending loop");
            }
        }).start();
        return Service.START_NOT_STICKY;

    }

    @Override
    public void onDestroy () {
        Log.d(TAG, "Service Destroyed");
        running = false;
        super.onDestroy();
    }




    class ServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // if the intent`s action type is ACTION_MSG_TO_SERVICE
            // get the Soruce from the intent`s extras
            Log.d(TAG, "onReceive: Reeving article id");
            String action = intent.getAction();
            if (action == null)
                return;

            switch (action) {
                case MainActivity.ACTION_MSG_TO_SERVICE:
                    String sourceId = "";
                    if (intent.hasExtra(MainActivity.SOURCE_ID)) {
                       sourceId = intent.getStringExtra(MainActivity.SOURCE_ID);
                 //       source = intent.getStringExtra("SourceID");
                //        temp=sourceId.replaceAll(" ","-");
                    }
                    // create NewsArticleDownloader
                    // Load the data

                        NewsArticleDownloader arl = new NewsArticleDownloader(this, sourceId);
                        new Thread(arl).start();

                    break;
                    }

            }

        public void setArticles(ArrayList<Article> list){
            articleArrayList.clear();
            articleArrayList.addAll(list);

        }
        }
    }




