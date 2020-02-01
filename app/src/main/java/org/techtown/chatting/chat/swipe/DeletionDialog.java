package org.techtown.chatting.chat.swipe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import org.techtown.chatting.R;
import org.techtown.chatting.chat.ChattingRoom;

public class DeletionDialog extends Dialog {
    private OnDialogListener listener;
    Button exit, cancle;

    public DeletionDialog(Context context, final int position, ChattingRoom chattingRoom) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.activity_notification);

        exit = findViewById(R.id.exit);
        cancle = findViewById(R.id.cancle);

        exit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(listener != null) {
                    listener.onDeleted(position);

                    dismiss();
                }
            }
        });

        cancle.setOnClickListener(new View.OnClickListener() {

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
