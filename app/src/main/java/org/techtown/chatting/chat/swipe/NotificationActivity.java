package org.techtown.chatting.chat.swipe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.util.Log;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import org.techtown.chatting.R;

public class NotificationActivity extends AppCompatActivity {
    Button exit, cancle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(android.R.style.Theme_Material_Dialog_Alert);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);


        exit = (Button)findViewById(R.id.exit);
        cancle = (Button)findViewById(R.id.cancle);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent();
                if(view.getId() == R.id.exit) {
                    //삭제 함
                    intent2.putExtra("delete", 1);
                } else {
                    //삭제 안함
                    intent2.putExtra("delete", 0);
                }
                setResult(RESULT_OK, intent2);
                finish();
            }
        };

        exit.setOnClickListener(clickListener);
        cancle.setOnClickListener(clickListener);
    }
}
