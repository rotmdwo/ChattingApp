package org.techtown.chatting.AddFriend;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.techtown.chatting.R;

import java.util.Map;

public class ReceiveFriendRequestActivity extends AppCompatActivity {
    private DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("FriendRequest");
    RequesterAdapter adapter = new RequesterAdapter();
    RecyclerView recyclerView;
    public static Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_friend_request);
        mContext = this;

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        reference2.addListenerForSingleValueEvent(dataListener2);
    }

    ValueEventListener dataListener2 = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            Map<String, Object> message = (Map<String, Object>) dataSnapshot.getValue();
            int user_num = Integer.parseInt(message.get("num").toString());

            for(int i=1;i<=user_num;i++){
                Map<String, Object> message2 = (Map<String, Object>) message.get(Integer.toString(i));
                if(((String)message2.get("toWhom")).equals(restoreState())){
                    adapter.addItem(new Requester((String)message2.get("requester_id"),(String)message2.get("requester_name")),getApplicationContext());
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
