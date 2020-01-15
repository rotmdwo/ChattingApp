package org.techtown.chatting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    EditText userId, userPw, userPw2, userName, userEmail;
    Button idCheckBtn, emailCheckBtn, submitBtn;
    RadioGroup rdGroup;
    RadioButton rdMale, rdFemale;
    String inputId;
    boolean isIdChecked = false;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        userId = (EditText)findViewById(R.id.userId);
        userPw = (EditText)findViewById(R.id.userPw);
        userPw2 = (EditText)findViewById(R.id.userPw2);
        userName = (EditText)findViewById(R.id.userName);
        userEmail = (EditText)findViewById(R.id.userEmail);
        idCheckBtn = (Button)findViewById(R.id.idCheckBtn);
        emailCheckBtn = (Button)findViewById(R.id.emailCheckBtn);
        submitBtn = (Button)findViewById(R.id.submitBtn);
        rdGroup = (RadioGroup)findViewById(R.id.rdGroup);
        rdMale = (RadioButton)findViewById(R.id.rdMale);
        rdFemale = (RadioButton)findViewById(R.id.rdFemale);

        idCheckBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputId = userId.getText().toString();
                //Toast.makeText(getApplicationContext(), "입력한 아이디는 " + inputId + "입니다.", Toast.LENGTH_LONG).show();
                reference.addListenerForSingleValueEvent(dataListener);
            }
        });

        //일단 idCheckBtn 리스너를 만들어 준다
        //아이디 값을 받아서 DB에 그 있나 없나 리턴함

        //emailCheckBtn 리스너를 만들어준다
        //이메일을 받아서 DB에 그 이메일이 있나 없나 리턴함

        //submitBtn 리스너를 만들어준다
        //뷰에서 값을 받아옴
        //빈 게 있으면 토스트 메세지 띠우고 포커스를 넣어줌
        //빈 게 없으면
        //DB에 정보 올리고 토스트메시지로 성공 표시하고 액티비티 닫음
    }

    ValueEventListener dataListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for(DataSnapshot dataSnapshot1 : dataSnapshot.child("user").getChildren()){
                //Log.d("asdasda",dataSnapshot1.getValue().toString());
                //key = (Long) dataSnapshot2.getValue();

                Log.d("asdasda",dataSnapshot1.getValue().toString());
                String id = (String)dataSnapshot1.get("userId");
                Toast.makeText(getApplicationContext(), "받아온 값은 " + id, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

}
