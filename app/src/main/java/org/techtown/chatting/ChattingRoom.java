package org.techtown.chatting;

public class ChattingRoom {
    String roomName;

    public ChattingRoom(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}