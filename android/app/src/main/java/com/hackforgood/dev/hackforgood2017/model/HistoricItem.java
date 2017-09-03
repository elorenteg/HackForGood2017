package com.hackforgood.dev.hackforgood2017.model;

public class HistoricItem {
    private int code;
    private String name;

    public HistoricItem(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
