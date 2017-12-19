package com.stiglmair.hydra.objects;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Quote {
    private String quote;
    private String author;
    private Date date;

    public Quote(String quote, String author) {
        this.quote = quote;
        this.author = author;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString(){
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        return "\"" + quote + "\"" + " - " + author + ", den " +  format.format(date);
    }
}
