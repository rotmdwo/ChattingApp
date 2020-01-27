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

import com.google.firebase.database.ChildEventListener;
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
import java.util.HashMap;
import java.util.Map;

public class ChatListActivity extends AppCompatActivity {
    ArrayList<String> list = new ArrayList<>();
    ArrayList<String> chatNameList = new ArrayList<>();
    ArrayList<String> addRoomUserList = new ArrayList<>(); //새로 추가한 방의 멤버들 ID
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("Room");
    private DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference().child("user");
    private DatabaseReference reference4;
    RecyclerView chatRoomList; //채팅방 목록을 표시하는 리사이클러뷰
    RecyclerView recyclerView;
    ImageView person, chatRoom, randomChat, setting; //하단바 이미지뷰
    ImageView addChatRoom; //채팅방 새로 만드는 상단 버튼
    ChatRoomAdapter adapter = new ChatRoomAdapter(); //리사이클러뷰에 사용하는 어댑터
    String chatRoomNumList; //채팅방 이름
    int roomNum, member;
    String roomName;
    public static final int REQUEST_CODE = 001;
    int myNum;

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
        reference4 = FirebaseDatabase.getInstance().getReference().child("user").child(restoreState("user_num")).child("room");
        reference4.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //Log.d("test", "Added 실행");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("test", "changed 실행");
                reference.addListenerForSingleValueEvent(dataListener);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            //Intent intentFromAdd = getIntent();
            Bundle bundle = data.getExtras();
            member = bundle.getInt("memberNum");

            for(int i=0; i<member; i++) {
                addRoomUserList.add(bundle.getString("" + i));
            }
            addRoomUserList.add(restoreState("id"));
            //몇명인지
            //bundle.getInt("memberNum");

            //일단 채팅방 이름은 android로 하자
            //roomName = bundle.getString("title");
            roomName = "android";

            //데이터베이스에 업데이트
            reference2.addListenerForSingleValueEvent(dataListener2);
            reference3.addListenerForSingleValueEvent(dataListener3);
        }
    }


    //현재 들어가있는 톡방을 가져온다.
    //일단 이 사람이 누구인지 알아야 하고 그 사람이 들어간 톡방 번호를 받아온다.
    //그 톡방의 name 값을 받아와서 textView로 출력한다.

    final ValueEventListener dataListener = new ValueEventListener() {
        // 리사이클러뷰에 표시할 데이터 리스트 생성.
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Map<String, Object> message = (Map<String, Object>)dataSnapshot.child("user").child("2").child("room").getValue();
            String tmp = "";
            for ( String key : message.keySet() ) {
                if(key.equals("size")) {
                    //size일때
                } else {
                    tmp = message.get(key).toString();
                    Log.d("test", tmp);
                    adapter.addItem(tmp);
                    //방 번호일때


                }

                //key, map.get(key)
            }


            //ArrayList 변수인 list에 방 번호를 String으로 받아옴
            //String tmp = dataSnapshot2.getValue().toString();

            // 리사이클러뷰에 ChatRoomAdapter 객체 지정.
            //adapter = new ChatRoomAdapter(chatNameList);
            recyclerView.setAdapter(adapter);

            //리사이클러뷰의 채팅방을 클릭했을 경우,
            //list.get(position) 을 통해서 해당 채팅방의 아이디를 반환받을 수 있음
            adapter.setOnItemClickListener(new ChatRoomAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    //intent로 ChatRoomActivity를 호출
                    Intent chatIntent = new Intent(getApplicationContext(), ChatRoomActivity.class);
                    //방번호를 담음
                    Log.d("test", list.get(position));
                    chatIntent.putExtra("room_no", list.get(position));
                    //intent 전달하면서 액티비티 시작함
                    startActivity(chatIntent);
                }
            });
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    final ValueEventListener dataListener2 = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            //Log.d("test", "호출 되는지 테스트");

            Map<String, Object> message = (Map<String, Object>) dataSnapshot.getValue();

            //방 개수 받아와서 추가
            roomNum = Integer.parseInt(message.get("num_of_rooms").toString());
            roomNum ++;
            Map<String, Object> roomUpdater1 = new HashMap<>();

            //방 개수 DB에 업로드
            roomUpdater1.put("Room/num_of_rooms", roomNum);

            //방 정보 업데이트: 방 이름, 메세지 수
            //멤버 수는 아직 보류
            Map<String, Object> roomValues = new HashMap<>();
            roomValues.put("name", roomName);
            roomValues.put("num_of_messages", 0);
            //roomValues.put("num_of_members", member);

            //방 추가 및 DB에 업로드
            Map<String, Object> roomUpdater2 = new HashMap<>();
            roomUpdater2.put("Room/rooms/"+roomNum, roomValues);

            reference.updateChildren(roomUpdater1);
            reference.updateChildren(roomUpdater2);
            Log.d("debug", "정상적으로 방 생성했습니다.");


        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    final ValueEventListener dataListener3 = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            //사용자가 방 멤버로 추가한 모든 사람의 room 밑에 방 정보를 추가해야함

            for(DataSnapshot dataSnapshot4 : dataSnapshot.getChildren()) {

                Map<String, Object> userInfo = (Map<String, Object>) dataSnapshot4.getValue();
                //Log.d("test", ""+ dataSnapshot4.getKey());
                //Log.d("test", ""+ userInfo.toString());
                //만약, 현재 userInfo가 사용자가 추가한 상대의 아이디와 같다면
                    //방에 번호를 추가한다.

                for(String string : addRoomUserList) {
                    if(userInfo.get("userId").equals(string)) {
                        int whoIs = Integer.parseInt(dataSnapshot4.getKey().toString());
                        for(DataSnapshot dataSnapshot5 : dataSnapshot4.getChildren()) {

                            if(dataSnapshot5.getKey().equals("room")) {
                                Map<String, Object> userRoomUpdater = new HashMap<>();
                                userRoomUpdater.put("user/" + whoIs + "/room/" +roomNum, roomNum);

                                Map<String, Object> getSize = (Map<String, Object>) dataSnapshot5.getValue();
                                int userRoomNum = Integer.parseInt(getSize.get("size").toString());
                                userRoomNum++;
                                userRoomUpdater.put("user/" + whoIs + "/room/size", userRoomNum);

                                reference.updateChildren(userRoomUpdater);
                            }

                            //Map<String, Object> userInfo2 = (Map<String, Object>) dataSnapshot5.getValue();
                            //Log.d("test", "" + userInfo2.toString());

                        }
                    }
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    //입력 인자에 따라 유저 id 혹은 유저 고유 번호를 반환함
    protected String restoreState(String choice){
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        String result;
        if(choice.equals("id")) {
            result = pref.getString("id","");
            return result;
        } else if(choice.equals("user_num")) {
            result = pref.getString("user_num", "");
            return result;
        } else {
            result = "";
            return result;
        }

    }
}
