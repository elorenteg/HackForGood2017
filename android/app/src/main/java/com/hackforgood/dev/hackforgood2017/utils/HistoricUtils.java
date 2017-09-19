package com.hackforgood.dev.hackforgood2017.utils;

import android.content.Context;

import com.hackforgood.dev.hackforgood2017.model.HistoricItem;

import java.util.ArrayList;

/**
 * Created by Marc on 04/09/2017.
 */

public class HistoricUtils {
    private static final String HISTORIC_MANAGER_INDEX = "INDEX_VALUE";
    private static final String HISTORIC_MANAGER_CODE_NAME = "ITEM_CODE_NAME";

    public static void saveInformationHistoric(Context context, int code, String name) {
        int currentIndex = getCurrentIndex(context);

        SharedPreferencesManager.setIntValue(context, HISTORIC_MANAGER_INDEX, currentIndex + 1);
        SharedPreferencesManager.setStringValue(context, currentIndex + "" + HISTORIC_MANAGER_CODE_NAME, code + "#" + name);
    }

    public static ArrayList<HistoricItem> getInformationHistoric(Context context) {
        int currentIndex = getCurrentIndex(context);

        ArrayList<HistoricItem> arrayHistoric = new ArrayList<>();
        for (int i = 0; i < currentIndex; ++i) {
            String preferencesValue = SharedPreferencesManager.getStringValue(context, i + "" + HISTORIC_MANAGER_CODE_NAME, "");

            if (!preferencesValue.equals("")) {
                if (preferencesValue.split("#").length > 1) {
                    int itemCode = Integer.parseInt(preferencesValue.split("#")[0]);
                    String itemName = preferencesValue.split("#")[1];
                    arrayHistoric.add(new HistoricItem(itemCode, itemName));
                }
            }
        }
        return arrayHistoric;
    }

    private static int getCurrentIndex(Context context) {
        return SharedPreferencesManager.getIntValue(context, HISTORIC_MANAGER_INDEX, 0);
    }
}
