package org.techtown.chatting.AddFriend;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.techtown.chatting.R;

import java.util.HashMap;
import java.util.Map;

public class AddFriendActivity extends AppCompatActivity {
    EditText editText;
    Button button, button2;
    ImageView imageView;
    TextView textView2,textView3,textView4,textView5,textView6;
    int user_num;
    String id;
    Boolean isFound = false;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("FriendRequest");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        ImageButton imageButton = findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){  //뒤로가기 버튼
                finish();
            }
        });

        editText = findViewById(R.id.editText);
        imageView = findViewById(R.id.imageView);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        textView4 = findViewById(R.id.textView4);
        textView5 = findViewById(R.id.textView5);
        textView6 = findViewById(R.id.textView6);

        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){  //찾기 버튼
                isFound = false;
                id = editText.getText().toString();
                if(id.equals(restoreState())){  //자기 자신의 아이디를 입력했을 때
                    imageView.setVisibility(View.INVISIBLE);
                    textView2.setVisibility(View.INVISIBLE);
                    textView5.setVisibility(View.INVISIBLE);
                    textView3.setVisibility(View.INVISIBLE);
                    textView6.setVisibility(View.INVISIBLE);
                    textView4.setVisibility(View.VISIBLE);
                } else if(!id.equals("")){  //타인의 아이디를 입력했을 때
                    reference.addListenerForSingleValueEvent(dataListener);
                } else{  //아무 것도 입력 안 했을 때
                    imageView.setVisibility(View.INVISIBLE);
                    textView2.setVisibility(View.INVISIBLE);
                    textView4.setVisibility(View.INVISIBLE);
                    textView5.setVisibility(View.INVISIBLE);
                    textView6.setVisibility(View.INVISIBLE);
                    textView3.setVisibility(View.VISIBLE);
                }
            }
        });

        button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){  //등록 버튼
                if(isFound == true){  //사람이 찾아진 상태에서 등록버튼을 누르면
                    reference2.addListenerForSingleValueEvent(dataListener2);
                } else{  //사람 찾기를 하지 않고 등록버튼을 누르면
                    imageView.setVisibility(View.INVISIBLE);
                    textView2.setVisibility(View.INVISIBLE);
                    textView4.setVisibility(View.INVISIBLE);
                    textView5.setVisibility(View.INVISIBLE);
                    textView6.setVisibility(View.INVISIBLE);
                    textView3.setVisibility(View.VISIBLE);
                }
            }
        });


    }

    final ValueEventListener dataListener = new ValueEventListener() {

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for (DataSnapshot dataSnapshot1 : dataSnapshot.child("user_num").getChildren()) {
                user_num = Integer.parseInt(dataSnapshot1.getValue().toString());
            }

            for (DataSnapshot dataSnapshot1 : dataSnapshot.child("user").getChildren()) {
                Map<String, Object> message = (Map<String, Object>) dataSnapshot1.getValue();
                if (((String)message.get("userId")).equals(id)) {  //해당하는 ID의 유저를 찾았으면
                    textView3.setVisibility(View.INVISIBLE);
                    textView2.setVisibility(View.INVISIBLE);
                    textView4.setVisibility(View.INVISIBLE);
                    textView6.setVisibility(View.INVISIBLE);
                    textView5.setText((String)message.get("name"));
                    textView5.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                    isFound = true;
                }
            }

            if(isFound == false){
                textView3.setVisibility(View.INVISIBLE);
                textView4.setVisibility(View.INVISIBLE);
                textView5.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.INVISIBLE);
                textView6.setVisibility(View.INVISIBLE);
                textView2.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    final ValueEventListener dataListener2 = new ValueEventListener() {

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Boolean alreadyAdded = false;
            Map<String, Object> message = (Map<String, Object>) dataSnapshot.getValue();
            user_num = Integer.parseInt(message.get("num").toString());

            for(int i=1;i<=user_num;i++){
                Map<String, Object> message2 = (Map<String, Object>) message.get(Integer.toString(i));
                if((((String)message2.get("requester")).equals(restoreState()) && ((String)message2.get("toWhom")).equals(id)) || (((String)message2.get("requester")).equals(id) && ((String)message2.get("toWhom")).equals(restoreState()))){
                    alreadyAdded = true;
                }
            }

            if(alreadyAdded == false){
                //프렌드 리퀘스트 구현
                Map<String, Object> childUpdates1 = new HashMap<>();
                Map<String, Object> childUpdates2 = new HashMap<>();
                Map<String, Object> postValues = new HashMap<>();
                user_num++;
                childUpdates1.put("FriendRequest/num",user_num);
                postValues.put("requester",restoreState());
                postValues.put("toWhom",id);
                childUpdates2.put("FriendRequest/"+user_num,postValues);
                reference.updateChildren(childUpdates1);
                reference.updateChildren(childUpdates2);
            } else{  //이미 프렌드 리퀘스트 요청했을 때
                textView2.setVisibility(View.INVISIBLE);
                textView4.setVisibility(View.INVISIBLE);
                textView3.setVisibility(View.INVISIBLE);
                textView5.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);
                textView6.setVisibility(View.VISIBLE);
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
