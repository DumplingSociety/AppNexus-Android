package com.example.stock_watch;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHandler";

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    // DB Name
    private static final String DATABASE_NAME = "StockAppDb";

    // DB Table Name
    private static final String TABLE_NAME = "StockWatchTable";

    ///DB Columns
    private static final String SYMBOL = "stockSymbol";
    private static final String COMPANY = "CompanyName";


    // DB Table Create Code
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    SYMBOL + " TEXT not null unique," +
                    COMPANY + " TEXT not null )" ;


    private SQLiteDatabase database;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = getWritableDatabase(); // Inherited from SQLiteOpenHelper
        Log.d(TAG, "DatabaseHandler: C`tor DONE");
    }

    // DB Add
    //public void addStock(Stock stock) {
    public void addStock(HashMap<String, String> stock){
        Log.d(TAG, "addStock: Adding" + stock.size() );

        ContentValues values = new ContentValues();
        values.put(SYMBOL, stock.get(SYMBOL));
        values.put(COMPANY, stock.get(COMPANY));
        database.insert(TABLE_NAME, null, values);

        Log.d(TAG, "addStock: Add Complete");

    }

    //DB Delete
    public void deleteStock(String symbol) {
        Log.d(TAG, "deleteStock: Deleting Stock " + symbol);
        int cnt = database.delete(TABLE_NAME, SYMBOL +" = ?", new String[]{symbol});
        Log.d(TAG, "deleteStock: " + cnt);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // onCreate is only called is the DB does not exist
        Log.d(TAG, "onCreate: Making New DB");
        db.execSQL(SQL_CREATE_TABLE);
    }

    // DB Load All
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public ArrayList<String[]> loadStocks() {

        ArrayList<String[]> stocks = new ArrayList<>();
        Cursor cursor = database.query(
                TABLE_NAME, // The table to query
                new String[]{SYMBOL, COMPANY }, // The columns to return
                null, // The columns for the WHERE clause
                null, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                null); // The sort order

        if (cursor != null) {
            cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); i++) {
                String symbol = cursor.getString(0);
                String company = cursor.getString(1);
                stocks.add(new String[]{symbol, company});

            }
            cursor.close();
        }
        return stocks;
    }

    void dumpDbToLog() {
        Cursor cursor = database.rawQuery("select * from " + TABLE_NAME, null);
        if (cursor != null) {
            cursor.moveToFirst();
            Log.d(TAG, "dumpDbToLog: ");
            for (int i = 0; i < cursor.getCount(); i++) {
                String name = cursor.getString(0);
                String symbol = cursor.getString(1);

                Log.d(TAG, "dumpDbToLog: " +
                        String.format("%s %-18s", COMPANY + ":", name) +
                        String.format("%s %-18s", SYMBOL + ":", symbol) );
                cursor.moveToNext();
            }
            cursor.close();
        }
        Log.d(TAG, "dumpDbToLog: ");
    }

}
