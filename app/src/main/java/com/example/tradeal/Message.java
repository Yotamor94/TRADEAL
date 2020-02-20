package com.example.tradeal;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

public class Message implements Serializable {

    private String userEmail, content;
    private String oUserEmail;
    private String messageId;
    private Date date;

    public Message(String userEmail, String content, String oUserEmail, Date date) {
        this.userEmail = userEmail;
        this.content = content;
        this.oUserEmail = oUserEmail;
        this.messageId = messageId;
        this.date = date;
    }

    public Message() {
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getoUserEmail() {
        return oUserEmail;
    }

    public void setoUserEmail(String oUserEmail) {
        this.oUserEmail = oUserEmail;
    }
}
