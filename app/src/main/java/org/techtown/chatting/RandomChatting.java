package org.techtown.chatting;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class RandomChatting {
    String userName;
    String text;
    long key;

    public RandomChatting(){

    }

    public RandomChatting(String name, String text, long key){
        userName = name;
        this.text = text;
        this.key = key;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        Log.d("chatChange", userName);
        Log.d("chatChange", text);
        result.put("usr", userName);
        result.put("text", text);
        result.put("key", key);
        return result;
    }
}
