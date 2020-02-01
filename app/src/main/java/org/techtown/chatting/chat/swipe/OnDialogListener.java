package org.techtown.chatting.chat.swipe;

import org.techtown.chatting.chat.ChattingRoom;

public interface OnDialogListener {
    void onFinish(int position, ChattingRoom chatRoom);
    void onDeleted(int position);
}
