package org.techtown.chatting.setting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.techtown.chatting.R;

public class ProfileManagement extends AppCompatActivity {
    static final String[] LIST_MENU = {"상태메세지 변경","프로필사진 변경"} ;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_management);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, LIST_MENU);
        listView = (ListView)findViewById(R.id.listview1);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String item = (String)adapterView.getItemAtPosition(i);

                if(item.equals("상태메세지 변경")) {
                    Intent intent = new Intent(getApplicationContext(), StateMessageChange.class);
                    startActivity(intent);
                }
            }
        });
    }
}
