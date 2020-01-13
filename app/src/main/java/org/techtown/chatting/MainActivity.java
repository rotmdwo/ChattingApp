package org.techtown.chatting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private Button button;
    private EditText editText;
    private ListView lv_chatting;
    private ArrayAdapter<String> arrayAdapter;
    private String str_name;
    private String str_msg;
    private String chat_user;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("message");
    int key;
    int msg_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Intent intent = getIntent();
        if(intent != null) key = intent.getIntExtra("key",0);

        lv_chatting = findViewById(R.id.lv_chatting);
        button = findViewById(R.id.btn_send);
        editText = findViewById(R.id.et_msg);

        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        lv_chatting.setAdapter(arrayAdapter);

        lv_chatting.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        str_name = "Guest " + new Random().nextInt(1000);

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Map<String, Object> map = new HashMap<String,Object>();
                String key = reference.push().getKey();
                reference.updateChildren(map);
                DatabaseReference dbRef =reference.child(key);
                Map<String, Object> objectMap = new HashMap<String,Object>();

                objectMap.put("str_name",str_name);
                objectMap.put("text",editText.getText().toString());
                objectMap.put("key",key);

                dbRef.updateChildren(objectMap);
                editText.setText("");
            }
        });

        reference.addChildEventListener(new ChildEventListener(){
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s){
                chatListener(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot,String s){
                chatListener(dataSnapshot);
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

    private void chatListener(DataSnapshot dataSnapshot){
        Iterator i = dataSnapshot.getChildren().iterator();

        while(i.hasNext()){
            chat_user = (String) ((DataSnapshot) i.next()).getValue();
            str_msg = (String) ((DataSnapshot) i.next()).getValue();
            msg_key = (int) ((DataSnapshot) i.next()).getValue();

            if(key==msg_key){
                arrayAdapter.add(chat_user+" : " + str_msg);
            }
        }

        arrayAdapter.notifyDataSetChanged();
    }
}
