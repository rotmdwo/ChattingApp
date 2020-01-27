package org.techtown.chatting.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import org.techtown.chatting.R;
import org.techtown.chatting.chat.ChattingRoom;

import java.util.ArrayList;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ViewHolder> {
    //현재는 채팅방 이름만을 저장함, 추후 수정
    private ArrayList<ChattingRoom> rooms = new ArrayList<>() ;

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }

    private OnItemClickListener mListener = null ;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }

    /*
    public ChatRoomAdapter() {

    }
    */

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView1 ;

        ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조. (hold strong reference)
            textView1 = itemView.findViewById(R.id.textView) ;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {

                        if (mListener != null) {
                            mListener.onItemClick(v, pos) ;
                        }
                    }
                }
            });
        }
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public ChatRoomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.chat_room_item, parent, false) ;
        ChatRoomAdapter.ViewHolder vh = new ChatRoomAdapter.ViewHolder(view) ;

        return vh ;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(ChatRoomAdapter.ViewHolder holder, int position) {
        String text = rooms.get(position).getRoomName();
        holder.textView1.setText(text) ;
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return rooms.size() ;
    }

    public int getItem(int position) {
        int roomNum = Integer.parseInt(rooms.get(position).getRoomId());
        return roomNum;
    }

    public void addItem(ChattingRoom item) {
        rooms.add(item);
    }
}
