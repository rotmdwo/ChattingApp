package org.techtown.chatting;

public class friend {
    String name;
    String state_message;

    public friend(String name, String state_message) {
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
}
