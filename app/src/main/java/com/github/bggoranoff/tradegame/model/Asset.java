package com.github.bggoranoff.tradegame.model;

public class Asset {

    private String symbol;
    private String fileName;
    private String type;

    public Asset(String symbol, String type, String fileName) {
        this.symbol = symbol;
        this.type = type;
        this.fileName = fileName;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getFileName() {
        return fileName;
    }

    public String getType() {
        return type;
    }
}
