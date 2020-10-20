package com.example.stock_watch;

import android.content.Context;
import android.content.DialogInterface;
import android.net.NetworkInfo;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
           //     refresh();
                Log.d(TAG, "onRefresh: its working ");
            }
        });

        loadFile();
    }

    @Override
    public void onClick(View v) {

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void loadFile() {

        Log.d(TAG, "loadFile: Loading JSON File");

        try {
            InputStream is = getApplicationContext().
                    openFileInput(getString(R.string.file_name));

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            //      JSONObject jsonObject = new JSONObject(sb.toString());
            JSONArray jsonArray = new JSONArray(sb.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("name");
                String symbol = jsonObject.getString("symbol");
                double lPrice = jsonObject.getDouble("latestPrice");
                double priceChange = jsonObject.getDouble("change");
                double changePercent = jsonObject.getDouble("changePercent");
//            notepad.setTitle(name);
                //           notepad.setDescription(desc);
                //         notepad.setDate(ldate);
                Stock stockload = new Stock(name, symbol, lPrice, priceChange, changePercent);
                stockList.add(stockload);
            }

            mAdapter.notifyDataSetChanged();
        } catch (FileNotFoundException e) {
            Toast.makeText(this, getString(R.string.no_file), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    public void updateData(HashMap<String, String> nameHM) {

        // here need a flag to check duplicate stock
        stockInfo.putAll(nameHM);
        databaseHandler.addStock(nameHM);
        mAdapter.notifyDataSetChanged();
    }

    public void addStock(Stock stock) {

        // here need a flag to check duplicate stock
        stockList.add(stock);
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
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return true;
        }

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        // if internet is connected
        if (netInfo != null && netInfo.isConnected()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // Create an edittext and set it to be the builder's view
            final EditText et = new EditText(this);
            // converts user input to all caps
            et.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
            et.setInputType(InputType.TYPE_CLASS_TEXT);
            et.setGravity(Gravity.CENTER_HORIZONTAL);
            builder.setView(et);


            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String name = et.getText().toString();
                    //func to find stock name from hashmap
                    stockNameFinder(name);

                    //if the input maches multple stocks
                    //if one stock matches
                    //no stock found

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
            //show no internet dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Stocks cannot be updated until an internet connection is established.");
            builder.setTitle("No Network Connection");

            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return true;
    }

    public void stockNameFinder(String userInput) {

        HashMap<String, String> retrieved = new HashMap<>();
        String curr_symb, curr_name;
        Set<String> symbols = stockInfo.keySet();
        Iterator name_iterator = symbols.iterator();


        while (name_iterator.hasNext()) {
            curr_symb = (String) name_iterator.next();
            curr_name = stockInfo.get(curr_symb);

            // if it matches more than one name/symbol
            if (curr_symb.contains(userInput) || curr_name.contains(userInput)) {
                retrieved.put(curr_symb, curr_name);
                // display a list of resulting stock symbols and company names in a dialog
                // add return selected stock from StockLoadRunner

            }
        } // no match found
        if (retrieved.size() == 0) {
            noStockDialog(userInput);
        } // matches one stock
        else if (retrieved.size() == 1) {
            Set<String> retrieved_symbols = retrieved.keySet();
            Iterator i = retrieved_symbols.iterator();
            loadStockData((String) i.next());
        } else {
            // match more than one stock name/symbol
            buildStockDialog(retrieved);
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

    protected void buildStockDialog(HashMap<String, String> retrieved) {
        int num = retrieved.size();
        final CharSequence[] sArray = new CharSequence[num];
        Set<String> keys = retrieved.keySet();
        Iterator riter = keys.iterator();
        final String symbols[] = new String[num];
        int i = 0;
        while (riter.hasNext()) {
            String symbol = riter.next().toString();
            sArray[i] = symbol + " -> " + retrieved.get(symbol);
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


    public void loadStockData(String symbol) {
        Stock newstock;
        Iterator stocks_it = stockList.iterator();
        while (stocks_it.hasNext()) {
            newstock = (Stock) stocks_it.next();

            if (newstock.getName().equals(symbol)) {
                // check duplication
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setIcon(R.drawable.baseline_warning_black_36);
                builder.setTitle("Duplicate Stock");
                builder.setMessage("Stock Symbol "+symbol+" is already displayed");
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
/*
    private boolean doNetCheck() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return false;
        }

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected()) {
            statusText.setText(R.string.connected);
        } else {
            Log.d(TAG, "doNetCheck: ");
            return false;
        }
    }
  */
}
