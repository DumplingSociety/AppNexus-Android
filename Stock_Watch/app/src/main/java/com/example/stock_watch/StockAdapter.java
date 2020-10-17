package com.example.stock_watch;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;




public class StockAdapter extends RecyclerView.Adapter<StockHolder> {


    private static final String TAG = "StockAdapter";

    private List<Stock> stockList;
    private MainActivity mainAct;

    StockAdapter(List<Stock> noteList, MainActivity ma) { // pass the mainactivity and sets the two varibles for me
        this.stockList = stockList;
        this.mainAct = ma;
    }

    @NonNull
    @Override
    public StockHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) { //create view holders
        Log.d(TAG, "onCreateViewHolder: MAKING NEW StockHolder");

        View itemView = LayoutInflater.from(parent.getContext())                 // inflates the layout and create view holder and populate data and passing into the recyclerview
                .inflate(R.layout.stock_list_row, parent, false);

        itemView.setOnClickListener(mainAct);       // (onClinckListener in the MainActivity) who response for the onClinckListener
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
        holder.price.setText(n.getPrice());

    }

    @Override
    public int getItemCount () { // counting how many items in the list
        return stockList.size();
    }


}
