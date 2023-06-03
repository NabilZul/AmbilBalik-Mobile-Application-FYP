package com.example.ambilbalik;

import java.io.Serializable;

public class Lost implements Serializable {

    private String itemName, itemDescription, eventName,userUid,lostUid,ownerName,OwnerPhone,imaggeUrl,eventDate;

    public String getImaggeUrl() {
        return imaggeUrl;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public void setImaggeUrl(String imaggeUrl) {
        this.imaggeUrl = imaggeUrl;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerPhone() {
        return OwnerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        OwnerPhone = ownerPhone;
    }

    public String getLostUid() {
        return lostUid;
    }

    public void setLostUid(String lostUid) {
        this.lostUid = lostUid;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }
}
