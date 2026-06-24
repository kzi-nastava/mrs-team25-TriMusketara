package com.example.clickanddrive.dtosample;

public class ChatMessageDTO {
    private String message;
    private String senderEmail;
    private String receiverEmail;
    private String sentAt;
    private boolean seen;

    public ChatMessageDTO() {}

    public ChatMessageDTO(String message, String senderEmail, String receiverEmail, String sentAt, boolean seen) {
        this.message = message;
        this.senderEmail = senderEmail;
        this.receiverEmail = receiverEmail;
        this.sentAt = sentAt;
        this.seen = seen;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getSenderEmail() { return senderEmail; }
    public void setSenderEmail(String senderEmail) { this.senderEmail = senderEmail; }

    public String getReceiverEmail() { return receiverEmail; }
    public void setReceiverEmail(String receiverEmail) { this.receiverEmail = receiverEmail; }

    public String getSentAt() { return sentAt; }
    public void setSentAt(String sentAt) { this.sentAt = sentAt; }

    public boolean isSeen() { return seen; }
    public void setSeen(boolean seen) { this.seen = seen; }
}