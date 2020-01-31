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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.techtown.chatting.R;
import org.techtown.chatting.adapter.ChatRoomAdapter;
import org.techtown.chatting.chat.addChatRoom.AddChatActivity;
import org.techtown.chatting.chat.swipe.ItemTouchHelperCallback;
import org.techtown.chatting.friend.FriendListActivity;
import org.techtown.chatting.ranChat.RandomChatActivity;
import org.techtown.chatting.setting.ConfigActivity;

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
    public static final int DELETE_CODE = 005;

    //채팅방 리사이클러뷰 관련 변수
    RecyclerView recyclerView; //채팅방 목록을 표시하는 리사이클러뷰
    ChatRoomAdapter adapter; //리사이클러뷰에 사용하는 어댑터

    //하단바 관련 변수
    ImageView person, chatRoom, randomChat, setting; //하단바 이미지뷰

    //액티비티 관련 변수
    ImageView addChatRoom; //채팅방 새로 만드는 상단 버튼

    //채팅방 생성 관련 변수
    int roomNum, member; //유저가 추가한 채팅방 id와 인원수
    String roomName; //유저가 입력한 채팅방 이름

    private long time;

    int removedPosition; //삭제된 채팅방 위치
    String removedItemInDB; //삭제된 채팅방 DB 위치

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        adapter = new ChatRoomAdapter(this);

        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        recyclerView = findViewById(R.id.chatRoomList) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(this)) ;

        //ItemTouchHelper로 슬라이드 구현
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelperCallback(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        reference.addListenerForSingleValueEvent(dataListener);         //액티비티가 create될때 DB에서 처음으로 채팅방 목록을 받아옴

        addChatRoom = (ImageView)findViewById(R.id.addChatRoom);

        //하단바 관련 변수
        person = (ImageView)findViewById(R.id.person);
        chatRoom = (ImageView)findViewById(R.id.chatRoom);
        randomChat = (ImageView)findViewById(R.id.randomChat);
        setting = (ImageView)findViewById(R.id.setting);

        //하단바 클릭 이벤트 리스너
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

        // 채팅방 추가 버튼 클릭 이벤트 리스너
        View.OnClickListener clickListener2 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //추후에 채팅방 이름 입력까지 추가할 예정.
                Intent intent = new Intent(getApplicationContext(), AddChatActivity.class);     //AddChatActivity에서 채팅방에 추가할 사용자의 친구를 선택
                startActivityForResult(intent, REQUEST_CODE);                                   //onActivityResult 메서드에서 받은 응답을 처리함
            }
        };

        //채팅방 추가 변수 리스너 설정
        addChatRoom.setOnClickListener(clickListener2);

        //하단바 변수들 리스너 설정
        person.setOnClickListener(clickListener);
        chatRoom.setOnClickListener(clickListener);
        randomChat.setOnClickListener(clickListener);
        setting.setOnClickListener(clickListener);

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

    //채팅방 슬라이드 이벤트 리스너
    //추후에 채팅방 순서 변경도 구현 예정
    /*
    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            removedPosition = viewHolder.getAdapterPosition();          // 삭제되는 아이템의 포지션을 가져온다
            Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
            startActivityForResult(intent, DELETE_CODE);                //요청코드 'DELETE_CODE'로 삭제 의사 요청
        }
    };
    */

    //AddChatActivity에서 넘어온 정보를 처리하는 함수
    //intent에 bundle로 보낸 정보를 받음
    //NotificationActivity에서 넘어온 정보도 처리함
    //intent에 "delete"로 담긴 정수가 1이면 삭제, 0이면 삭제하지 않음, -1은 기본값.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

            Bundle bundle = data.getExtras();                           //추가할 채팅방의 정보는 bundle에 들어있음: 채팅방 이름, 이용자 수, 이용자 목록
            member = bundle.getInt("memberNum");                   //member 변수는 채팅방에 들어간 사람의 수

            for(int i=0; i<member; i++) {
                addRoomUserList.add(bundle.getString("" + i));     //addRoomUserList에 채팅방에 들어간 사람들의 id를 넣는다
            }

            addRoomUserList.add(restoreState("id"));            //addRoomUserList에 본인 id도 추가

            //roomName = bundle.getString("title");                     //유저가 입력한 채팅방 이름을 받아옴
            roomName = "android";                                       //일단 채팅방 이름은 기본값 android로 고정함

            //받아온 정보를 데이터베이스에 업데이트
            reference2.addListenerForSingleValueEvent(dataListener2);   //dataListener2는 Room 테이블에 정보 추가
            reference3.addListenerForSingleValueEvent(dataListener3);   //dataListener3는 초대된 user 테이블에 정보 추가
        }

        if(requestCode == DELETE_CODE && resultCode == RESULT_OK) {
            int result = data.getIntExtra("delete", -1);
            if(result == 1) {
                Toast.makeText(getApplicationContext(),
                        "삭제 했어요", Toast.LENGTH_SHORT).show();                             //삭제했다고 사용자에게 알림
                removedItemInDB = adapter.getRoomIdByPosition(removedPosition);
                adapter.removeItem(removedPosition);                                                //어댑터에서 아이템 삭제
                adapter.notifyItemRemoved(removedPosition);                                         //어댑터에 삭제되었음을 알림 (굳이 해야되나?)
                reference.addListenerForSingleValueEvent(dataListener6);                            //삭제한 사람의 DB에서만 채팅방 목록 삭제
            } else {
                Toast.makeText(getApplicationContext(),
                        "취소 했어요", Toast.LENGTH_SHORT).show();                             //삭제안했다고 사용자에게 알림
                reference.addListenerForSingleValueEvent(dataListener4);
            }

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
                    String roomId = message.get(key).toString();
                    final Map<String, Object> message2 =
                            (Map<String, Object>)dataSnapshot
                                    .child("Room")
                                    .child("rooms")
                                    .child(roomId)
                                    .getValue();                                                    //DB에 접근해서 해당 name을 받아옴
                    String roomName = message2.get("name").toString();
                    adapter.addItem(new ChattingRoom(roomName, roomId));                            //adapter에 아이템 추가
                }
            }

            recyclerView.setAdapter(adapter);                                                       // 리사이클러뷰에 adapter 지정

            //리사이클러뷰의 채팅방을 클릭했을 경우 이벤트 리스너
            //ChatRoomAdapter에 구현된 getItem 메서드 활용
            adapter.setOnItemClickListener(new ChatRoomAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Intent chatIntent = new Intent(getApplicationContext(), ChatRoomActivity.class);
                    chatIntent.putExtra("room_no", adapter.getItem(position));               //방 번호를 intent로 전달하면서 액티비티 시작함
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


            roomNum = Integer.parseInt(message.get("num_of_rooms").toString());                     //방 개수 받아옴
            roomNum ++;
            roomUpdater1.put("Room/num_of_rooms", roomNum);                                      //방 개수 DB에 업로드

            Map<String, Object> roomValues = new HashMap<>();                                       //방 정보 업데이트: 방 이름, 메세지 수
            roomValues.put("name", roomName);
            roomValues.put("num_of_messages", 0);
            //roomValues.put("num_of_members", member);                                             //멤버수는 보류 (멤버수가 0이면 자동으로 폭파되게 하면 좋을듯)


            Map<String, Object> roomUpdater2 = new HashMap<>();
            roomUpdater2.put("Room/rooms/"+roomNum, roomValues);                                 //방 추가 및 DB에 업로드
            reference.updateChildren(roomUpdater1);
            reference.updateChildren(roomUpdater2);

            Toast.makeText(getApplicationContext(),
                    "새로운 채팅방이 만들어졌어요", Toast.LENGTH_SHORT).show();                //방 생성했다고 사용자에게 알리기

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
                    adapter.addItem(new ChattingRoom(roomName, message.get(key).toString())); //adapter에 아이템 추가
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


    //채팅방을 삭제할때 DB의 Room 테이블과 통신
    /*
    final ValueEventListener dataListener5 = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            //num_of_rooms을 하나 줄임
            //번호를 통째로 삭제함
            Map<String, Object> message = (Map<String, Object>) dataSnapshot.getValue();
            Map<String, Object> roomUpdater1 = new HashMap<>();

            //방 개수 받아옴
            roomNum = Integer.parseInt(message.get("num_of_rooms").toString());
            roomNum --;
            //방 개수 하나 줄여서 DB에 업로드
            roomUpdater1.put("Room/num_of_rooms", roomNum);


            //방 추가 및 DB에 업로드
            Map<String, Object> roomUpdater2 = new HashMap<>();
            roomUpdater2.put("Room/rooms/"+removedItemInDB, null);
            reference.updateChildren(roomUpdater1);
            reference.updateChildren(roomUpdater2);

            //디버깅용 로그 출력
            Log.d("debug", "정상적으로 방 삭제했습니다.");
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };*/

    //채팅방이 삭제된 사용자의 User 테이블과 통신
    //삭제한 사용자의 테이블에만 접근하면 됨
    final ValueEventListener dataListener6 = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            //user/whoIs/room/size
            Map<String, Object> userInfo2 = (Map<String, Object>) dataSnapshot.child("user").child(restoreState("user_num")).child("room").getValue();
            Map<String, Object> userRoomUpdater = new HashMap<>();
            int userRoomSize = Integer.parseInt(userInfo2.get("size").toString());
            userRoomSize--;

            String removedKey = "-1";
            for(Object key : userInfo2.keySet()) {
                if(userInfo2.get(key).toString().equals(removedItemInDB)) {
                    removedKey = key.toString();
                }
            }

            userRoomUpdater.put("user/" + restoreState("user_num") + "/room/size", userRoomSize);
            userRoomUpdater.put("user/" + restoreState("user_num") + "/room/" + removedKey, null);

            reference.updateChildren(userRoomUpdater);
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
