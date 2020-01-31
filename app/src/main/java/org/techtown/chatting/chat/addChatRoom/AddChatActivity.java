package org.techtown.chatting.chat.addChatRoom;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import org.techtown.chatting.R;
import org.techtown.chatting.friend.Friend;
import org.techtown.chatting.friend.FriendAdapterForAddChatRoom;

import java.util.ArrayList;
import java.util.Map;

public class AddChatActivity extends AppCompatActivity {
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    ArrayList<Friend> list = new ArrayList<>();
    ArrayList<Friend> selectedList = new ArrayList<>();
    RecyclerView recyclerView;
    Button creatBtn, cancleBtn;
    FriendAdapterForAddChatRoom adapter = new FriendAdapterForAddChatRoom();

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
                    if(selectedList.size() == 0) {
                        Toast.makeText(getApplicationContext(), "최소한 한명을 선택해주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    int i = 0;
                    for(Friend f : selectedList) {
                        bundle.putString("" + i, f.getName());
                        i++;
                    }
                    bundle.putInt("memberNum", selectedList.size());
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);
                    finish();
                } else if(view.getId() == R.id.button4) {
                    Toast.makeText(getApplicationContext(), "채팅방 생성을 취소해요.", Toast.LENGTH_SHORT).show();
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
                        Map<String, Object> message1 = (Map<String, Object>) message.get(Integer.toString(i));
                        list.add(new Friend((String)message1.get("id"),(String)message1.get("name"),"상태메세지"));
                        //adapter.addItem();
                    }
                }
            }
            adapter = new FriendAdapterForAddChatRoom();
            adapter.setItems(list);

            recyclerView.setAdapter(adapter);

            adapter.setOnItemClickListener(new FriendAdapterForAddChatRoom.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    //클릭된 객체가 selectedList에 있는지 확인
                    //있으면 없애기
                    //없으면 추가하기

                    Friend selected = list.get(position);
                    boolean isAdd = true;
                    for(Friend f : selectedList) {
                        if(f.equals(selected)) {
                            isAdd = false;
                        }
                    }

                    if(isAdd) {
                        selectedList.add(selected);
                        //Toast.makeText(getApplicationContext(), selected.getName() + "를 추가 헀어요", Toast.LENGTH_SHORT).show();
                    } else {
                        selectedList.remove(selected);
                        //Toast.makeText(getApplicationContext(), selected.getName() + "를 삭제했어요", Toast.LENGTH_SHORT).show();
                    }
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
