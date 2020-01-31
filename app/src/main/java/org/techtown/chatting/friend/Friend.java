package org.techtown.chatting.friend;

public class Friend {
    String id;
    String name;
    String state_message;

    public Friend(String id,String name, String state_message) {
        this.id = id;
        this.name = name;
        this.state_message = state_message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState_message() {
        return state_message;
    }

    public void setState_message(String state_message) {
        this.state_message = state_message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
