package org.techtown.chatting.chat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import org.techtown.chatting.ranChat.EnterRandomChat;
import org.techtown.chatting.setting.ConfigActivity;
import org.techtown.chatting.R;
import org.techtown.chatting.adapter.ChatRoomAdapter;
import org.techtown.chatting.chat.addChatRoom.AddChatActivity;
import org.techtown.chatting.friend.FriendListActivity;
import org.techtown.chatting.ranChat.RandomChatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatListActivity extends AppCompatActivity {
    ArrayList<String> addRoomUserList = new ArrayList<>(); //새로 추가한 방의 멤버들 ID

    //데이터베이스 관련 변수
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference(); //초기 채팅방 세팅 (onCreate)
    private DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("Room"); //채팅방 생성 Room 테이블
    private DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference().child("user"); //채팅방 생성 user 테이블
    private DatabaseReference reference4; //채팅방 변화 감지 세팅 (onChildChanged)

    //액티비티간 정보 전달 상수 (startActivityForResult)
    public static final int REQUEST_CODE = 001;

    //채팅방 리사이클러뷰 관련 변수
    RecyclerView recyclerView; //채팅방 목록을 표시하는 리사이클러뷰
    ChatRoomAdapter adapter = new ChatRoomAdapter(); //리사이클러뷰에 사용하는 어댑터

    //하단바 관련 변수
    ImageView person, chatRoom, randomChat, setting; //하단바 이미지뷰

    //액티비티 관련 변수
    ImageView addChatRoom; //채팅방 새로 만드는 상단 버튼

    //얘넨뭐지
    int roomNum, member;
    String roomName;

    private long time;

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
                        intent = new Intent(getApplicationContext(), EnterRandomChat.class);
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

        // 채팅방 추가 버튼 클릭 이벤트
        View.OnClickListener clickListener2 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //AddChatActivity는 채팅방에 추가할 사용자의 친구를 선택함
                //선택된 친구는 onActivityResult에서 응답받아서 처리함
                //추후에 채팅방 이름 입력까지 추가할 예정.
                Intent intent = new Intent(getApplicationContext(), AddChatActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        };

        person.setOnClickListener(clickListener);
        chatRoom.setOnClickListener(clickListener);
        randomChat.setOnClickListener(clickListener);
        setting.setOnClickListener(clickListener);

        addChatRoom.setOnClickListener(clickListener2);
        reference4 = FirebaseDatabase.getInstance().getReference().child("user").child(restoreState("user_num")).child("room");
        reference4.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                reference.addListenerForSingleValueEvent(dataListener4);
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

    //AddChatActivity에서 넘어온 정보를 처리하는 함수
    //intent에 bundle로 보낸 정보를 받음
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            //추가할 채팅방의 정보는 bundle에 들어있음: 채팅방 이름, 이용자 수, 이용자 목록
            //data.getExtras로 번들을 받아온 뒤에, getInt 혹은 getString 함수로 번들에 들어있는 정보를 받으면 됨
            Bundle bundle = data.getExtras();
            member = bundle.getInt("memberNum");

            //member 변수는 채팅방에 들어간 사람의 수
            //addRoomUserList에 채팅방에 들어간 사람들의 id를 넣는다
            for(int i=0; i<member; i++) {
                addRoomUserList.add(bundle.getString("" + i));
            }

            addRoomUserList.add(restoreState("id")); //본인 id도 넣음

            //유저가 입력한 채팅방 이름을 받아옴 but,
            //일단 채팅방 이름은 android로 하자
            //roomName = bundle.getString("title");
            roomName = "android";

            //받아온 정보를 데이터베이스에 업데이트
            //dataListener2는 Room 테이블에 정보 추가
            reference2.addListenerForSingleValueEvent(dataListener2);
            //dataListener3는 초대된 user 테이블에 정보 추가
            reference3.addListenerForSingleValueEvent(dataListener3);
        }
    }

    //리사이클러뷰에 표시할 데이터 리스트 생성하기 위해 DB와 통신
    //액티비티가 초기화될때 첫 1회만 실행됨
    final ValueEventListener dataListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            //사용자의 room 테이블에 접근함
            final Map<String, Object> message = (Map<String, Object>)dataSnapshot.child("user").child(restoreState("user_num")).child("room").getValue();

            //for문을 돌면서 현재 사용자가 들어간 채팅방을 탐색함
            for ( String key : message.keySet() ) {
                //key값이 size일때 예외처리
                if(key.equals("size")) {
                    continue;
                } else {
                    adapter.addItem(new ChattingRoom("android", message.get(key).toString())); //adapter에 아이템 추가
                }
            }



            // 리사이클러뷰에 adapter 지정
            recyclerView.setAdapter(adapter);

            //리사이클러뷰의 채팅방을 클릭했을 경우 이벤트 리스너
            //ChatRoomAdapter에 구현된 getItem 메서드 활용
            adapter.setOnItemClickListener(new ChatRoomAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Intent chatIntent = new Intent(getApplicationContext(), ChatRoomActivity.class);
                    //방번호를 담음
                    chatIntent.putExtra("room_no", adapter.getItem(position));
                    //방 번호를 intent로 전달하면서 액티비티 시작함
                    startActivity(chatIntent);
                }
            });
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    //채팅방을 추가할때 DB의 Room 테이블과 통신
    final ValueEventListener dataListener2 = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Map<String, Object> message = (Map<String, Object>) dataSnapshot.getValue();
            Map<String, Object> roomUpdater1 = new HashMap<>();

            //방 개수 받아옴
            roomNum = Integer.parseInt(message.get("num_of_rooms").toString());
            roomNum ++;
            //방 개수 DB에 업로드
            roomUpdater1.put("Room/num_of_rooms", roomNum);

            //방 정보 업데이트: 방 이름, 메세지 수
            //멤버 수는 보류
            Map<String, Object> roomValues = new HashMap<>();
            roomValues.put("name", roomName);
            roomValues.put("num_of_messages", 0);
            //roomValues.put("num_of_members", member);

            //방 추가 및 DB에 업로드
            Map<String, Object> roomUpdater2 = new HashMap<>();
            roomUpdater2.put("Room/rooms/"+roomNum, roomValues);
            reference.updateChildren(roomUpdater1);
            reference.updateChildren(roomUpdater2);

            //디버깅용 로그 출력
            //Log.d("debug", "정상적으로 방 생성했습니다.");
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    //채팅방이 추가된 사용자의 User 테이블과 통신
    //사용자가 방 멤버로 추가한 모든 사람의 room 밑에 방 정보를 추가해야함
    final ValueEventListener dataListener3 = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for(DataSnapshot dataSnapshot4 : dataSnapshot.getChildren()) {
                Map<String, Object> userInfo = (Map<String, Object>) dataSnapshot4.getValue();

                //addRoomUserList에 추가된 사용자의 id가 들어있음
                for(String string : addRoomUserList) {
                    if(userInfo.get("userId").equals(string)) {
                        //id로 해당 사용자가 DB에서 몇번인지 확인함: whoIs 변수에 저장
                        int whoIs = Integer.parseInt(dataSnapshot4.getKey().toString());
                        for(DataSnapshot dataSnapshot5 : dataSnapshot4.getChildren()) {
                            if(dataSnapshot5.getKey().equals("room")) {
                                Map<String, Object> getSize = (Map<String, Object>) dataSnapshot5.getValue();
                                Map<String, Object> userRoomUpdater = new HashMap<>();
                                int userRoomNum = Integer.parseInt(getSize.get("size").toString());
                                userRoomNum++;

                                userRoomUpdater.put("user/" + whoIs + "/room/" +userRoomNum, roomNum);
                                userRoomUpdater.put("user/" + whoIs + "/room/size", userRoomNum);

                                reference.updateChildren(userRoomUpdater);
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    //리사이클러뷰에 표시할 데이터 리스트 생성하기 위해 DB와 통신
    //dataListener1과 다르게 채팅방 리스트에 변화가 생길때마다 실행됨
    final ValueEventListener dataListener4 = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            final Map<String, Object> message = (Map<String, Object>)dataSnapshot.child("user").child(restoreState("user_num")).child("room").getValue();
            //현재는 방 번호만 받아와서 String이지만, 나중에 ChattingRoom 클래스를 adapter에 넣도록 변경해야함 (사진 구현할때 등등)
            //ChattingRoom tmp = new ChattingRoom();
            String size = "";

            //for문을 돌면서 현재 사용자가 들어간 채팅방을 탐색함
            for ( String key : message.keySet() ) {
                //key값이 size일때 예외처리
                if(key.equals("size")) {
                    size = (message.get(key).toString());
                    break;
                }
            }

            for ( String key : message.keySet() ) {
                if(key.equals(size)) {
                    adapter.addItem(new ChattingRoom("android", message.get(key).toString())); //adapter에 아이템 추가
                    break;
                }
            }
            // 리사이클러뷰에 adapter 지정
            recyclerView.setAdapter(adapter);

            //리사이클러뷰의 채팅방을 클릭했을 경우 이벤트 리스너
            //ChatRoomAdapter에 구현된 getItem 메서드 활용
            adapter.setOnItemClickListener(new ChatRoomAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Intent chatIntent = new Intent(getApplicationContext(), ChatRoomActivity.class);
                    //방번호를 담음
                    chatIntent.putExtra("room_no", adapter.getItem(position));
                    //방 번호를 intent로 전달하면서 액티비티 시작함
                    startActivity(chatIntent);
                }
            });
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

    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis()-time>=2000) {
            time = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), "\'뒤로\' 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
        else if(System.currentTimeMillis()-time < 2000){
            finish();
        }
    }
}
