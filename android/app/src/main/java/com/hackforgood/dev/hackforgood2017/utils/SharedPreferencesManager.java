package com.hackforgood.dev.hackforgood2017.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferencesManager {
    private static SharedPreferences settings;

    private static SharedPreferences getSharedPreferences(Context context) {
        if (settings == null) {
            settings = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        }
        return settings;
    }

    public static void setStringValue(Context context, String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        if (value != null) {
            editor.putString(key, value);
        } else {
            editor.remove(key);
        }
        editor.commit();
    }

    public static void removeStringValue(Context context, String key) {
        setStringValue(context, key, null);
    }

    public static String getStringValue(Context context, String key, String defaultValue) {
        return getSharedPreferences(context).getString(key, defaultValue);
    }

    public static void setBooleanValue(Context context, String key, Boolean value) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        if (value != null)
            editor.putBoolean(key, value);
        else
            editor.remove(key);
        editor.commit();
    }

    public static void removeBooleanValue(Context context, String key) {
        setBooleanValue(context, key, null);
    }

    public static boolean getBooleanValue(Context context, String key, boolean defaultValue) {
        return getSharedPreferences(context).getBoolean(key, defaultValue);
    }

    public static void setIntValue(Context context, String key, Integer value) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        if (value != null)
            editor.putInt(key, value);
        else
            editor.remove(key);
        editor.commit();
    }

    public static void removeIntValue(Context context, String key) {
        setIntValue(context, key, null);
    }

    public static int getIntValue(Context context, String key, Integer defaultValue) {
        return getSharedPreferences(context).getInt(key, defaultValue);
    }

    public static void setLongValue(Context context, String key, Long value) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        if (value != null)
            editor.putLong(key, value);
        else
            editor.remove(key);
        editor.commit();
    }

    public static void removeLongValue(Context context, String key) {
        setLongValue(context, key, null);
    }

    public static long getLongValue(Context context, String key, Long defaultValue) {
        return getSharedPreferences(context).getLong(key, defaultValue);
    }

    public static void setFloatValue(Context context, String key, Float value) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        if (value != null)
            editor.putFloat(key, value);
        else
            editor.remove(key);
        editor.commit();
    }

    public static void removeFloatValue(Context context, String key) {
        setFloatValue(context, key, null);
    }

    public static float getFloatValue(Context context, String key, Float defaultValue) {
        return getSharedPreferences(context).getFloat(key, defaultValue);
    }

    public static void setDoubleValue(Context context, String key, Double value) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        if (value != null)
            editor.putLong(key, Double.doubleToRawLongBits(value));
        else
            editor.remove(key);
        editor.commit();
    }

    public static void removeDoubleValue(Context context, String key) {
        setDoubleValue(context, key, null);
    }

    public static double getDoubleValue(Context context, String key, Double defaultValue) {
        return Double.longBitsToDouble(getSharedPreferences(context).getLong(key, Double.doubleToLongBits(defaultValue)));
    }
}