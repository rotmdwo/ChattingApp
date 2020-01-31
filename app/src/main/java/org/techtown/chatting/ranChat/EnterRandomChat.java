package org.techtown.chatting.ranChat;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.techtown.chatting.R;

import java.util.Map;

public class EnterRandomChat extends AppCompatActivity {
    String userId;
    Boolean isMatchedBoolean = false;

    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_loading);

        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        userId = pref.getString("id", "");

        reference.addListenerForSingleValueEvent(isMatched);
    }

    //매칭됐는지 확인
    ValueEventListener isMatched = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for(DataSnapshot dataSnapshot1 : dataSnapshot.child("randomRoom").getChildren()){
                Map<String, Object> ranroomData = (Map<String, Object>)dataSnapshot1.getValue();
                if(ranroomData.size() == 4 || ranroomData.size() == 5){
                    if(userId.equals(ranroomData.get("id1")) || userId.equals(ranroomData.get("id2"))){
                        isMatchedBoolean = true;
                        break;
                    }
                } else{
                    if(userId.equals(ranroomData.get("id1"))){
                        isMatchedBoolean = true;
                        break;
                    }
                }
            }
            if(isMatchedBoolean){
                Intent intent = new Intent(getApplicationContext(), RandomChattingRoomActivity.class);
                startActivity(intent);
                finish();
            } else{
                Intent intent = new Intent(getApplicationContext(), RandomChatActivity.class);
                startActivity(intent);
                finish();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
}
