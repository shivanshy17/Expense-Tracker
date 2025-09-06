package com.example.expensetracker.Model;

public class Data {

    private int amount;
    private String type;
    private String note;
    private String id;
    private String date;

    // Required empty constructor for Firebase
    public Data() {}

    public Data(int amount, String type, String note, String id, String date) {
        this.amount = amount;
        this.type = type;
        this.note = note;
        this.id = id;
        this.date = date;
    }

    // Getters
    public int getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    public String getNote() {
        return note;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    // Setters
    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
