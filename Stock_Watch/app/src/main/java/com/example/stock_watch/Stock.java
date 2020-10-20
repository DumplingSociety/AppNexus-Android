package com.example.stock_watch;

import java.io.Serializable;

 public class Stock {
//public class Stock implements Serializable{


    // Serializable needed to add as extra to intent

    private String name;
    private String symbol;
    private double price;
    private double priceChange;
    private double changePercent;
    private static  int ctr = 1;


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
/*
    public void setName(String name) {
        this.name = name;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setPrice_change(double priceChange) {
        this.priceChange = priceChange;
    }

    public void setChange_percent(double changePercent) {
        this.changePercent = changePercent;
    }
*/
}
