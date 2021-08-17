package com.github.bggoranoff.tradegame.util;

import com.github.bggoranoff.tradegame.model.Asset;
import com.github.bggoranoff.tradegame.model.AssetType;

import java.util.HashMap;

public class AssetConstants {

    public static final int DIMENSIONS = 54;
    public static final int MARGIN = 10;

    public static final HashMap<String, Asset> STOCKS = new HashMap<String, Asset>(){
        {
            put("AAPL", new Asset("AAPL", AssetType.STOCK, "aapl"));
            put("AMZN", new Asset("AMZN", AssetType.STOCK, "amzn"));
            put("GOOG", new Asset("GOOG", AssetType.STOCK, "goog"));
            put("NFLX", new Asset("NFLX", AssetType.STOCK, "nflx"));
            put("TSLA", new Asset("TSLA", AssetType.STOCK, "tsla"));
            put("MSFT", new Asset("MSFT", AssetType.STOCK, "msft"));
            put("FB", new Asset("FB", AssetType.STOCK, "fb"));
            put("NOK", new Asset("NOK", AssetType.STOCK, "nok"));
            put("T", new Asset("T", AssetType.STOCK, "t"));
            put("MA", new Asset("MA", AssetType.STOCK, "ma"));
            put("SMSN.IL", new Asset("SMSN.IL", AssetType.STOCK, "smsn"));
            put("TWTR", new Asset("TWTR", AssetType.STOCK, "twtr"));
            put("F", new Asset("F", AssetType.STOCK, "f"));
            put("GE", new Asset("GE", AssetType.STOCK, "ge"));
            put("DIS", new Asset("DIS", AssetType.STOCK, "dis"));
            put("MRNA", new Asset("MRNA", AssetType.STOCK, "mrna"));
            put("PFE", new Asset("PFE", AssetType.STOCK, "pfe"));
            put("BABA", new Asset("BABA", AssetType.STOCK, "baba"));
            put("KO", new Asset("KO", AssetType.STOCK, "ko"));
            put("JPM", new Asset("JPM", AssetType.STOCK, "jpm"));
            put("SBUX", new Asset("SBUX", AssetType.STOCK, "sbux"));
            put("NVDA", new Asset("NVDA", AssetType.STOCK, "nvda"));
            put("BAC", new Asset("BAC", AssetType.STOCK, "bac"));
            put("ADBE", new Asset("ADBE", AssetType.STOCK, "adbe"));
        }
    };

    public static final HashMap<String, Asset> CRYPTO = new HashMap<String, Asset>(){
        {
            put("BTC-USD", new Asset("BTC-USD", AssetType.CRYPTO, "btc"));
            put("ETH-USD", new Asset("ETH-USD", AssetType.CRYPTO, "eth"));
            put("XRP-USD", new Asset("XRP-USD", AssetType.CRYPTO, "xrp"));
            put("LTC-USD", new Asset("LTC-USD", AssetType.CRYPTO, "ltc"));
            put("EOS-USD", new Asset("EOS-USD", AssetType.CRYPTO, "eos"));
            put("XTZ-USD", new Asset("XTZ-USD", AssetType.CRYPTO, "xtz"));
            put("ADA-USD", new Asset("ADA-USD", AssetType.CRYPTO, "ada"));
            put("MIOTA-USD", new Asset("MIOTA-USD", AssetType.CRYPTO, "miota"));
            put("XLM-USD", new Asset("XLM-USD", AssetType.CRYPTO, "xlm"));
            put("TRX-USD", new Asset("TRX-USD", AssetType.CRYPTO, "trx"));
            put("BNB-USD", new Asset("BNB-USD", AssetType.CRYPTO, "bnb"));
            put("UNI3-USD", new Asset("UNI3-USD", AssetType.CRYPTO, "uni"));
            put("DASH-USD", new Asset("DASH-USD", AssetType.CRYPTO, "dash"));
        }
    };

    public static final HashMap<String, Asset> COMMODITIES = new HashMap<String, Asset>(){
        {
            put("CL=F", new Asset("CL=F", AssetType.COMMODITY, "oil"));
            put("GC=F", new Asset("GC=F", AssetType.COMMODITY, "gold"));
            put("SI=F", new Asset("SI=F", AssetType.COMMODITY, "silver"));
            put("HG=F", new Asset("HG=F", AssetType.COMMODITY, "copper"));
            put("NG=F", new Asset("NG=F", AssetType.COMMODITY, "gas"));
            put("PL=F", new Asset("PL=F", AssetType.COMMODITY, "platinum"));
            put("SB=F", new Asset("SB=F", AssetType.COMMODITY, "sugar"));
            put("CT=F", new Asset("CT=F", AssetType.COMMODITY, "cotton"));
            put("CC=F", new Asset("CC=F", AssetType.COMMODITY, "cocoa"));
            put("ZW=F", new Asset("ZW=F", AssetType.COMMODITY, "wheat"));
        }
    };

    public static final HashMap<String, Asset> FOREX = new HashMap<String, Asset>(){
        {
            put("EURUSD=X", new Asset("EURUSD=X", AssetType.FOREX, "eur"));
            put("CADUSD=X", new Asset("CADUSD=X", AssetType.FOREX, "cad"));
            put("GBPUSD=X", new Asset("GBPUSD=X", AssetType.FOREX, "gbp"));
            put("NZDUSD=X", new Asset("NZDUSD=X", AssetType.FOREX, "nzd"));
            put("CHFUSD=X", new Asset("CHFUSD=X", AssetType.FOREX, "chf"));
        }
    };

    public static final HashMap<String, Asset> INDEX = new HashMap<String, Asset>(){
        {
            put("^GSPC", new Asset("^GSPC", AssetType.ETF, "snp500"));
            put("^GDAXI", new Asset("^GDAXI", AssetType.ETF, "ger30"));
            put("^DJI", new Asset("^DJI", AssetType.ETF, "dj"));
            put("^FTSE", new Asset("^FTSE", AssetType.ETF, "ftse"));
            put("^IXIC", new Asset("^IXIC", AssetType.ETF, "nasdaq"));
        }
    };
}
