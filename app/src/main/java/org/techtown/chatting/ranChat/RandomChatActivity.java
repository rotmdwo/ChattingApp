package org.techtown.chatting.ranChat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.techtown.chatting.chat.ChatListActivity;
import org.techtown.chatting.setting.ConfigActivity;
import org.techtown.chatting.friend.FriendListActivity;
import org.techtown.chatting.R;

import java.util.HashMap;
import java.util.Map;

public class RandomChatActivity extends AppCompatActivity {
    CheckBox campus1;
    CheckBox campus2;
    CheckBox male1;
    CheckBox male2;
    Button startBtn;
    Boolean userCampus = true;
    Boolean userMale = true;
    long key = 1;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference key_reference = FirebaseDatabase.getInstance().getReference().child("key");
    int already_added_to_waitingList = 0; //자신이 waiting list에 2번 이상 올라가는 걸 방지

    UserOption userOption = null;
    Boolean isMatched = false;

    ImageView person, chatRoom, randomChat, setting; //하단바 관련 변수

    private long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_chat);

        campus1 = findViewById(R.id.campus1);
        campus2 = findViewById(R.id.campus2);
        male1 = findViewById(R.id.male1);
        male2 = findViewById(R.id.male2);
        startBtn = findViewById(R.id.start_btn);

        //하단바 변수 대입 및 클릭 리스너 설정
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

        startBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(!campus1.isChecked()&&!campus2.isChecked()){
                    Toast.makeText(getApplicationContext(), "옵션을 선택해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!male1.isChecked()&&!male2.isChecked()){
                    Toast.makeText(getApplicationContext(), "옵션을 선택해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                reference.addListenerForSingleValueEvent(dataListener);
                Log.d("asdasda","위에 있는 addListenerForSingleValueEven보다 여기가 먼저 실행 돼서" +
                        "처음에 시작하기를 누르면 무조건 매칭 실패하는 일이 발생해서 뒤 부분을 onChildChanged와 ValueEventListener 끝으로 옮김");
                /* 이 부분 삭제됨
                if(isMatched){
                    Toast.makeText(getApplicationContext(), "매칭 성공", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    Toast.makeText(getApplicationContext(), "매칭 실패, 대기열에 올립니다.", Toast.LENGTH_SHORT).show();
                    postFirebaseDatabase();
                    return;
                }
                 */
            }
        });

        reference.addChildEventListener(new ChildEventListener(){
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s){ //테이블이 추가될 때 실행

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot,String s){ //테이블 내에서 추가, 삭제, 수정 등이 일어날 때 실행
                reference.addListenerForSingleValueEvent(dataListener);  // 처음에 매칭되지 않고 기다리는 중 다른 사람이 waiting list에 들어올 때 매칭여부 재확인
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot){

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot,String s){

            }

            @Override
            public void onCancelled(DatabaseError databaseError){

            }
        });
    }

    private void waitingListener(DataSnapshot dataSnapshot){

    }

    ValueEventListener dataListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for(DataSnapshot dataSnapshot2 : dataSnapshot.child("key").getChildren()){
                Log.d("asdasda",dataSnapshot2.getValue().toString());
                key = (Long) dataSnapshot2.getValue();
            }

            for(DataSnapshot dataSnapshot1 : dataSnapshot.child("waiting").getChildren()){
                Log.d("asdasda", dataSnapshot1.getKey());
                Log.d("asdasda", dataSnapshot1.toString());
                try{
                    Map<String, Object> message = (Map<String, Object>)dataSnapshot1.getValue();
                    UserOption get = new UserOption((Boolean)message.get("campus1"),(Boolean)message.get("campus2"),(Boolean)message.get("male1"),
                            (Boolean)message.get("male2"),(Boolean)message.get("userCampus"),(Boolean)message.get("userMale"));
                    //UserOption get = dataSnapshot1.child("value").getValue(UserOption.class); 삭제됨
                    if(userCampus){  //내가 인사캠인데
                        if(!get.campus1){
                            continue;  //상대방이 인사캠 체크 안 함
                        }
                    }
                    else{  //내가 자과캠인데
                        if(!get.campus2)    {
                            continue;  //상대방이 자과캠 체크 안 함
                        }
                    }
                    if(userMale){  //내가 남잔데
                        if(!get.male1)   {
                            continue;  //상대방이 남자 체크 안 함
                        }
                    }
                    else{  //내가 여잔데
                        if(!get.male2)  {
                            continue;  //상대방이 여자 체크 안 함
                        }
                    }
                    if(get.userMale){  //상대방 남자인데
                        if(!male1.isChecked()) {
                            continue;  //내가 남자에 체크 안 함
                        }
                    }
                    else{  //상대방 여자인데
                        if(!male2.isChecked()) {
                            continue;  //내가 여자에 체크 안 함
                        }
                    }
                    if(get.userCampus){  // 상대방 인사캠인데
                        if(!campus1.isChecked())  {
                            continue;  //내가 인사캠에 체크 안 함
                        }
                    }
                    else{  // 상대방 자과캠인데
                        if(!campus2.isChecked())   {
                            continue;  //내가 자과캠에 체크 안 함
                        }
                    }
                    isMatched = true;
                    userOption = get;
                    break;
                } catch(DatabaseException e){
                    dataSnapshot1.getKey();
                    Log.d("asdasda", "데이터베이스 오류");
                }
                // campus1 == userCampus true --> 인사캠
                // campus2 == userCampus false --> 자과캠
                // male1 == userMale true --> 이성
                // male2 == userMale false --> 동성
            }
            if(isMatched){
                Toast.makeText(getApplicationContext(), "매칭 성공", Toast.LENGTH_SHORT).show();
                Log.d("asdasda",Long.toString(key));
                return;
            }
            else{
                Toast.makeText(getApplicationContext(), "매칭 실패, 대기열에 올립니다.", Toast.LENGTH_SHORT).show();
                Log.d("asdasda",Long.toString(key));
                postFirebaseDatabase();
                return;
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    public void postFirebaseDatabase(){
        if(already_added_to_waitingList == 0){
            Map<String, Object> childUpdates = new HashMap<>();
            Map<String, Object> key_childUpdates = new HashMap<>();
            Map<String, Object> postValues = null;
            UserOption userOption = new UserOption(campus1.isChecked(), campus2.isChecked(), male1.isChecked(), male2.isChecked(), userCampus, userMale);
            postValues = userOption.toMap();
            key++;
            childUpdates.put("waiting/"+Long.toString(key), postValues);
            key_childUpdates.put("key",key);
            reference.updateChildren(childUpdates);
            key_reference.updateChildren(key_childUpdates);
            already_added_to_waitingList = 1;
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
