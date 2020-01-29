package org.techtown.chatting.friend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.techtown.chatting.AddFriend.AddFriendActivity;
import org.techtown.chatting.AddFriend.ReceiveFriendRequestActivity;
import org.techtown.chatting.ranChat.EnterRandomChat;
import org.techtown.chatting.setting.ConfigActivity;
import org.techtown.chatting.R;
import org.techtown.chatting.chat.ChatListActivity;

import org.techtown.chatting.ranChat.EnterRandomChat;

import java.util.Map;

public class FriendListActivity extends AppCompatActivity {
    private long time;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("FriendRequest");
    ImageView person, chatRoom, randomChat, setting;
    public FriendAdapter adapter = new FriendAdapter();
    public RecyclerView recyclerView;
    TextView textView_name, textView_statement_message; //유저의 이름과 상태메세지 텍스트뷰
    String name, statement_message; //유저의 이름과 상태메세지 변수
    Boolean gotFriendRequest = false;
    ImageButton button2;
    public static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        mContext = this;

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

        textView_name=findViewById(R.id.textView);
        textView_statement_message=findViewById(R.id.textView2);

        reference.addListenerForSingleValueEvent(dataListener);
        reference2.addListenerForSingleValueEvent(dataListener2);

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

        person.setOnClickListener(clickListener);
        chatRoom.setOnClickListener(clickListener);
        randomChat.setOnClickListener(clickListener);
        setting.setOnClickListener(clickListener);

        ImageButton button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddFriendActivity.class);
                startActivity(intent);
            }
        });

        button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ReceiveFriendRequestActivity.class);
                startActivity(intent);
            }
        });
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

            for(DataSnapshot dataSnapshot1 : dataSnapshot.child("friend").getChildren()){
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

    ValueEventListener dataListener2 = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            Map<String, Object> message = (Map<String, Object>) dataSnapshot.getValue();
            int user_num = Integer.parseInt(message.get("num").toString());

            for(int i=1;i<=user_num;i++){
                Map<String, Object> message2 = (Map<String, Object>) message.get(Integer.toString(i));
                if(((String)message2.get("toWhom")).equals(restoreState())){
                    gotFriendRequest = true;
                    button2.setBackgroundResource(R.drawable.human_plus);
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
