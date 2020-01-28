package org.techtown.chatting.ranChat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import java.util.Random;

public class RandomChatActivity extends AppCompatActivity {
    // 캠퍼스 선택 관련 변수
    long campusSeoul = 1;
    long campusSuwon = 2;
    long campusAll = 3;

    // 성별 선택 관련 변수
    long male = 1;
    long female = 2;
    long fm = 3;

    // 유저 정보
    Boolean userCampus = true;
    Boolean userMale = true;
    Boolean userWaiting = false;
    String userId = "";

    long optionCampus = 0;
    long optionMale = 0;

    CheckBox campus1, campus2, male1, male2;
    Button startBtn;

    long waitingNum = 0;
    long roomNum = 0;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    Boolean isMatched = false;

    ImageView person, chatRoom, randomChat, setting; // 하단바 관련 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_chat);

        campus1 = findViewById(R.id.campus1);
        campus2 = findViewById(R.id.campus2);
        male1 = findViewById(R.id.male1);
        male2 = findViewById(R.id.male2);
        startBtn = findViewById(R.id.start_btn);

        // 하단바 변수 대입 및 클릭 리스너 설정
        addClickListener();

        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        userId = pref.getString("id", "");

        reference.addListenerForSingleValueEvent(userType);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userWaiting){
                    Toast.makeText(getApplicationContext(), "대기중입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!campus1.isChecked() && !campus2.isChecked()){
                    Toast.makeText(getApplicationContext(), "옵션을 선택해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!male1.isChecked() && !male2.isChecked()){
                    Toast.makeText(getApplicationContext(), "옵션을 선택해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(campus1.isChecked()){
                    if(campus2.isChecked()) optionCampus = campusAll;
                    else optionCampus = campusSeoul;
                }
                else optionCampus = campusSuwon;
                if(male1.isChecked()){
                    if(male2.isChecked()) optionMale = fm;
                    else optionMale = male;
                }
                else optionMale = female;

                reference.addListenerForSingleValueEvent(matchingListener);
            }
        });
    }

    public void onWait(){
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = new HashMap<>();

        postValues.put("userId", userId);
        postValues.put("userCampus", userCampus);
        postValues.put("userMale", userMale);
        postValues.put("optionCampus", optionCampus);
        postValues.put("optionMale", optionMale);

        childUpdates.put("waiting/" + waitingNum, postValues);
        reference.updateChildren(childUpdates);
    }

    public void afterOnWait(){
        userWaiting = true;
        TextView op = (TextView)findViewById(R.id.ran_option);
        op.setVisibility(View.GONE);
        LinearLayout lin1 = (LinearLayout)findViewById(R.id.rand_option1);
        LinearLayout lin2 = (LinearLayout)findViewById(R.id.rand_option2);
        lin1.setVisibility(View.GONE);
        lin2.setVisibility(View.GONE);
        startBtn.setVisibility(View.GONE);
        ProgressBar pb = (ProgressBar)findViewById(R.id.progress_circular);
        TextView pbtext = (TextView)findViewById(R.id.progress_text);
        pbtext.setVisibility(View.VISIBLE);
        pb.setVisibility(View.VISIBLE);
    }

    public void ifMatched(String id1, String id2){
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = new HashMap<>();

        Random random = new Random();
        String randId1 = "user", randId2 = "user";
        for(int i=0;i<4;i++){
            randId1 = randId1 + Integer.toString(random.nextInt(10));
            randId2 = randId2 + Integer.toString(random.nextInt(10));
        }

        postValues.put("id1", id1);
        postValues.put("id2", id2);
        postValues.put("randId1", randId1);
        postValues.put("randId2", randId2);

        childUpdates.put("randomRoom/" + roomNum, postValues);
        reference.updateChildren(childUpdates);
    }

    //user 정보 확인 & 대기중인지 확인 & 매칭됐는지 확인
    ValueEventListener userType = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for(DataSnapshot dataSnapshot2 : dataSnapshot.child("waiting").getChildren()){
                waitingNum ++;
            }
            waitingNum ++;
            for(DataSnapshot dataSnapshot3 : dataSnapshot.child("randomRoom").getChildren()){
                roomNum ++;
            }
            roomNum++;
            for(DataSnapshot dataSnapshot1 : dataSnapshot.child("user").getChildren()){
                Map<String, Object> userData = (Map<String, Object>) dataSnapshot1.getValue();
                if(userId.equals(userData.get("userId"))){
                    userCampus = (Boolean)userData.get("userCampus");
                    userMale = (Boolean)userData.get("userMale");
                    break;
                }
            }
            for(DataSnapshot dataSnapshot1 : dataSnapshot.child("randomRoom").getChildren()){
                Map<String, Object> ranroomData = (Map<String, Object>)dataSnapshot1.getValue();
                if(userId.equals(ranroomData.get("id1")) || userId.equals(ranroomData.get("id2"))){
                    isMatched = true;
                    break;
                }
            }
            if(isMatched){
                Toast.makeText(getApplicationContext(), "매칭되었습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), RandomChattingRoomActivity.class);
                startActivity(intent);
                finish();
                return;
            }
            for(DataSnapshot dataSnapshot1 : dataSnapshot.child("waiting").getChildren()){
                Map<String, Object> waitingData = (Map<String, Object>) dataSnapshot1.getValue();
                if(userId.equals(waitingData.get("userId"))){
                    userWaiting = true;
                    break;
                }
            }

            if(userWaiting){
                TextView op = (TextView)findViewById(R.id.ran_option);
                op.setVisibility(View.GONE);
                LinearLayout lin1 = (LinearLayout)findViewById(R.id.rand_option1);
                LinearLayout lin2 = (LinearLayout)findViewById(R.id.rand_option2);
                lin1.setVisibility(View.GONE);
                lin2.setVisibility(View.GONE);
                startBtn.setVisibility(View.GONE);

                ProgressBar pb = (ProgressBar)findViewById(R.id.progress_circular);
                pb.setVisibility(View.VISIBLE);
                TextView pbtext = (TextView)findViewById(R.id.progress_text);
                pbtext.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    //waiting 위의 유저들 확인
    ValueEventListener matchingListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for(DataSnapshot dataSnapshot1 : dataSnapshot.child("waiting").getChildren()){
                Map<String, Object> waitingData = (Map<String, Object>) dataSnapshot1.getValue();
                long otherOptionCampus = (Long) waitingData.get("optionCampus");
                long otherOptionMale = (Long) waitingData.get("optionMale");

                // 1. 내 정보와 상대방이 원하는 옵션
                if(userMale){ // 내가 남자
                    if(otherOptionMale == female) continue;
                }
                else{ // 내가 여자
                    if(otherOptionMale == male) continue;
                }
                if(userCampus){ // 내가 인사
                    if(otherOptionCampus == campusSuwon) continue;
                }else{ // 내가 자과
                    if(otherOptionCampus == campusSeoul) continue;
                }
                Boolean otherCampus = (Boolean)waitingData.get("userCampus");
                Boolean otherMale = (Boolean)waitingData.get("userMale");

                // 2. 내가 원하는 옵션과 상대방의 정보
                if(otherMale){
                    if(optionMale == female) continue;
                }
                else{
                    if(optionMale == male) continue;
                }
                if(otherCampus){
                    if(optionCampus == campusSuwon) continue;
                }
                else{
                    if(optionCampus == campusSeoul) continue;
                }

                //매칭
                isMatched = true;
                reference.child("waiting").child(dataSnapshot1.getKey()).removeValue();
                ifMatched((String)waitingData.get("userId"), userId);
                Toast.makeText(getApplicationContext(), "매칭", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), RandomChattingRoomActivity.class);
                startActivity(intent);
                break;
            }
            if(!isMatched){
                onWait();
                afterOnWait();
            }
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
    }
}

