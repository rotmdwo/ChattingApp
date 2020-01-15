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

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    EditText userId, userPw, userPw2, userName, userEmail;
    Button idCheckBtn, emailCheckBtn, submitBtn;
    RadioGroup groupGender, groupCampus;
    RadioButton rdMale, rdFemale, rdHuman, rdNature;
    String inputId, inputEmail;
    boolean isIdChecked = false;
    boolean isEmailChecked = false;
    long userNum;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference user_reference = FirebaseDatabase.getInstance().getReference().child("user");
    private DatabaseReference user_num_reference = FirebaseDatabase.getInstance().getReference().child("user_num");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        userId = (EditText) findViewById(R.id.userId);
        userPw = (EditText) findViewById(R.id.userPw);
        userPw2 = (EditText) findViewById(R.id.userPw2);
        userName = (EditText) findViewById(R.id.userName);
        userEmail = (EditText) findViewById(R.id.userEmail);
        idCheckBtn = (Button) findViewById(R.id.idCheckBtn);
        emailCheckBtn = (Button) findViewById(R.id.emailCheckBtn);
        submitBtn = (Button) findViewById(R.id.submitBtn);
        groupGender = (RadioGroup) findViewById(R.id.groupGender);
        groupCampus = (RadioGroup) findViewById(R.id.groupCampus);
        rdMale = (RadioButton) findViewById(R.id.rdMale);
        rdFemale = (RadioButton) findViewById(R.id.rdFemale);
        rdHuman = (RadioButton) findViewById(R.id.rdHuman);
        rdNature = (RadioButton) findViewById(R.id.rdNature);

        idCheckBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputId = userId.getText().toString();
                //Toast.makeText(getApplicationContext(), "입력한 아이디는 " + inputId + "입니다.", Toast.LENGTH_LONG).show();
                reference.addListenerForSingleValueEvent(dataListener);
            }
        });

        emailCheckBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputEmail = userEmail.getText().toString();
                reference.addListenerForSingleValueEvent(dataListener2);
            }
        });

        submitBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isIdChecked) {
                    Toast.makeText(getApplicationContext(), "아이디 중복 확인을 해주세요", Toast.LENGTH_LONG).show();
                    return;
                }
                if(userPw.getText().toString().length() < 8) {
                    Toast.makeText(getApplicationContext(), "비밀번호를 8자 이상으로 해주세요", Toast.LENGTH_LONG).show();
                    return;
                }
                if(!userPw.getText().toString().equals(userPw2.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "비밀번호 확인이 일치하지 않습니다", Toast.LENGTH_LONG).show();
                    return;
                }
                if(userName.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "이름을 입력 해주세요.", Toast.LENGTH_LONG).show();
                    return;
                }
                if(!isEmailChecked) {
                    Toast.makeText(getApplicationContext(), "이메일 중복 확인을 해주세요.", Toast.LENGTH_LONG).show();
                    return;
                }
                if(!rdMale.isChecked() && !rdFemale.isChecked()) {
                    Toast.makeText(getApplicationContext(), "성별을 체크해주세요", Toast.LENGTH_LONG).show();
                }
                if(!rdHuman.isChecked() && !rdNature.isChecked()) {
                    Toast.makeText(getApplicationContext(), "캠퍼스를 체크해주세요", Toast.LENGTH_LONG).show();
                }
                boolean isMale;
                boolean isHuman;
                if(rdMale.isChecked()) {
                    //남자임
                    isMale = true;
                } else {
                    isMale = false;
                }
                if(rdHuman.isChecked()) {
                    isHuman = true;
                } else {
                    isHuman = false;
                }
                Toast.makeText(getApplicationContext(), "가입 완료", Toast.LENGTH_LONG).show();

                Map<String, Object> childUpdates = new HashMap<>();
                Map<String, Object> key_childUpdates = new HashMap<>();
                Map<String, Object> postValues = new HashMap<>();

                postValues.put("name", userName.getText().toString());
                postValues.put("userId", userId.getText().toString());
                postValues.put("password", userPw.getText().toString());
                postValues.put("userCampus", isMale);
                postValues.put("userMale", isMale);
                postValues.put("email", userEmail.getText().toString());
                userNum++;
                childUpdates.put("user/" + userNum, postValues);
                key_childUpdates.put("user_num", userNum);

                user_num_reference.updateChildren(key_childUpdates);
                reference.updateChildren(childUpdates);

                finish();
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
            for (DataSnapshot dataSnapshot1 : dataSnapshot.child("user").getChildren()) {
                //Log.d("asdasda",dataSnapshot1.getValue().toString());
                //key = (Long) dataSnapshot2.getValue();
                Map<String, Object> message = (Map<String, Object>) dataSnapshot1.getValue();
                Log.d("asdasda", dataSnapshot1.getValue().toString());
                String id = (String) message.get("userId");
                //Toast.makeText(getApplicationContext(), "받아온 값은 " + id, Toast.LENGTH_LONG).show();
                if (inputId.equals(id)) {
                    //아이디가 중복됨
                    Toast.makeText(getApplicationContext(), "아이디가 중복됩니다. ", Toast.LENGTH_LONG).show();
                    isIdChecked = false;
                    break;
                }
            }
            if(inputId.equals("")) {
                Toast.makeText(getApplicationContext(), "아이디를 입력해주세요", Toast.LENGTH_LONG).show();
                return;
            }
            Toast.makeText(getApplicationContext(), "아이디 중복 확인 완료. ", Toast.LENGTH_LONG).show();
            isIdChecked = true;
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    ValueEventListener dataListener2 = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for(DataSnapshot dataSnapshot2 : dataSnapshot.child("user_num").getChildren()){
                //Log.d("asdasda",dataSnapshot2.getValue().toString());
                userNum = (Long) dataSnapshot2.getValue();
            }
            for (DataSnapshot dataSnapshot1 : dataSnapshot.child("user").getChildren()) {
                //Log.d("asdasda",dataSnapshot1.getValue().toString());
                //key = (Long) dataSnapshot2.getValue();
                Map<String, Object> message = (Map<String, Object>) dataSnapshot1.getValue();
                String email = (String) message.get("email");
                //Toast.makeText(getApplicationContext(), "받아온 값은 " + id, Toast.LENGTH_LONG).show();
                if (inputEmail.equals(email)) {
                    //아이디가 중복됨
                    Toast.makeText(getApplicationContext(), "이메일이 중복됩니다. ", Toast.LENGTH_LONG).show();
                    isEmailChecked = false;
                    break;
                }
            }
            Toast.makeText(getApplicationContext(), "이메일 중복 확인 완료. ", Toast.LENGTH_LONG).show();
            isEmailChecked = true;
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
}