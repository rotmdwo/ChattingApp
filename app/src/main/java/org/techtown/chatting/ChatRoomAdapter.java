package org.techtown.chatting;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ViewHolder>{
    ArrayList<ChatRoom> items = new ArrayList<ChatRoom>();
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.chat_room_item,viewGroup,false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        ChatRoom item = items.get(i);
        viewHolder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textView= itemView.findViewById(R.id.textView);
        }

        public void setItem(ChatRoom book){
            textView.setText(book.getRoomName());
        }
    }

    public void addItem(ChatRoom item){
        items.add(item);
    }

    public void setItems(ArrayList<ChatRoom> items){
        this.items = items;
    }

    public ChatRoom getItem(int position){
        return items.get(position);
    }

    public ChatRoom setItem(int position, ChatRoom item){
        return items.set(position,item);
    }
}
