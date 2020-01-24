package org.techtown.chatting;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.techtown.chatting.chat.ChatListActivity;
import org.techtown.chatting.friend.FriendListActivity;
import org.techtown.chatting.login.Login;
import org.techtown.chatting.ranChat.RandomChatActivity;

public class ConfigActivity extends AppCompatActivity {
    static final String[] LIST_MENU = {"공지사항", "프로필 관리", "계정 관리", "알림 설정", "친구목록 관리", "문의하기", "로그아웃"} ;
    ImageView person, chatRoom, randomChat, setting;

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        person = (ImageView)findViewById(R.id.person);
        chatRoom = (ImageView)findViewById(R.id.chatRoom);
        randomChat = (ImageView)findViewById(R.id.randomChat);
        setting = (ImageView)findViewById(R.id.setting);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                switch (view.getId()) {
                    case R.id.person:
                        intent = new Intent(getApplicationContext(), FriendListActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.chatRoom:
                        intent = new Intent(getApplicationContext(), ChatListActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.randomChat:
                        intent = new Intent(getApplicationContext(), RandomChatActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.setting:
                        /*intent = new Intent(getApplicationContext(),ConfigActivity.class);
                        startActivity(intent);
                        finish();*/
                        break;
                }

            }
        };

        person.setOnClickListener(clickListener);
        chatRoom.setOnClickListener(clickListener);
        randomChat.setOnClickListener(clickListener);
        setting.setOnClickListener(clickListener);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, LIST_MENU);
        listView = (ListView)findViewById(R.id.listview1);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String item = (String)adapterView.getItemAtPosition(i);

                if(item.equals("로그아웃")) {
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    clearState();
                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    Toast.makeText(getApplicationContext(), "로그아웃 되었어요.", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    finish();
                } else if(item.equals("프로필 관리")){
                    Intent intent = new Intent(getApplicationContext(),ProfileManagement.class);
                    startActivity(intent);
                } else if(item.equals("문의하기")) {
                    Intent email = new Intent(Intent.ACTION_SEND);
                    email.setType("plain/text");
                    // email setting 배열로 해놔서 복수 발송 가능
                    String[] address = {"bestowing02@gmail.com"};
                    email.putExtra(Intent.EXTRA_EMAIL, address);
                    email.putExtra(Intent.EXTRA_SUBJECT,"문의하기");
                    email.putExtra(Intent.EXTRA_TEXT,"문의 내용을 적어주세요.\n");
                    startActivity(email);
                }
            }
        });
    }

    protected void clearState() {
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }
}
