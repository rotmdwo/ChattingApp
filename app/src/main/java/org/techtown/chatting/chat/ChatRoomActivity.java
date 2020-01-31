package org.techtown.chatting.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.techtown.chatting.R;

import java.util.HashMap;
import java.util.Map;

public class ChatRoomActivity extends AppCompatActivity {
    ImageButton imageButton,imageButton2;
    EditText editText;
    RecyclerView recyclerView;
    messageAdapter adapter = new messageAdapter();
    Intent intent;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Room");
    private DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference reference3;
    int room_no,num_of_messages;
    TextView textView,textView2;
    private Parcelable recyclerViewState;  //자동 스크롤 방지
    Boolean sentByMe = false;
    long scrollLocation;
    public static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        mContext = this;

        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(recyclerView.computeVerticalScrollOffset() + recyclerView.computeVerticalScrollExtent() == recyclerView.computeVerticalScrollRange()){
                    textView2.setVisibility(View.INVISIBLE);
                }
            }
        });


        intent = getIntent();
        room_no = intent.getIntExtra("room_no",1);
        //Toast.makeText(getApplicationContext(), "넘겨받은 값은: " + room_no, Toast.LENGTH_SHORT).show();

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
                if(!message.equals("")){  // 보내는 거 구현
                    Map<String, Object> childUpdates1 = new HashMap<>();
                    Map<String, Object> childUpdates2 = new HashMap<>();
                    Map<String, Object> postValues = new HashMap<>();
                    num_of_messages++;
                    childUpdates1.put("Room/rooms/"+room_no+"/num_of_messages",num_of_messages);
                    postValues.put("message",message);
                    postValues.put("sender",restoreState());
                    childUpdates2.put("Room/rooms/"+room_no+"/"+num_of_messages,postValues);
                    sentByMe = true;
                    reference2.updateChildren(childUpdates1);
                    reference2.updateChildren(childUpdates2);
                }
            }
        });
        reference.addListenerForSingleValueEvent(dataListener);

        reference3 = FirebaseDatabase.getInstance().getReference().child("Room").child("rooms").child(Integer.toString(room_no));
        reference3.addChildEventListener(new ChildEventListener(){
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s){

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot,String s){
                if(sentByMe == true){  // 내가 메세지 보냄
                    sentByMe = false;
                    reference.addListenerForSingleValueEvent(dataListener2);
                } else{  // 상대가 메세지 보냄
                    reference.addListenerForSingleValueEvent(dataListener3);
                }
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

    final ValueEventListener dataListener = new ValueEventListener() {  //채팅창에 들어와 처음 메세지를 로딩할 때

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
                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);  //자동 스크롤
                }
            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    ValueEventListener dataListener2 = new ValueEventListener() {  //내가 메세지를 보냈을 때

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for(DataSnapshot dataSnapshot1 : dataSnapshot.child("rooms").getChildren()) {
                if (dataSnapshot1.getKey().equals(Integer.toString(room_no))) {
                    Map<String, Object> message = (Map<String, Object>) dataSnapshot1.getValue();
                    textView.setText((String) message.get("name"));
                    num_of_messages = Integer.parseInt(message.get("num_of_messages").toString());
                    Map<String, Object> message1 = (Map<String, Object>) message.get(Integer.toString(num_of_messages));
                    adapter.addItem(new message((String) message1.get("sender"), (String) message1.get("message")), getApplicationContext());
                    recyclerView.setAdapter(adapter);
                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);  //자동 스크롤
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    ValueEventListener dataListener3 = new ValueEventListener() {  //상대방이 메세지를 보냈을 때

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for(DataSnapshot dataSnapshot1 : dataSnapshot.child("rooms").getChildren()) {
                if (dataSnapshot1.getKey().equals(Integer.toString(room_no))) {
                    Map<String, Object> message = (Map<String, Object>) dataSnapshot1.getValue();
                    textView.setText((String) message.get("name"));
                    num_of_messages = Integer.parseInt(message.get("num_of_messages").toString());
                    Map<String, Object> message1 = (Map<String, Object>) message.get(Integer.toString(num_of_messages));
                    adapter.addItem(new message((String) message1.get("sender"), (String) message1.get("message")), getApplicationContext());

                    if(recyclerView.computeVerticalScrollOffset() + recyclerView.computeVerticalScrollExtent() != recyclerView.computeVerticalScrollRange()){
                        textView2.setText((String) message1.get("sender") + " : " + (String) message1.get("message"));
                        textView2.setVisibility(View.VISIBLE);  // 대화창 스크롤이 가장 밑에 있지 않을 때 상대가 메세지를 보내면 미리보기 창이 뜸
                    }

                    recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();  //자동 스크롤 방지
                    recyclerView.setAdapter(adapter);
                    recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);  //자동 스크롤 방지
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
