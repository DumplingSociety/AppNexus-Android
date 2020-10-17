package com.example.stock_watch;

public class Stock {
    private String name;
    private String symbol;
    private double price;
    private double price_change;
    private double change_percent;

    public Stock(String name, String symbol){
        this.price = price;
        this.price_change = price_change;
        this.change_percent = change_percent;

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
        return price_change;
    }

    public double getChange_percent() {
        return change_percent;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setPrice_change(double price_change) {
        this.price_change = price_change;
    }

    public void setChange_percent(double change_percent) {
        this.change_percent = change_percent;
    }

}
