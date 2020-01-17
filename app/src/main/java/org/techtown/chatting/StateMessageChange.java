package org.techtown.chatting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class StateMessageChange extends AppCompatActivity {
    EditText editText;
    Button button;
    String changedMessage;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    int userNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state_message_change);

        editText = findViewById(R.id.editText);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editText.getText().toString().equals("")){
                    changedMessage = editText.getText().toString();

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
                if(restoreState().equals(message.get("userId"))){
                    userNum = Integer.parseInt(dataSnapshot1.getKey());
                }

            }

            Map<String, Object> childUpdates = new HashMap<>();
            Map<String, Object> postValues = new HashMap<>();
            postValues.put("statement_message", changedMessage);
            childUpdates.put("user/" + userNum, postValues);
            reference.updateChildren(childUpdates);
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
