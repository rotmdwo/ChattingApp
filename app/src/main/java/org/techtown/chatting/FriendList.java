package org.techtown.chatting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class FriendList extends AppCompatActivity {
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    friendAdapter adapter = new friendAdapter();
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);


        reference.addListenerForSingleValueEvent(dataListener);



    }

    ValueEventListener dataListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for(DataSnapshot dataSnapshot1 : dataSnapshot.child("friend").getChildren()){
                Log.d("asd",dataSnapshot1.getValue().toString());
                Log.d("asd",dataSnapshot1.getKey());
                if(dataSnapshot1.getKey().equals(restoreState())){
                    Log.d("asd","1");
                    Map<String, Object> message = (Map<String, Object>) dataSnapshot1.getValue();
                    Log.d("asd","2");
                    Log.d("asd",message.toString());
                    int num = Integer.parseInt(message.get("num").toString());  //firebase에서 int 가져오는 방법
                    Log.d("asd","3");

                    for(int i=1;i<=num;i++){
                        adapter.addItem(new friend((String)message.get(Integer.toString(i)),"상태메세지"));
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
