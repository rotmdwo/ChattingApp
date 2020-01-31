package org.techtown.chatting.ranChat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.techtown.chatting.R;
import org.techtown.chatting.chat.ChatListActivity;
import org.techtown.chatting.chat.message;
import org.techtown.chatting.friend.FriendListActivity;
import org.techtown.chatting.setting.ConfigActivity;

import java.util.HashMap;
import java.util.Map;

public class RandomChattingRoomActivity extends AppCompatActivity {
    ImageButton backBtn, sendBtn;
    EditText editText;
    RecyclerView recyclerView;
    randomMessageAdapter adapter = new randomMessageAdapter();
    TextView chatName;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference roomReference;
    int num_message = 0;
    long time;
    String userId;
    String roomUserId;
    String otherId;
    String otherRId;
    String roomName;
    long roomNum;

    Boolean noOther = false;

    ImageView person, chatRoom, randomChat, setting; // 하단바 관련 변수

    String TAG = "RandomChattingRoomActivityTAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_chatting_room);

        addClickListener();

        //user ID
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        userId = pref.getString("id","");
        //set chatting room name
        chatName = findViewById(R.id.chatName);
        editText = findViewById(R.id.sendText);

        //find room number
        reference.addListenerForSingleValueEvent(findRoom);

        recyclerView = findViewById(R.id.recyclerView_random);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        backBtn = findViewById(R.id.backBtn);
        sendBtn = findViewById(R.id.sendBtn);

        //set sending message

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(noOther){
                    Toast.makeText(getApplicationContext(), "메시지를 보내실 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
                else{
                    String message = editText.getText().toString();
                    if(message == "") return;
                    editText.setText("");
                    Map<String, Object> childUpdates = new HashMap<>();
                    Map<String, Object> postValues = new HashMap<>();
                    postValues.put("message",message);
                    postValues.put("id", userId);
                    childUpdates.put("randomRoom/"+roomNum + "/text/"+(num_message+1), postValues);
                    reference.updateChildren(childUpdates);
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RandomChattingRoomActivity.this);
                builder.setTitle("채팅방을 나가시겠습니까?");
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(noOther){
                            reference.child("randomRoom").child(roomName).removeValue();
                            Intent intent = new Intent(getApplicationContext(), RandomChatActivity.class);
                            startActivity(intent);
                            roomReference.removeEventListener(eventListener);
                            finish();
                            return;
                        }
                        if(roomUserId.equals("id1")){
                            reference.child("randomRoom").child(roomName).child("id1").setValue(otherRId);
                            reference.child("randomRoom").child(roomName).child("randId1").setValue(otherId);
                            reference.child("randomRoom").child(roomName).child("id2").removeValue();
                            reference.child("randomRoom").child(roomName).child("randId2").removeValue();

                            Intent intent = new Intent(getApplicationContext(), RandomChatActivity.class);
                            startActivity(intent);
                            roomReference.removeEventListener(eventListener);
                            finish();
                            return;
                        }
                        else{
                            reference.child("randomRoom").child(roomName).child("id2").removeValue();
                            reference.child("randomRoom").child(roomName).child("randId2").removeValue();
                            Intent intent = new Intent(getApplicationContext(), RandomChatActivity.class);
                            startActivity(intent);
                            roomReference.removeEventListener(eventListener);
                            finish();
                            return;
                        }
                    }
                });
                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

    }

    ValueEventListener eventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Map<String, Object> list = (Map<String, Object>)dataSnapshot.getValue();
            if(list.size() <= 3){
                noOther = true;
                Toast.makeText(getApplicationContext(), "상대방이 나갔습니다.", Toast.LENGTH_SHORT).show();
                chatName.setText("상대방이 나갔습니다.");
                editText.setHint("메시지를 보낼 수 없는 채팅방입니다.");
                editText.setEnabled(false);
                return;
            }
            for(DataSnapshot dataSnapshot1 : dataSnapshot.child("text").getChildren()){
                if(Long.parseLong(dataSnapshot1.getKey()) == num_message+1){
                    Map<String, Object> message = (Map<String, Object>) dataSnapshot1.getValue();
                    num_message ++;
                    adapter.addItem(new randomMessage((String)message.get("id"), (String)message.get("message")), getApplicationContext());
                    adapter.notifyDataSetChanged();
                }
                recyclerView.scrollToPosition(adapter.getItemCount()-1);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    // find room number
    ValueEventListener findRoom = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for(DataSnapshot dataSnapshot1 : dataSnapshot.child("randomRoom").getChildren()){
                Map<String, Object> roomData = (Map<String, Object>) dataSnapshot1.getValue();
                Log.d(TAG, Integer.toString(roomData.size()));
                if(roomData.size() == 4 || roomData.size() == 5){
                    if(userId.equals((String)roomData.get("id1"))){
                        roomNum = Long.parseLong(dataSnapshot1.getKey());
                        roomUserId = "id1";
                        otherRId = (String)roomData.get("id2");
                        otherId = (String)roomData.get("randId2");
                        break;
                    }
                    else if(userId.equals((String)roomData.get("id2"))){
                        roomNum = Long.parseLong(dataSnapshot1.getKey());
                        roomUserId = "id2";
                        otherRId = (String)roomData.get("id1");
                        otherId = (String)roomData.get("randId1");
                        break;
                    }
                }
                else{
                    if(userId.equals((String)roomData.get("id1"))){
                        roomNum = Long.parseLong(dataSnapshot1.getKey());
                        roomUserId = "id1";
                        noOther = true;
                        break;
                    }
                }

            }
            roomName = Long.toString(roomNum);
            roomReference = FirebaseDatabase.getInstance().getReference().child("randomRoom").child(roomName);

            for(DataSnapshot dataSnapshot2 : dataSnapshot.child("randomRoom").child(Long.toString(roomNum)).child("text").getChildren()){
                num_message ++;
            }
            roomReference.addListenerForSingleValueEvent(chatList);
            roomReference.addValueEventListener(eventListener);

            if(noOther){
                chatName.setText("상대방이 나갔습니다.");
                editText.setHint("메시지를 보낼 수 없는 채팅방입니다.");
                editText.setEnabled(false);
            } else{
                chatName.setText(otherId + "와의 채팅방");
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    };

    // add list
    ValueEventListener chatList = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            num_message = 0;
            recyclerView.setAdapter(adapter);
            for(DataSnapshot dataSnapshot1 : dataSnapshot.child("text").getChildren()){
                num_message ++;
                Map<String, Object> messages = (Map<String, Object>)dataSnapshot1.getValue();

                adapter.addItem(new randomMessage((String) messages.get("id"), (String)messages.get("message")), getApplicationContext());
            }
            recyclerView.scrollToPosition(adapter.getItemCount()-1);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private void addClickListener(){
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
                        roomReference.removeEventListener(eventListener);
                        intent = new Intent(getApplicationContext(), FriendListActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.chatRoom:
                        roomReference.removeEventListener(eventListener);
                        intent = new Intent(getApplicationContext(), ChatListActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.randomChat:
                        /*intent = new Intent(getApplicationContext(), RandomChatActivity.class);
                        startActivity(intent);
                        finish();*/
                        break;
                    case R.id.setting:
                        roomReference.removeEventListener(eventListener);
                        intent = new Intent(getApplicationContext(), ConfigActivity.class);
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
