package org.techtown.chatting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class ChatListActivity extends AppCompatActivity {
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    RecyclerView chatRoomList; //채팅방 목록을 표시하는 리사이클러뷰
    ImageView person, chatRoom, randomChat, setting; //하단바 이미지뷰
    ImageView addChatRoom; //채팅방 새로 만드는 상단 버튼
    ChatRoomAdapter adapter = new ChatRoomAdapter(); //리사이클러뷰에 사용하는 어댑터
    String chatRoomNumList; //채팅방 이름

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        addChatRoom = (ImageView)findViewById(R.id.addChatRoom);
        chatRoomList = (RecyclerView)findViewById(R.id.chatRoomList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        chatRoomList.setLayoutManager(layoutManager);

        reference.addListenerForSingleValueEvent(dataListener);

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
                        intent = new Intent(getApplicationContext(),FriendList.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.chatRoom:
                        /*intent = new Intent(getApplicationContext(),ChatListActivity.class);
                        startActivity(intent);
                        finish();
                        break;*/
                    case R.id.randomChat:
                        intent = new Intent(getApplicationContext(),RandomChattingWaitingRoom.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.setting:
                        intent = new Intent(getApplicationContext(),ConfigActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                }

            }
        };

        person.setOnClickListener(clickListener);
        chatRoom.setOnClickListener(clickListener);
        randomChat.setOnClickListener(clickListener);
        setting.setOnClickListener(clickListener);
    }



    //intent로 방번호랑 방 이름을 보낸다.

    //현재 들어가있는 톡방을 가져온다.
    //일단 이 사람이 누구인지 알아야 하고 그 사람이 들어간 톡방 번호를 받아온다.
    //그 톡방의 name 값을 받아와서 textView로 출력한다.

    ValueEventListener dataListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            //user의 자식 중에서 getSharedPreference 메서드를 사용,
            //사용자가 누구인지 받아온다.
            for (DataSnapshot dataSnapshot2 : dataSnapshot.child("user").getChildren()) {
                Map<String, Object> message2 = (Map<String, Object>) dataSnapshot2.getValue();
                //만약 사용자의 id를 찾으면,
                if (restoreState().equals(message2.get("userId"))) {
                    //그 사람이 들어간 방 번호를 받아온다.
                    Log.d("a", "message2는 " + message2.toString());
                    for(DataSnapshot dataSnapshot3 : dataSnapshot2.child("room").getChildren()) {
                        Map<String, Object> message3 = (Map<String, Object>) dataSnapshot3.getValue();
                        Log.d("a", " " + message3.toString());
                    }

                }
            }
            /*
            for (DataSnapshot dataSnapshot1 : dataSnapshot.child("friend").getChildren()) {
                if (dataSnapshot1.getKey().equals(restoreState())) {
                    Map<String, Object> message = (Map<String, Object>) dataSnapshot1.getValue();
                    int num = Integer.parseInt(message.get("num").toString());  //firebase에서 int 가져오는 방법

                    for (int i = 1; i <= num; i++) {
                        //adapter.addItem(new friend((String) message.get(Integer.toString(i)), "상태메세지"));
                    }
                }
            }*/

            //recyclerView.setAdapter(adapter);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    protected String restoreState(){
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        return pref.getString("id","");
    }
}
