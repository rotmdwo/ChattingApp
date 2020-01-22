package org.techtown.chatting.ranChat;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class UserOption {
    Boolean campus1;
    Boolean campus2;
    Boolean male1;
    Boolean male2;

    Boolean userCampus;
    Boolean userMale;

    public UserOption(){

    }

    public UserOption(Boolean campus1, Boolean campus2, Boolean male1, Boolean male2, Boolean userCampus, Boolean userMale){
        this.campus1 = campus1;
        this.campus2 = campus2;
        this.male1 = male1;
        this.male2 = male2;
        this.userCampus = userCampus;
        this.userMale = userMale;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("campus1", campus1);
        result.put("campus2", campus2);
        result.put("male1", male1);
        result.put("male2", male2);
        result.put("userCampus", userCampus);
        result.put("userMale", userMale);
        return result;
    }
}
