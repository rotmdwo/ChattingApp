package org.techtown.chatting;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.util.HashMap;
import java.util.Map;

public class ChatRoom extends AppCompatActivity {
    ImageButton imageButton,imageButton2;
    EditText editText;
    RecyclerView recyclerView;
    messageAdapter adapter = new messageAdapter();
    Intent intent;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Room");
    private DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference();
    int room_no,num_of_messages;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        textView = findViewById(R.id.textView);

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

        //intent = getIntent();
        //room_no = intent.getIntExtra("room_no",1);
        room_no = 1;

        imageButton = findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        editText = findViewById(R.id.editText);
        imageButton2 = findViewById(R.id.imageButton2);
        imageButton2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String message = editText.getText().toString();
                editText.setText("");
                // 보내는 거 구현
                Map<String, Object> childUpdates1 = new HashMap<>();
                Map<String, Object> childUpdates2 = new HashMap<>();
                Map<String, Object> postValues = new HashMap<>();
                num_of_messages++;
                childUpdates1.put("Room/rooms/"+room_no+"/num_of_messages",num_of_messages);
                postValues.put("message",message);
                postValues.put("sender",restoreState());
                childUpdates2.put("Room/rooms/"+room_no+"/"+num_of_messages,postValues);
                reference2.updateChildren(childUpdates1);
                reference2.updateChildren(childUpdates2);
                reference.addListenerForSingleValueEvent(dataListener2);
            }
        });
        reference.addListenerForSingleValueEvent(dataListener);
    }

    final ValueEventListener dataListener = new ValueEventListener() {

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for (DataSnapshot dataSnapshot1 : dataSnapshot.child("rooms").getChildren()) {
                if (dataSnapshot1.getKey().equals(Integer.toString(room_no))) {
                    Map<String, Object> message = (Map<String, Object>) dataSnapshot1.getValue();
                    textView.setText((String) message.get("name"));
                    num_of_messages = Integer.parseInt(message.get("num_of_messages").toString());
                    for (int i = 1; i <= num_of_messages; i++) {
                        Map<String, Object> message1 = (Map<String, Object>) message.get(Integer.toString(i));
                        adapter.addItem(new message((String) message1.get("sender"), (String) message1.get("message")), getApplicationContext());

                    }
                    recyclerView.setAdapter(adapter);
                }
            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    ValueEventListener dataListener2 = new ValueEventListener() {

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for(DataSnapshot dataSnapshot1 : dataSnapshot.child("rooms").getChildren()){
                if(dataSnapshot1.getKey().equals(Integer.toString(room_no))){
                    Map<String, Object> message = (Map<String, Object>) dataSnapshot1.getValue();
                    textView.setText((String)message.get("name"));
                    num_of_messages = Integer.parseInt(message.get("num_of_messages").toString());
                    Map<String, Object> message1 = (Map<String, Object>) message.get(Integer.toString(num_of_messages));
                    adapter.addItem(new message((String)message1.get("sender"),(String)message1.get("message")),getApplicationContext());
                    recyclerView.setAdapter(adapter);
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
}
