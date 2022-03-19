package com.example.browser.modal;

import java.util.ArrayList;

public class Tabs {
    String url;
    String Table_name;

    public Tabs(String url, String table_name, int table_id) {
        this.url = url;
        Table_name = table_name;
    }

    public Tabs() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTable_name() {
        return Table_name;
    }

    public void setTable_name(String table_name) {
        Table_name = table_name;
    }

}
