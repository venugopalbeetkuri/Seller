package com.example.db;

/**
 * Created by Venu gopal on 30-11-2016.
 */

public class AcknowledgePoints {

    String status;
    String earnRedeemString;

    public AcknowledgePoints(String status, String earnRedeemString) {
        this.status = status;
        this.earnRedeemString = earnRedeemString;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEarnRedeemString() {
        return earnRedeemString;
    }

    public void setEarnRedeemString(String earnRedeemString) {
        this.earnRedeemString = earnRedeemString;
    }
}
