package org.techtown.chatting.chat.addChatRoom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.techtown.chatting.R;
import org.techtown.chatting.adapter.ChatRoomAdapter;
import org.techtown.chatting.chat.ChatRoomActivity;
import org.techtown.chatting.friend.Friend;
import org.techtown.chatting.friend.FriendAdapter;

import java.util.ArrayList;
import java.util.Map;

public class AddChatActivity extends AppCompatActivity {
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    ArrayList<Friend> list = new ArrayList<>();
    RecyclerView recyclerView;
    Button creatBtn, cancleBtn;
    FriendAdapter adapter = new FriendAdapter();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chat);

        recyclerView = findViewById(R.id.recyclerView);
        creatBtn = findViewById(R.id.button3);
        cancleBtn = findViewById(R.id.button4);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)) ;

        reference.addListenerForSingleValueEvent(dataListener);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId() == R.id.button3) {
                    Toast.makeText(getApplicationContext(), "채팅방을 만들었어요.", Toast.LENGTH_LONG).show();
                } else if(view.getId() == R.id.button4) {
                    Toast.makeText(getApplicationContext(), "채팅방 생성을 취소해요.", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        };

        creatBtn.setOnClickListener(clickListener);
        cancleBtn.setOnClickListener(clickListener);
    }



    ValueEventListener dataListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for(DataSnapshot dataSnapshot1 : dataSnapshot.child("friend").getChildren()){
                if(dataSnapshot1.getKey().equals(restoreState())){
                    Map<String, Object> message = (Map<String, Object>) dataSnapshot1.getValue();
                    int num = Integer.parseInt(message.get("num").toString());  //firebase에서 int 가져오는 방법
                    for(int i=1;i<=num;i++){
                        list.add(new Friend((String)message.get(Integer.toString(i)),"상태메세지"));
                        //adapter.addItem();
                    }
                }
            }
            adapter = new FriendAdapter();
            adapter.setItems(list);

            recyclerView.setAdapter(adapter);

            adapter.setOnItemClickListener(new FriendAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Toast.makeText(getApplicationContext(), position + "번째 친구를 눌렀어요", Toast.LENGTH_SHORT).show();
                    Log.d("asdfg", list.get(position).getName());
                    //list.get(position)
                }
            });
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
