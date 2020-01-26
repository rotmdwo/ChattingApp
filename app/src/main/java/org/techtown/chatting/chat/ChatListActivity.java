package org.techtown.chatting.chat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.techtown.chatting.ConfigActivity;
import org.techtown.chatting.R;
import org.techtown.chatting.adapter.ChatRoomAdapter;
import org.techtown.chatting.chat.addChatRoom.AddChatActivity;
import org.techtown.chatting.friend.Friend;
import org.techtown.chatting.friend.FriendListActivity;
import org.techtown.chatting.ranChat.RandomChatActivity;

import java.util.ArrayList;
import java.util.Map;

public class ChatListActivity extends AppCompatActivity {
    ArrayList<String> list = new ArrayList<>();
    ArrayList<String> chatNameList = new ArrayList<>();
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    RecyclerView chatRoomList; //채팅방 목록을 표시하는 리사이클러뷰
    RecyclerView recyclerView;
    ImageView person, chatRoom, randomChat, setting; //하단바 이미지뷰
    ImageView addChatRoom; //채팅방 새로 만드는 상단 버튼
    ChatRoomAdapter adapter; //리사이클러뷰에 사용하는 어댑터
    String chatRoomNumList; //채팅방 이름
    public static final int REQUEST_CODE = 001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        recyclerView = findViewById(R.id.chatRoomList) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(this)) ;

        addChatRoom = (ImageView)findViewById(R.id.addChatRoom);

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
                        intent = new Intent(getApplicationContext(), FriendListActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.chatRoom:
                        /*intent = new Intent(getApplicationContext(),ChatListActivity.class);
                        startActivity(intent);
                        finish();*/
                        break;
                    case R.id.randomChat:
                        intent = new Intent(getApplicationContext(), RandomChatActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.setting:
                        intent = new Intent(getApplicationContext(), ConfigActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                }

            }
        };



        // +버튼 눌렀을때 채팅방 만들기
        View.OnClickListener clickListener2 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //누구랑 채팅방을 만들건지 묻기

                //톡방 이름을 정하기

                //DB에 채팅방 업데이트
                    //num_of_rooms 숫자 1늘리기
                    //rooms밑에 테이블 만들기
                    //만든 테이블의 num_of_messages를 0으로 한다.
                    //만든 테이블의 name을 유저가 입력한 것으로 함

                Intent intent = new Intent(getApplicationContext(), AddChatActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        };


        // 채팅방 누르면 startActivity

        person.setOnClickListener(clickListener);
        chatRoom.setOnClickListener(clickListener);
        randomChat.setOnClickListener(clickListener);
        setting.setOnClickListener(clickListener);

        addChatRoom.setOnClickListener(clickListener2);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            //Intent intentFromAdd = getIntent();
            Bundle bundle = data.getExtras();
            int member = bundle.getInt("memberNum");

            for(int i=0; i<member; i++) {
                Log.d("asdfg", bundle.getString("" + i));
            }

            //몇명인지
            //bundle.getInt("memberNum");

            //일단 채팅방 이름은 android로 하자
            //bundle.getString("title");

        }
    }


    //현재 들어가있는 톡방을 가져온다.
    //일단 이 사람이 누구인지 알아야 하고 그 사람이 들어간 톡방 번호를 받아온다.
    //그 톡방의 name 값을 받아와서 textView로 출력한다.

    final ValueEventListener dataListener = new ValueEventListener() {
        // 리사이클러뷰에 표시할 데이터 리스트 생성.
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


            for (DataSnapshot dataSnapshot2 : dataSnapshot.child("user").getChildren()) {
                Map<String, Object> message2 = (Map<String, Object>) dataSnapshot2.getValue();
                //user의 자식 중에서 getSharedPreference 메서드를 사용, 사용자의 정보만 참조한다.
                if (restoreState().equals(message2.get("userId"))) {
                    for (DataSnapshot dataSnapshot3 : dataSnapshot2.child("room").getChildren()) {
                        //ArrayList 변수인 list에 방 번호를 String으로 받아옴
                        String tmp = dataSnapshot3.getValue().toString();
                        //list에 더한다
                        list.add(tmp);

                        for(DataSnapshot dataSnapshot1 : dataSnapshot.child("Room").getChildren()) {
                            if(dataSnapshot1.getKey().equals("rooms")) {
                                for(DataSnapshot dataSnapshot4 : dataSnapshot1.getChildren()) {

                                    if(dataSnapshot4.getKey().toString().equals(tmp)) {
                                        for(DataSnapshot dataSnapshot5 : dataSnapshot4.getChildren()) {
                                            if(dataSnapshot5.getKey().equals("name")) {
                                                chatNameList.add(dataSnapshot5.getValue().toString());
                                            }
                                        }
                                    }
                                }
                                //Map<String, Object> message = (Map<String, Object>) dataSnapshot1.getValue();
                                //Log.d("abcd", message.toString());
                            }
                        }
                    }
                    // 리사이클러뷰에 ChatRoomAdapter 객체 지정.
                    adapter = new ChatRoomAdapter(chatNameList);
                    recyclerView.setAdapter(adapter);

                    //리사이클러뷰의 채팅방을 클릭했을 경우,
                    //list.get(position) 을 통해서 해당 채팅방의 아이디를 반환받을 수 있음
                    adapter.setOnItemClickListener(new ChatRoomAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View v, int position) {
                            //intent로 ChatRoomActivity를 호출
                            Intent chatIntent = new Intent(getApplicationContext(), ChatRoomActivity.class);
                            //방번호를 담음
                            chatIntent.putExtra("room_no", list.get(position));
                            //intent 전달하면서 액티비티 시작함
                            startActivity(chatIntent);
                        }
                    });
                }
            }


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
