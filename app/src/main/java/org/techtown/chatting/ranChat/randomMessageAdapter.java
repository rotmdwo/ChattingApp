package org.techtown.chatting.ranChat;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.chatting.R;
import org.techtown.chatting.chat.message;
import org.techtown.chatting.chat.messageAdapter;

import java.util.ArrayList;

public class randomMessageAdapter extends RecyclerView.Adapter<randomMessageAdapter.ViewHolder>{
    ArrayList<randomMessage> items = new ArrayList<randomMessage>();
    private Context mContext;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        switch(viewType){  //xml 파일 두 개 쓰는 방법
            case 0:
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                itemView = inflater.inflate(R.layout.random_message_right,parent,false);
                return new randomMessageAdapter.ViewHolder(itemView);
            case 1:
                LayoutInflater inflater1 = LayoutInflater.from(parent.getContext());
                itemView = inflater1.inflate(R.layout.random_message_left,parent,false);
                return new randomMessageAdapter.ViewHolder(itemView);
        }
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        itemView = inflater.inflate(R.layout.random_message_right,parent,false);
        return new randomMessageAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull randomMessageAdapter.ViewHolder viewHolder, int i) {
        randomMessage item = items.get(i);
        viewHolder.setItem(item);
    }

    @Override
    public int getItemViewType(int position) {  //xml 파일 두 개 쓰는 방법
        randomMessage message = items.get(position);
        if(message.getSender().equals(restoreState())){
            return 0;
        } else{
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
        }

        public void setItem(randomMessage randomMessage){
            textView.setText(randomMessage.getMessage());
        }
    }

    public void addItem(randomMessage item, Context context){
        items.add(item);
        this.mContext = context; // getSharedPreferences() 빨간줄 뜰 때 사용
    }

    public void setItems(ArrayList<randomMessage> items){
        this.items = items;
    }

    public randomMessage getItem(int position){
        return items.get(position);
    }

    public randomMessage setItem(int position, randomMessage item){
        return items.set(position,item);
    }

    protected String restoreState(){
        SharedPreferences pref = mContext.getSharedPreferences("pref", Activity.MODE_PRIVATE); // getSharedPreferences() 빨간줄 뜰 때 사용
        return pref.getString("id","");
    }
}
