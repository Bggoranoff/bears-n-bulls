package com.github.bggoranoff.tradegame.util;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.provider.BaseColumns;

import com.github.bggoranoff.tradegame.model.Position;
import com.github.bggoranoff.tradegame.model.Wallet;

public class DatabaseManager {

    public static final String _ID = BaseColumns._ID;
    public static final String DB_NAME = "Positions";
    public static final String TABLE_NAME = "positions";
    public static final String SYMBOL = "symbol";
    public static final String PRICE = "price";
    public static final String QUANTITY = "quantity";
    public static final String BUY = "buy";
    public static final String TIME = "time";

    public static void openOrCreateTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                _ID + " INTEGER PRIMARY KEY, " +
                SYMBOL + " VARCHAR, " +
                TIME + " INTEGER, " +
                PRICE + " REAL, " +
                QUANTITY + " REAL, " +
                BUY + " INTEGER" +
                ")"
        );
    }

    public static void savePosition(SQLiteDatabase db, Position position) {
        ContentValues values = new ContentValues();
        values.put(SYMBOL, position.getSymbol());
        values.put(TIME, position.getTimeInMillis());
        values.put(PRICE, position.getPrice());
        values.put(QUANTITY, position.getQuantity());
        values.put(BUY, position.isBuy());
        db.insert(TABLE_NAME, null, values);
    }

    public static void deletePosition(SQLiteDatabase db, Position position) {
        try {
            db.delete(
                    TABLE_NAME,
                    SYMBOL + " = ? AND " + TIME + " = ?",
                    new String[] {position.getSymbol(), String.valueOf(position.getTimeInMillis())}
            );
        } catch(SQLiteException ex) {
            ex.printStackTrace();
        }
    }

    public static void deletePositions(SQLiteDatabase db, String symbol) {
        try {
            db.delete(TABLE_NAME, SYMBOL + " = ?", new String[] {symbol});
        } catch(SQLiteException ex) {
            ex.printStackTrace();
        }
    }

    public static void wipe(SQLiteDatabase db) {
        try {
            db.delete(TABLE_NAME, null, null);
        } catch(SQLiteException ex) {
            ex.printStackTrace();
        }
    }

    // Returns full wallet, money must be set from sharedPreferences
    public static Wallet getWallet(SQLiteDatabase db) {
        Wallet result = new Wallet();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        int symbolIndex = cursor.getColumnIndex(SYMBOL);
        int priceIndex = cursor.getColumnIndex(PRICE);
        int quantityIndex = cursor.getColumnIndex(QUANTITY);
        int buyIndex = cursor.getColumnIndex(BUY);
        int timeIndex = cursor.getColumnIndex(TIME);

        try {
            cursor.moveToFirst();
            do {
                String symbol = cursor.getString(symbolIndex);
                float price = cursor.getFloat(priceIndex);
                float quantity = cursor.getFloat(quantityIndex);
                boolean buy = cursor.getInt(buyIndex) == 1;
                long time = cursor.getLong(timeIndex);
                Position position = new Position(symbol, time, price, quantity, buy);
                result.addPosition(position);
            } while (cursor.moveToNext());
        } catch(CursorIndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
        cursor.close();

        return result;
    }
}
