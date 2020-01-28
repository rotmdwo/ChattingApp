package org.techtown.chatting.ranChat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.techtown.chatting.R;
import org.techtown.chatting.chat.message;

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
    String userId;
    String roomUserId;
    String otherId;
    long roomNum;

    String TAG = "RandomChattingRoomActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_chatting_room);

        //user ID
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        userId = pref.getString("id","");
        //set chatting room name
        chatName = findViewById(R.id.chatName);

        //find room number
        reference.addListenerForSingleValueEvent(findRoom);




        recyclerView = findViewById(R.id.recyclerView_random);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        backBtn = findViewById(R.id.backBtn);
        sendBtn = findViewById(R.id.sendBtn);

        //set sending message
        editText = findViewById(R.id.sendText);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });

    }

    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            roomReference.addListenerForSingleValueEvent(newChat);
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
    };

    // find room number
    ValueEventListener findRoom = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for(DataSnapshot dataSnapshot1 : dataSnapshot.child("randomRoom").getChildren()){
                Map<String, Object> roomData = (Map<String, Object>) dataSnapshot1.getValue();
                if(userId.equals((String)roomData.get("id1"))){
                    roomNum = Long.parseLong(dataSnapshot1.getKey());
                    roomUserId = "Id1";
                    otherId = (String)roomData.get("randId2");
                    break;
                }
                else if(userId.equals((String)roomData.get("id2"))){
                    roomNum = Long.parseLong(dataSnapshot1.getKey());
                    roomUserId = "Id2";
                    otherId = (String)roomData.get("randId1");
                    break;
                }
            }
            roomReference = FirebaseDatabase.getInstance().getReference().child("randomRoom").child(Long.toString(roomNum));

            for(DataSnapshot dataSnapshot2 : dataSnapshot.child("randomRoom").child(Long.toString(roomNum)).child("chat").getChildren()){
                num_message ++;
            }
            roomReference.addListenerForSingleValueEvent(chatList);
            roomReference.addChildEventListener(childEventListener);

            chatName.setText(otherId + "와의 채팅방");
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    // add new Chat
    ValueEventListener newChat = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for(DataSnapshot dataSnapshot1 : dataSnapshot.child("text").getChildren()){
                if(Long.parseLong(dataSnapshot1.getKey()) == num_message+1){
                    Map<String, Object> message = (Map<String, Object>) dataSnapshot1.getValue();
                    num_message ++;
                    Log.d(TAG, Integer.toString(num_message));
                    Log.d(TAG, "id: "+(String)message.get("id"));
                    Log.d(TAG,"message: "+ (String)message.get("message"));
                    adapter.addItem(new randomMessage((String)message.get("id"), (String)message.get("message")), getApplicationContext());
                    adapter.notifyDataSetChanged();
                }
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
            for(DataSnapshot dataSnapshot1 : dataSnapshot.child("text").getChildren()){
                num_message ++;
                Map<String, Object> messages = (Map<String, Object>)dataSnapshot1.getValue();

                adapter.addItem(new randomMessage((String) messages.get("id"), (String)messages.get("message")), getApplicationContext());
                recyclerView.setAdapter(adapter);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
}
