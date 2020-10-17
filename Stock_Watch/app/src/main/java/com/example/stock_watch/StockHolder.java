package com.example.stock_watch;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class StockHolder extends RecyclerView.ViewHolder {
    public TextView symbol;
    public TextView name;
    public TextView price;
    public TextView price_change;
    public TextView change_percent;


    public StockHolder(View itemView) {
        super(itemView);
        symbol = itemView.findViewById(R.id.tv_symbol);
        name = itemView.findViewById(R.id.tv_name);
        price = itemView.findViewById(R.id.stockprice);
        price_change = itemView.findViewById(R.id.priceChange);
        change_percent = itemView.findViewById(R.id.priceChangeRate);
    }
}
