package net.sourceforge.squirrel_sql.plugins.hibernate.util;

public class Message {
    private Long id;
    private String text;
    private Message nextMessage;
    
    Message() {}
    
    public Message(String text) {
        this.text = text;
    }
    
    public Long getId() {
        return id;
    }
    
    @SuppressWarnings("unused")
    private void setId(long id) {
        this.id = id;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public Message getNextMessage() {
        return nextMessage;
    }
    
    public void setNextMessage(Message nextMessage) {
        this.nextMessage = nextMessage;
    }
}
