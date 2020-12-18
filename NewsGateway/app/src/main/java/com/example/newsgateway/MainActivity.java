package com.example.newsgateway;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

// color
import android.graphics.Color;
import android.text.style.ForegroundColorSpan;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final String ACTION_NEWS_STORY = "ACTION_NEWS_STORY";
    //  static final String ACTION_MSG_TO_SVC = "ACTION_MSG_TO_SVC";
    public static final String ACTION_MSG_TO_SERVICE = "ACTION_MSG_TO_SERVICE";
    public static final String SOURCE_ID = "SOURCE_ID";
    public static final String ARTICLE_LIST = "ARTICLE_LIST";

    //   private ArrayList<String> subRegionDisplayed = new ArrayList<>();
    // key is news category, value is the list of news networks
    private HashMap<String, ArrayList<String>> sourceDataMap = new HashMap<>();
    private ArrayList<String> NewsSourceList = new ArrayList<>();
    private ArrayList<Source> sourceData = new ArrayList<>();
    private List<Fragment> fragments;
    private Menu opt_menu;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private NewsReceiver newsReceiver;
    // setup fragment list
    //private List<Fragment> fragments;
    // setup  Adapter
    private MyPageAdapter pageAdapter;
    private ArrayAdapter<String> arrayAdapter;
    // setup pageViewer
    private ViewPager pager;
    private String currentSubNetwork;
    private boolean serviceRunning;
    private ArrayList<Article> artList;
    private String selected_name ;
    private ArrayList<String> listIn = new ArrayList<>();;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        newsReceiver = new NewsReceiver();
        //textView = findViewById(R.id.textView);

        // start NewsService (pass it the News list)
        if (!serviceRunning) {
            Intent intent = new Intent(this, NewsService.class);
            // intent.putExtra("STORIES", NewsSourceList);
            startService(intent);
            serviceRunning = true;
        }

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.drawer_list);

        //    mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, items));
        mDrawerList.setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selectItem(position);
                        mDrawerLayout.closeDrawer(mDrawerList);
                    }
                }
        );

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        );
        // setup fragments, pageAdapter and viewpager
        fragments = new ArrayList<>();

        pageAdapter = new MyPageAdapter(getSupportFragmentManager());

        pager = findViewById(R.id.viewpager);
        pager.setAdapter(pageAdapter);

        // Load the data
        if (sourceDataMap.isEmpty()) {
            NewsSourceDownloader arl = new NewsSourceDownloader(this);
            new Thread(arl).start();
        }
    }
    /*
    private void selectItem(int position) {
        currentNewsSource = srcList.get(position);
        Intent intent = new Intent(MainActivity.ACTION_MSG_TO_SERVICE);
        intent.putExtra(SOURCE_ID, currentNewsSource);
        sendBroadcast(intent);
        mDrawerLayout.closeDrawer(mDrawerList);
    }
    */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //     getMenuInflater().inflate(R.menu.action_menu, menu);
        Log.d(TAG, "onCreate: restoreState options");
        getMenuInflater().inflate(R.menu.option_menu, menu);
        opt_menu = menu;
        return true;
    }


    // You need the below to open the drawer when the toggle is clicked
    // Same method is called when an options menu item is selected.

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // if clicks on the drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "onOptionsItemSelected: mDrawerToggle " + item);
            return true; // done with this event
        }
        // change the title bar accordingly
        setTitle(item.getTitle());
        // clear the drawer
        NewsSourceList.clear();

        ArrayList<String> lst = sourceDataMap.get(item.getTitle().toString());
        if (lst != null) {
            NewsSourceList.addAll(lst);
        }

        arrayAdapter.notifyDataSetChanged();
        return super.onOptionsItemSelected(item);

    }
    // drawer item selected
    private void selectItem(int position) {

        // set the viewpager`s background to null
        pager.setBackground(null);

        selected_name = NewsSourceList.get(position);
        int j = 0;

        for ( j= 0; j<sourceData.size(); j++ )
        {
            //if(sourceData.get(j).getName().toString().equals("ddd"));
            String test = sourceData.get(j).getName();
            if(test == selected_name)
            {

                position = j;

                break;
            }

        }
        currentSubNetwork = sourceData.get(position).getId();

        setTitle(sourceData.get(position).getName());
        // send ID to the reciver of NewsService
        // create an intent ACTION_MSG_TO_SERVICE
        Intent intent = new Intent(MainActivity.ACTION_MSG_TO_SERVICE);
        intent.putExtra(SOURCE_ID, currentSubNetwork);
        sendBroadcast(intent);

        mDrawerLayout.closeDrawer(mDrawerList);
    }



    public void setSources(HashMap<String, HashSet<String>> sourceMapIn, ArrayList<Source> sourceList) {

        sourceDataMap.clear();

        // get network source data
        sourceData.addAll(sourceList) ;
        //  ArrayList<String> listIn = new ArrayList<>();
        for (String s : sourceMapIn.keySet()) {
            HashSet<String> hSet = sourceMapIn.get(s);
            if (hSet == null)
                continue;
            ArrayList<String> subRegions = new ArrayList<>(hSet);
            Collections.sort(subRegions);
            sourceDataMap.put(s, subRegions);
        }

        for(int i = 0 ;i< sourceList.size() ;i++ )
        {
            listIn.add(sourceList.get(i).getName());
        }
        // add ALL category
        sourceDataMap.put("all", listIn);
        // use tempList to sort the list (key value(name of network))
        ArrayList<String> tempList = new ArrayList<>(sourceDataMap.keySet());
        Collections.sort(tempList);

        // add them one by one to opt_menu
        for (String s : tempList)
            opt_menu.add(s);


        // used to populate the drawer list
        NewsSourceList.clear();
        //get the news network of the first category on the menu
        ArrayList<String> lst = sourceDataMap.get(tempList.get(0));
        if (lst != null) {
            NewsSourceList.addAll(lst);
        }


        arrayAdapter = new ArrayAdapter<>(this, R.layout.drawer_list_item, NewsSourceList);
        mDrawerList.setAdapter(arrayAdapter);
        // make sure the toggle drawer display right
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }


    // Page Adapter for Fragment
    private class MyPageAdapter extends FragmentPagerAdapter {
        private long baseId = 0;


        MyPageAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public long getItemId(int position) {
            // give an ID different from position when position has been changed
            return baseId + position;
        }

        /**
         * Notify that the position of a fragment has been changed.
         * Create a new ID for each position to force recreation of the fragment
         *
         * @param n number of items which have been changed
         */
        void notifyChangeInPosition(int n) {
            // shift the ID returned by getItemId outside the range of all previous fragments
            baseId += getCount() + n;
        }

    }
    @Override
    protected void onStop(){
        unregisterReceiver(newsReceiver);
        super.onStop();
    }

    // important for making class NewsReceiver run
    @Override
    protected void onResume(){
        IntentFilter filter = new IntentFilter(ACTION_NEWS_STORY);
        registerReceiver(newsReceiver, filter);
        super.onResume();
    }

    class NewsReceiver extends BroadcastReceiver {
        private static final String TAG = "NewsReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: Reeving article list");
            String action = intent.getAction();
            if (action == null)
                return;

            switch (action) {
                case ACTION_NEWS_STORY:
//                    ArrayList<Article> artList;
                    if (intent.hasExtra(ARTICLE_LIST)) {
                        artList = (ArrayList<Article>) intent.getSerializableExtra(ARTICLE_LIST);
                        reDoFragments(artList);
                    }
                    break;
            }
        }

        private void reDoFragments(ArrayList<Article> articles){
            Log.d(TAG, "reDoFragments: t");
            //setTitle(currentSource.getName());
            for(int i = 0; i < pageAdapter.getCount(); i++){
                pageAdapter.notifyChangeInPosition(i);
            }
            fragments.clear();
            for(int i = 0; i < articles.size(); i++){
                fragments.add(ArticleFragment.newInstance(articles.get(i),i+1, articles.size()));
            }
            pageAdapter.notifyDataSetChanged();
            pager.setCurrentItem(0);
            //      newsArticleArrayList = articles;
        }


    }
}