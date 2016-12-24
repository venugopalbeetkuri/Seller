package com.example.db;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Chalam on 11/18/2016.
 */

public class PointsBO {

    private
    String deviceid;
    String type;
    String billAmount;
    String storeName;
    String points;
    String disCountAmount;
    String time;

    public PointsBO(String type,
            String billAmount,
            String storeName,
            String points,
            String deviceID,
            String disCountAmount,
            String time){

        this.type = type;
        this.billAmount =billAmount;
        this.storeName = storeName;
        this.points = points;
        this.deviceid = deviceID;
        this.disCountAmount = disCountAmount;

        //Calendar c = Calendar.getInstance();
        //SimpleDateFormat acceptdate = new SimpleDateFormat("ddMMyyyy");
        //String accept = acceptdate.format(time);

        this.time = time;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(String billAmount) {
        this.billAmount = billAmount;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getDeviceId() {
        return deviceid;
    }

    public void setDeviceId(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getDisCountAmount() {
        return disCountAmount;
    }

    public void setDisCountAmount(String disCountAmount) {
        this.disCountAmount = disCountAmount;
    }

    public String getTime(){return time;}

    public void setTime(String time) { this.time = time;}
}
