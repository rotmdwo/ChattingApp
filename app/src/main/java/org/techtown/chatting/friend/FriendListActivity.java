package org.techtown.chatting.friend;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.techtown.chatting.chat.ChatListActivity;
import org.techtown.chatting.ConfigActivity;
import org.techtown.chatting.R;
import org.techtown.chatting.ranChat.RandomChatActivity;

import java.util.Map;

public class FriendListActivity extends AppCompatActivity {
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    ImageView person, chatRoom, randomChat, setting;
    FriendAdapter adapter = new FriendAdapter();
    RecyclerView recyclerView;
    TextView textView_name, textView_statement_message; //유저의 이름과 상태메세지 텍스트뷰
    String name, statement_message; //유저의 이름과 상태메세지 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

        textView_name=findViewById(R.id.textView);
        textView_statement_message=findViewById(R.id.textView2);

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
                        /*intent = new Intent(getApplicationContext(),FriendListActivity.class);
                        startActivity(intent);
                        finish();*/
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

    ValueEventListener dataListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for(DataSnapshot dataSnapshot2 : dataSnapshot.child("user").getChildren()){
                Map<String, Object> message2 = (Map<String, Object>) dataSnapshot2.getValue();
                if(restoreState().equals(message2.get("userId"))){
                    name = (String) message2.get("name");
                    try{
                        statement_message = (String) message2.get("statement_message");
                        textView_statement_message.setText(statement_message);
                    }catch(SQLException e){
                        Log.d("asd","에러");
                    }
                    textView_name.setText(name);

                }
            }

            for(DataSnapshot dataSnapshot1 : dataSnapshot.child("Friend").getChildren()){
                if(dataSnapshot1.getKey().equals(restoreState())){
                    Map<String, Object> message = (Map<String, Object>) dataSnapshot1.getValue();
                    int num = Integer.parseInt(message.get("num").toString());  //firebase에서 int 가져오는 방법

                    for(int i=1;i<=num;i++){
                        adapter.addItem(new Friend((String)message.get(Integer.toString(i)),"상태메세지"));
                    }
                }
            }

            recyclerView.setAdapter(adapter);
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
