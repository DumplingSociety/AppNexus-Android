package com.example.stock_watch;

import java.io.Serializable;

 public class Stock {
//public class Stock implements Serializable{

    private String name;
    private String symbol;
    private double price;
    private double priceChange;
    private double changePercent;


    public Stock(String name, String symbol, double price, double priceChange, double changePercent) {
        this.name = name;
        this.symbol = symbol;
        this.price = price;
        this.priceChange = priceChange;
        this.changePercent = changePercent;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getPrice() {
        return price;
    }

    public double getPrice_change() {
        return priceChange;
    }

    public double getChange_percent() {
        return changePercent;
    }

}
