package com.example.nour.searchengine;



public class SearchItem {
    private String content;
    private String title;
    private String url;

    public SearchItem(String content, String title, String url) {
        this.content = content;
        this.title = title;
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
