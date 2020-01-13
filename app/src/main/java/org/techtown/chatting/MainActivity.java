package org.techtown.chatting;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    CheckBox campus1;
    CheckBox campus2;
    CheckBox male1;
    CheckBox male2;
    Button startBtn;
    Boolean userCampus = true;
    Boolean userMale = true;
    long key = 1;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    UserOption userOption = null;
    Boolean isMatched = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        campus1 = findViewById(R.id.campus1);
        campus2 = findViewById(R.id.campus2);
        male1 = findViewById(R.id.male1);
        male2 = findViewById(R.id.male2);
        startBtn = findViewById(R.id.start_btn);

        startBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(!campus1.isChecked()&&!campus2.isChecked()){
                    Toast.makeText(getApplicationContext(), "옵션을 선택해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!male1.isChecked()&&!male2.isChecked()){
                    Toast.makeText(getApplicationContext(), "옵션을 선택해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                reference.addListenerForSingleValueEvent(dataListener);
                if(isMatched){
                    Toast.makeText(getApplicationContext(), "매칭 성공", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    Toast.makeText(getApplicationContext(), "매칭 실패, 대기열에 올립니다.", Toast.LENGTH_SHORT).show();
                    postFirebaseDatabase();
                    return;
                }
            }
        });

        reference.addChildEventListener(new ChildEventListener(){
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s){
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot,String s){
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

    private void waitingListener(DataSnapshot dataSnapshot){

    }

    ValueEventListener dataListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for(DataSnapshot dataSnapshot1 : dataSnapshot.child("waiting").getChildren()){
                Log.d("asdasda", dataSnapshot1.getKey());
                Log.d("asdasda", dataSnapshot1.toString());
                try{
                    UserOption get = dataSnapshot1.getValue(UserOption.class);

                    if(userCampus){
                        if(!get.campus1)    continue;
                    }
                    else{
                        if(!get.campus2)    continue;
                    }
                    if(userMale){
                        if(!get.male1)   continue;
                    }
                    else{
                        if(!get.male2)  continue;
                    }
                    if(get.userMale){
                        if(!male1.isChecked()) continue;
                    }
                    else{
                        if(!male2.isChecked()) continue;
                    }
                    if(get.userCampus){
                        if(!campus1.isChecked())  continue;
                    }
                    else{
                        if(!campus2.isChecked())   continue;
                    }

                    isMatched = true;
                    userOption = get;
                    break;
                } catch(DatabaseException e){
                    dataSnapshot1.getKey();
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    public void postFirebaseDatabase(){
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        UserOption userOption = new UserOption(campus1.isChecked(), campus2.isChecked(), male1.isChecked(), male2.isChecked(), userCampus, userMale);
        postValues = userOption.toMap();
        childUpdates.put("waiting/"+Long.toString(key), postValues);
        reference.updateChildren(childUpdates);
    }
}
