package com.udacity.stockhawk.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.udacity.stockhawk.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public final class PrefUtils {

    private PrefUtils() { }

	/**
     *
     * @param context
     * @return
     */
    public static Set<String> getStocks(Context context) {
        String stocksKey = context.getString(R.string.pref_stocks_key);
        String initializedKey = context.getString(R.string.pref_stocks_initialized_key);
        String[] defaultStocksList = context.getResources().getStringArray(R.array.default_stocks);
        HashSet<String> defaultStocks = new HashSet<>(Arrays.asList(defaultStocksList));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean initialized = prefs.getBoolean(initializedKey, false);
        if (!initialized) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(initializedKey, true);
            editor.putStringSet(stocksKey, defaultStocks);
            editor.apply();
            return defaultStocks;
        }
        return prefs.getStringSet(stocksKey, new HashSet<String>());
    }

    private static void editStockPref(Context context, String symbol, Boolean add) {
        String key = context.getString(R.string.pref_stocks_key);
        Set<String> stocks = getStocks(context);
        if (add) {
            stocks.add(symbol);
        } else {
            stocks.remove(symbol);
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(key, stocks);
        editor.apply();
    }

    public static void addStock(Context context, String symbol) {
        editStockPref(context, symbol, true);
    }

    public static void removeStock(Context context, String symbol) {
        editStockPref(context, symbol, false);
    }

    public static String getDisplayMode(Context context) {
        String key = context.getString(R.string.pref_display_mode_key);
        String defaultValue = context.getString(R.string.pref_display_mode_default);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(key, defaultValue);
    }

    public static void toggleDisplayMode(Context context) {
        String key = context.getString(R.string.pref_display_mode_key);
        String absoluteKey = context.getString(R.string.pref_display_mode_absolute_key);
        String percentageKey = context.getString(R.string.pref_display_mode_percentage_key);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String displayMode = getDisplayMode(context);
        SharedPreferences.Editor editor = prefs.edit();
        if (displayMode.equals(absoluteKey)) {
            editor.putString(key, percentageKey);
        } else {
            editor.putString(key, absoluteKey);
        }
        editor.apply();
    }

	/**
     * Set the time when system refesh stocks.
     * @param context
     */
    public static void setLastRefesh(Context context)  {
        String keyLastRefresh = context.getString(R.string.pref_last_refresh_key);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(keyLastRefresh, new Date().getTime());
        editor.apply();
    }

	/**
     * Returns the time when system refesh stocks.
     * @param context
     * @return
     */
    public static String getLastRefesh(Context context){
        long lastRefeshMiliseconds = 0;
        String keyLastRefresh = context.getString(R.string.pref_last_refresh_key);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        lastRefeshMiliseconds = prefs.getLong(keyLastRefresh, lastRefeshMiliseconds);
        /* in case there is not refresh yet return never. */
        String lastRefreshStr = context.getString(R.string.never);
        if (lastRefeshMiliseconds>0) {
            Date lastRefreshDate = new Date(lastRefeshMiliseconds);
            DateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            lastRefreshStr = df.format(lastRefreshDate);
        }
        return lastRefreshStr;
    }
}

