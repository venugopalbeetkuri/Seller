package com.example.db;

/**
 * Created by Venu gopal on 23-11-2016.
 */

public class StoreBO {

    String emailId;
    String storeName;

    public StoreBO(String emailId, String storeName){
        this.emailId = emailId;
        this.storeName = storeName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
}
