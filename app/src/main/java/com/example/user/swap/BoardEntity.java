package com.example.user.swap;

import java.io.Serializable;

public class BoardEntity implements Serializable {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    int id;
    String regDate;
    String category;
    String userId;
    String pName;
    String pWant;
    String pContent;
    int image_id;
    int dealState;



    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public String getpWant() {
        return pWant;
    }

    public void setpWant(String pWant) {
        this.pWant = pWant;
    }

    public String getpContent() {
        return pContent;
    }

    public void setpContent(String pContent) {
        this.pContent = pContent;
    }

    public int getImage_id() {
        return image_id;
    }

    public void setImage_id(int image_id) {
        this.image_id = image_id;
    }

    public int getDealState(){ return dealState;}

    public void setDealState(int dealState){ this.dealState = dealState; }
}
