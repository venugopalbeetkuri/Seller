package com.example.db;

/**
 * Created by Venu gopal on 23-11-2016.
 */

public class StoreBO {

    String emailId;
    static String storeName;
    static String percentage;

    public StoreBO(String storeEmail, String storeName, String percentage){
        this.emailId = storeEmail;
        this.storeName = storeName;
        this.percentage = percentage;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public static String getStoreName() { return storeName; }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public static String getPercentage() {return percentage;}

    public void  setPercentage(String percentage) { this.percentage=percentage; }
}
