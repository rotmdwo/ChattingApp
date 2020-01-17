package org.techtown.chatting;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class friendAdapter extends RecyclerView.Adapter<friendAdapter.ViewHolder>{
    ArrayList<friend> items = new ArrayList<friend>();
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.friend_item,viewGroup,false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        friend item = items.get(i);
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

        public void setItem(friend book){
            textView.setText(book.getName());
            textView2.setText(book.getState_message());
        }
    }

    public void addItem(friend item){
        items.add(item);
    }

    public void setItems(ArrayList<friend> items){
        this.items = items;
    }

    public friend getItem(int position){
        return items.get(position);
    }

    public friend setItem(int position, friend item){
        return items.set(position,item);
    }
}
