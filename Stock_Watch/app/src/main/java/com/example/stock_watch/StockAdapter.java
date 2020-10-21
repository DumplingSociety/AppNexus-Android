package com.example.stock_watch;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<StockHolder> {
    private static final String TAG = "StockAdapter";

    private List<Stock> stockList;
    private MainActivity mainAct;

    public StockAdapter(List<Stock> stockList, MainActivity ma) { // pass the mainactivity and sets the two varibles for me
        this.stockList = stockList;
        this.mainAct = ma;
    }

    @NonNull
    @Override
    public StockHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) { //create view holders
        Log.d(TAG, "onCreateViewHolder: MAKING NEW StockHolder");

        View itemView = LayoutInflater.from(parent.getContext())                 // inflates the layout and create view holder and populate data and passing into the recyclerview
                .inflate(R.layout.stock_list_row, parent, false);

        itemView.setOnClickListener(mainAct);       // (onClinckListener in the MainActivity)  response for the onClinckListener
        itemView.setOnLongClickListener(mainAct);    // (onLongClickListerner in the MainActivity)

        return new StockHolder(itemView); // call the StockHolder(which I created ) and passing the itemview layout


    }

    // onBind is setting the data
    @Override
    public void onBindViewHolder (@NonNull StockHolder holder,int position){ // position is the index of the list
        Log.d(TAG, "onBindViewHolder: FILLING VIEW HOLDER  " + position);

        Stock n = stockList.get(position);

        holder.name.setText(n.getName()); //set holder title to the notes title
        holder.symbol.setText(n.getSymbol());
        holder.price.setText(String.format("%.2f", n.getPrice()));

        if(n.getChange_percent() > 0){
            holder.priceChange.setText("▲ "+String.format("%.2f", n.getPrice_change()));
            holder.percentChange.setText("("+String.format("%.2f", n.getChange_percent()) +"%)");
            holder.name.setTextColor(Color.GREEN);
            holder.price.setTextColor(Color.GREEN);
            holder.symbol.setTextColor(Color.GREEN);
            holder.priceChange.setTextColor(Color.GREEN);
            holder.percentChange.setTextColor(Color.GREEN);
        } else {
            holder.priceChange.setText("▼ "+String.format("%.2f", n.getPrice_change()));
            holder.percentChange.setText("("+String.format("%.2f", n.getChange_percent()) +"%)");
            holder.name.setTextColor(Color.RED);
            holder.price.setTextColor(Color.RED);
            holder.symbol.setTextColor(Color.RED);
            holder.priceChange.setTextColor(Color.RED);
            holder.percentChange.setTextColor(Color.RED);

        }

    }

    @Override
    public int getItemCount () { // counting how many items in the list
        return stockList.size();
    }
}
