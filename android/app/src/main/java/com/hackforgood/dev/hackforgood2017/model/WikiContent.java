package com.hackforgood.dev.hackforgood2017.model;

import android.util.Log;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WikiContent {

    private String queryText = "";
    private JSONObject jsonResponse = null;

    public String getQueryText() {
        return queryText;
    }

    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }

    public JSONObject getJsonResponse() {
        return jsonResponse;
    }

    public void setJsonResponse(JSONObject jsonResponse) {
        this.jsonResponse = jsonResponse;
    }

    public boolean isAMedicine() {
        if (jsonResponse != null) {
            String str = jsonResponse.toString();
            //if (str.contains("Fórmula_química") && str.contains("Farmacocinética")) return true;
            if (str.contains("Ficha de medicamento")) return true;
        }

        return false;
    }

    public boolean redirects() {
        if (jsonResponse != null) {
            String str = jsonResponse.toString();
            if (str.contains("#REDIRECCIÓN")) {
                if (!getRedirectionText().equals("")) return true;
            }
        }

        return false;
    }

    public String getRedirectionText() {
        String response = jsonResponse.toString();

        String pattern = "#REDIRECCIÓN ";
        int indPattern = response.indexOf(pattern);
        response = response.substring(indPattern + pattern.length());

        pattern = "]]";
        indPattern = response.indexOf(pattern);
        response = response.substring(0, indPattern);

        return response.replaceAll("\\[", "");
    }
}
