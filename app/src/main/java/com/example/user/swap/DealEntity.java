package com.example.user.swap;

import java.io.Serializable;

public class DealEntity implements Serializable {
    String sellerId;
    String buyerId;
    String sellPd;
    String buyPd;
    int dealComplete;

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getSellPd() {
        return sellPd;
    }

    public void setSellPd(String sellPd) {
        this.sellPd = sellPd;
    }

    public String getBuyPd() {
        return buyPd;
    }

    public void setBuyPd(String buyPd) {
        this.buyPd = buyPd;
    }

    public int getDealComplete() {
        return dealComplete;
    }

    public void setDealComplete(int dealComplete) {
        this.dealComplete = dealComplete;
    }



}
