package org.techtown.chatting.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.techtown.chatting.friend.FriendListActivity;
import org.techtown.chatting.R;

import java.util.Map;

public class Login extends AppCompatActivity {
    TextView textView,textView2;
    EditText editText, editText2;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    String id,password,userNum;
    Boolean isLoginChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textView = findViewById(R.id.textView);
        textView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        editText = findViewById(R.id.editText);
        editText2 = findViewById(R.id.editText2);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = editText.getText().toString();
                password = editText2.getText().toString();
                if(id.equals("") || password.equals("")){
                    Toast.makeText(getApplicationContext(),"아이디와 비밀번호를 입력해주세요",Toast.LENGTH_LONG).show();
                }else{
                    reference.addListenerForSingleValueEvent(dataListener);
                }
            }
        });
    }

    ValueEventListener dataListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for(DataSnapshot dataSnapshot1 : dataSnapshot.child("user").getChildren()){
                Map<String, Object> message = (Map<String, Object>) dataSnapshot1.getValue();


                String temp_id = (String)message.get("userId");
                String temp_pw = (String)message.get("password");
                if(temp_id.equals(id) && temp_pw.equals(password)) {
                    //유저 고유 번호 확인
                    userNum = (dataSnapshot1.getKey().toString());

                    //아이디, 비밀번호 확인됨
                    isLoginChecked = true;
                    break;
                }
            }
            if(isLoginChecked){
                saveState(id, userNum);  //로그인 성공, 로그인 된 아이디 저장
                Intent intent = new Intent(getApplicationContext(), FriendListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else{
                Toast.makeText(getApplicationContext(),"아이디 또는 비밀번호가 틀렸습니다.",Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    public void saveState(String id, String user_num){
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("id",id);
        editor.putString("user_num", user_num);
        editor.commit();
    }
}
