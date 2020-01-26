package org.techtown.chatting.friend;

import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.chatting.R;

import java.util.ArrayList;

public class FriendAdapterForAddChatRoom extends RecyclerView.Adapter<FriendAdapterForAddChatRoom.ViewHolder> {
    ArrayList<Friend> items = new ArrayList<Friend>();
    private FriendAdapterForAddChatRoom.OnItemClickListener mListener = null ;
    private SparseBooleanArray mSelectedItems = new SparseBooleanArray(0);

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }

    @NonNull
    @Override
    public FriendAdapterForAddChatRoom.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.friend_item,parent,false);

        return new FriendAdapterForAddChatRoom.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendAdapterForAddChatRoom.ViewHolder holder, int position) {
        Friend item = items.get(position);
        holder.setItem(item);

        if ( mSelectedItems.get(position, false) ){
            holder.itemView.setBackgroundColor(Color.GREEN);
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView, textView2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //view.setBackgroundColor(Color.GRAY);
                    int pos = getAdapterPosition() ;

                    if ( mSelectedItems.get(pos, false) ){
                        mSelectedItems.put(pos, false);
                        view.setBackgroundColor(Color.WHITE);
                    } else {
                        mSelectedItems.put(pos, true);
                        view.setBackgroundColor(Color.GREEN);
                    }


                    if (pos != RecyclerView.NO_POSITION) {


                        if (mListener != null) {
                            mListener.onItemClick(view, pos) ;
                        }
                    }
                }
            });

            textView= itemView.findViewById(R.id.textView);
            textView2 = itemView.findViewById(R.id.textView2);
        }

        public void setItem(Friend book){
            textView.setText(book.getName());
            textView2.setText(book.getState_message());
        }
    }

    public void setItems(ArrayList<Friend> items){
        this.items = items;
    }
}
