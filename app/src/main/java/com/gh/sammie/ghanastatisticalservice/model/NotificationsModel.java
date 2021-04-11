package com.gh.sammie.ghanastatisticalservice.model;


public class NotificationsModel {

    private String title, message;
    private long timeStampValue;

    public NotificationsModel() {
    }

    public NotificationsModel(String title, String message, long timeStampValue) {
        this.title = title;
        this.message = message;
        this.timeStampValue = timeStampValue;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimeStampValue() {
        return timeStampValue;
    }

    public void setTimeStampValue(long timeStampValue) {
        this.timeStampValue = timeStampValue;
    }
}
