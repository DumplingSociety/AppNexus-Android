package com.example.stock_watch;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class StockHolder extends RecyclerView.ViewHolder {
    public TextView symbol;
    public TextView name;
    public TextView price;
    public TextView priceChange;
    public TextView percentChange;


    public StockHolder(View itemView) {
        super(itemView);
        symbol = itemView.findViewById(R.id.symbol);
        name = itemView.findViewById(R.id.name);
        price = itemView.findViewById(R.id.stockPrice);
        priceChange = itemView.findViewById(R.id.priceChange);
        percentChange = itemView.findViewById(R.id.priceChangeRate);
    }
}
