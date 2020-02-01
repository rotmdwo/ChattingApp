package org.techtown.chatting.chat.swipe;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import org.techtown.chatting.R;
import org.techtown.chatting.chat.ChattingRoom;

public class ModificationDialog extends Dialog {
    private OnDialogListener listener;
    private Context context;
    private Button mod_bt, cancle_bt;
    private EditText mod_name;
    private String roomName, roomId;

    public ModificationDialog(Context context, final int position, ChattingRoom chattingRoom) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.custom_dialog);

        roomName = chattingRoom.getRoomName();
        roomId = chattingRoom.getRoomId();

        mod_name = findViewById(R.id.mod_name);
        mod_name.setText(roomName);
        mod_bt = findViewById(R.id.mod_bt);
        cancle_bt = findViewById(R.id.cancle_bt);

        mod_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null) {
                    String newName = mod_name.getText().toString();
                    ChattingRoom chatRoom = new ChattingRoom(newName, roomId);

                    listener.onFinish(position, chatRoom);

                    dismiss();
                }

            }
        });

        cancle_bt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(listener != null) {
                    dismiss();
                }
            }
        });
    }

    public void setDialogListener(OnDialogListener listener) {
        this.listener = listener;
    }
}
