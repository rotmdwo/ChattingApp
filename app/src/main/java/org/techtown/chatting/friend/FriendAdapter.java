package org.techtown.chatting.friend;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.chatting.R;

import java.util.ArrayList;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder>{
    ArrayList<Friend> items = new ArrayList<Friend>();
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.friend_item,viewGroup,false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Friend item = items.get(i);
        viewHolder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView, textView2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textView= itemView.findViewById(R.id.textView);
            textView2 = itemView.findViewById(R.id.textView2);
        }

        public void setItem(Friend book){
            textView.setText(book.getName());
            textView2.setText(book.getState_message());
        }
    }

    public void addItem(Friend item){
        items.add(item);
    }

    public void setItems(ArrayList<Friend> items){
        this.items = items;
    }

    public Friend getItem(int position){
        return items.get(position);
    }

    public Friend setItem(int position, Friend item){
        return items.set(position,item);
    }
}