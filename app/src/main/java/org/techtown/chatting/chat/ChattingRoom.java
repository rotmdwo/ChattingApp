package org.techtown.chatting.chat;

public class ChattingRoom {
    String roomName, roomId; //방 이름과 방 숫자

    public ChattingRoom(String roomName, String roomId) {
        this.roomName = roomName;
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getRoomId() { return roomId; }
}