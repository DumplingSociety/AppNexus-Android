package com.example.stock_watch;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.net.ConnectivityManager;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, View.OnLongClickListener { // interface for adapter
    // The above lines are important - them make this class a listener
    // for click and long click events in the ViewHolders (in the recycler

    private static final String TAG = "MainActivity";
    private HashMap<String, String> stockInfo = new HashMap<>();
    private RecyclerView recyclerView; // Layout's recyclerview
    private final List<Stock> stockList = new ArrayList<>();
    private StockAdapter mAdapter; // Data to recyclerview adapter
    private DatabaseHandler databaseHandler;
    private final ArrayList<String[]> tempList = new ArrayList<>();
    private SwipeRefreshLayout swiper;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.activity_main_recycler);

        // make an adapter
        mAdapter = new StockAdapter(stockList, this);
        //sets up adapter to recycler
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // set up layout

        // Load name & symbols
        NameLoaderRunnable nameloaderrunnable = new NameLoaderRunnable(this);
        new Thread(nameloaderrunnable).start(); // call run method
        databaseHandler = new DatabaseHandler(this);

        databaseHandler.dumpDbToLog();
        ArrayList<String[]> nameList = databaseHandler.loadStocks();
        tempList.addAll(nameList);
        mAdapter.notifyDataSetChanged();

        swiper = findViewById(R.id.swiper);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "onRefresh: its working ");
                refresh();
            }
        });

    }

    //tap a stock to open the website
    @Override
    public void onClick(View v) {
        if (doNetCheck()) {
            int position = recyclerView.getChildLayoutPosition(v);
            Stock s = stockList.get(position);

            Intent chromeIntent = new Intent(Intent.ACTION_VIEW);
            String url = "https://www.marketwatch.com/investing/stock/" + s.getName();

            chromeIntent.setData(Uri.parse(url));
            startActivity(chromeIntent);
        }
        else {            // shows no internet warning
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Stocks Cannot Be Added Without A Network Connection");
            builder.setTitle("No Network Connection");

            AlertDialog dialog = builder.create();
            dialog.show();

        }
    }

    //long tap to delete a stock
    @Override
    public boolean onLongClick(View v) {
        int  position = recyclerView.getChildLayoutPosition(v);
         final Stock delStock = stockList.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.baseline_delete_black_24);
        builder.setTitle("Delete Stock");
        builder.setMessage("Delete Stock Symbol "+delStock.getName()+"?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                databaseHandler.deleteStock(delStock.getSymbol());
                stockList.remove(delStock);
                mAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("No",new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog,int id) {
            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();

        return false;
    }


    public void updateData(HashMap<String, String> nameHM) {

        // here need a flag to check duplicate stock
        stockInfo.putAll(nameHM);
        databaseHandler.addStock(nameHM);
        mAdapter.notifyDataSetChanged();
    }

    public void addStock(Stock stock) {

        stockList.add(stock);
        // sort stock list
        Collections.sort(stockList, new Comparator<Stock>() {
            @Override
            public int compare(Stock stock1, Stock stock2) {
                String name = stock1.getName();
                return name.compareTo(stock2.getName());
            }
        });
        mAdapter.notifyDataSetChanged();
    }


    public void downloadFailed() {
        stockList.clear();
        mAdapter.notifyDataSetChanged();
        // Toast

    }

    // inflates option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    // option menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // if internet is connected
        if (doNetCheck()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // Create an edittext and set it to be the builder's view
            final EditText et = new EditText(this);
            // converts user input to all caps
            et.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
            et.setInputType(InputType.TYPE_CLASS_TEXT);
            et.setGravity(Gravity.CENTER_HORIZONTAL);
            builder.setView(et);

            // click on OK (build the list of stock that related to the user input)
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String name = et.getText().toString();
                    //func to find stock name from hashmap
                    stockNameFinder(name);
                }
            }); // click on CANCEL
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //   Toast.makeText(MainActivity.this, "You changed your mind!", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setMessage("Please enter a stock symbol:");
            builder.setTitle("Stock Selection");

            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        } else {
            // shows no internet warning
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Stocks Cannot Be Added Without A Network Connection");
            builder.setTitle("No Network Connection");

            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return true;
    }

    public void stockNameFinder(String userInput) {
        String curSymbol, curName;
        HashMap<String, String> matchStockHM = new HashMap<>();

        Set<String> symbols = stockInfo.keySet();
        Iterator name_iterator = symbols.iterator();

        while (name_iterator.hasNext()) {
            curSymbol = (String) name_iterator.next();
            curName = stockInfo.get(curSymbol);

            // if it matches more than one name/symbol
            if (curSymbol.contains(userInput) || curName.contains(userInput)) {
                matchStockHM.put(curSymbol, curName);
            }
        } // no match found
        if (matchStockHM.size() == 0) {
            noStockDialog(userInput);
        } // matches one stock
        else if (matchStockHM.size() == 1) {
            Set<String> matchStockHM_symbols = matchStockHM.keySet();
            Iterator i = matchStockHM_symbols.iterator();
            loadStockData((String) i.next());
        } else {
            // match more than one stock name/symbol
            buildStockDialog(matchStockHM);
        }
    }

    public void noStockDialog(String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Symbol Not Found: " + name + "");
        builder.setMessage("Data for stock symbol");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    protected void buildStockDialog(HashMap<String, String> matchStockHM) {
        int hashMapSize = matchStockHM.size();
        final CharSequence[] sArray = new CharSequence[hashMapSize];
        Set<String> keys = matchStockHM.keySet();
        Iterator riter = keys.iterator();
        final String symbols[] = new String[hashMapSize];
        int i = 0;
        while (riter.hasNext()) {
            String symbol = riter.next().toString();
            sArray[i] = symbol + " -> " + matchStockHM.get(symbol);
            i++;
            symbols[i - 1] = symbol;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Make a selection");
        builder.setItems(sArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // use selected symbol to execute StockLoader
                loadStockData(symbols[which]);
                //dialog.dismiss();
            }
        });

        builder.setNegativeButton("NEVERMIND", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // load stock data from runnable and check duplication
    public void loadStockData(String symbol) {
        Stock stock;
        Iterator stocks_it = stockList.iterator();
        while (stocks_it.hasNext()) {
            stock = (Stock) stocks_it.next();

            if (stock.getName().equals(symbol)) {
                // check duplication
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setIcon(R.drawable.baseline_warning_black_36);
                builder.setTitle("Duplicate Stock");
                builder.setMessage("Stock Symbol " + symbol + " is already displayed");
                builder.setPositiveButton("OK",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog,int id) {
                    }
                });
                AlertDialog dialog=builder.create();
                dialog.show();
                return;
            }
        }
        StockLoaderRunnable stockloaderrunnable = new StockLoaderRunnable(this, symbol);
        new Thread(stockloaderrunnable).start(); // call run method
        
    }

    // check internet connections
    private boolean doNetCheck() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return false;
        }

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected()) {
            Log.d(TAG, "doNetCheck: connected to internet");
            return true;
        } else {
            Log.d(TAG, "doNetCheck: not connected to internet");
            return false;
        }
    }

    // swiper refresh func
    public void refresh() {

        // refresh with network connection
        if(doNetCheck()) {
            List<Stock> currList = new ArrayList<>(stockList);

            for (int i = stockList.size() - 1; i >= 0; i--) {
                stockList.remove(i);
            }
            stockList.clear();

            for (int i = currList.size() - 1; i >= 0; i--) {
                Stock s = currList.get(i);
                String oldStock = s.getName();
                StockLoaderRunnable stockloaderrunnable = new StockLoaderRunnable(this, oldStock);
                new Thread(stockloaderrunnable).start(); // call run method
                mAdapter.notifyDataSetChanged();
            }

            swiper.setRefreshing(false);
        }
        else {
            //no network refres
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Stocks Cannot Be Added Without A Network Connection");
            builder.setTitle("No Network Connection");

            AlertDialog dialog = builder.create();
            dialog.show();
            swiper.setRefreshing(false);

            // put all stocks in the dispay with price change and percent change to 0
            List<Stock> currList = new ArrayList<>(stockList);
            for (int i = currList.size() - 1; i >= 0; i--) {
                Stock s = currList.get(i);
                stockList.remove(i);
                Stock current = new Stock(s.getName(), s.getSymbol(), 0.0, 0.0, 0.0);
                addStock(current);
            }


        }
    }
}
