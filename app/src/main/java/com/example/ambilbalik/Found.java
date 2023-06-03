package com.example.ambilbalik;

import java.io.Serializable;

public class Found implements Serializable {
    private String itemName, itemDescription, eventName,userUid,foundUid,itemStatus,founderName,founderPhone,itemLatitude,itemLongitude,imageUrl,eventDate,after1Year;

    public String getImageUrl() {
        return imageUrl;
    }

    public String getAfter1Year() {
        return after1Year;
    }

    public void setAfter1Year(String after1Year) {
        this.after1Year = after1Year;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getItemLatitude() {
        return itemLatitude;
    }

    public void setItemLatitude(String itemLatitude) {
        this.itemLatitude = itemLatitude;
    }

    public String getItemLongitude() {
        return itemLongitude;
    }

    public void setItemLongitude(String itemLongitude) {
        this.itemLongitude = itemLongitude;
    }

    public String getFounderName() {
        return founderName;
    }

    public void setFounderName(String founderName) {
        this.founderName = founderName;
    }

    public String getFounderPhone() {
        return founderPhone;
    }

    public void setFounderPhone(String founderPhone) {
        this.founderPhone = founderPhone;
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

    public String getFoundUid() {
        return foundUid;
    }

    public void setFoundUid(String foundUid) {
        this.foundUid = foundUid;
    }

    public String getItemStatus() {
        return itemStatus;
    }

    public void setItemStatus(String itemStatus) {
        this.itemStatus = itemStatus;
    }
}
