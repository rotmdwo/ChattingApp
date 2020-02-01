package org.techtown.chatting.adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.techtown.chatting.chat.ChatListActivity;
import org.techtown.chatting.chat.swipe.DeletionDialog;
import org.techtown.chatting.R;
import org.techtown.chatting.chat.ChattingRoom;
import org.techtown.chatting.chat.swipe.ItemDeleted;
import org.techtown.chatting.chat.swipe.ModificationDialog;
import org.techtown.chatting.chat.swipe.ItemTouchHelperListener;
import org.techtown.chatting.chat.swipe.OnDialogListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ViewHolder> implements ItemTouchHelperListener, OnDialogListener, ItemDeleted {
    //현재는 채팅방 이름만을 저장함, 추후 수정
    private ItemDeleted mDeleted;
    private ArrayList<ChattingRoom> rooms = new ArrayList<>() ;
    private OnItemClickListener mListener = null ;
    Context context;

    public ChatRoomAdapter(Context context, ItemDeleted itemDeleted){ this.context = context; this.mDeleted = itemDeleted;}

    @Override
    public void onFinish(int position, ChattingRoom chatRoom) {
        rooms.set(position, chatRoom);
        notifyItemRemoved(position);
    }

    @Override
    public void onDeleted(int position) {
        String removed = this.getRoomIdByPosition(position);
        rooms.remove(removed);
        notifyItemRemoved(position);
        mDeleted.itemRemoved(removed);
    }

    @Override
    public void itemRemoved(String position) {

    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }

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

    @Override
    public boolean onItemMove(int from_position, int to_position) {
        ChattingRoom chatRoom = rooms.get(from_position);   //받아 놓기
        rooms.remove(from_position);                        //삭제
        rooms.add(to_position, chatRoom);                   //이동할 곳에 추가

        notifyItemMoved(from_position, to_position);
        return true;
    }

    @Override
    public void onItemSwipe(int position) { }

    @Override
    public void onLeftClick(int position, RecyclerView.ViewHolder viewHolder) {
        //수정 버튼 클릭시 다이얼로그 생성
        ModificationDialog dialog = new ModificationDialog(context, position, rooms.get(position));
        //화면 사이즈 구하기
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        //다이얼로그 사이즈 세팅
        WindowManager.LayoutParams wm = dialog.getWindow().getAttributes();
        wm.copyFrom(dialog.getWindow().getAttributes());
        wm.width = (int) (width * 0.7);
        wm.height = height/2;
        //다이얼로그 Listener 세팅
        dialog.setDialogListener(this);

        //다이얼로그 띄우기
        dialog.show();
    }

    @Override
    public void onRightClick(int position, RecyclerView.ViewHolder viewHolder) {
        DeletionDialog notification = new DeletionDialog(context, position, rooms.get(position));
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        //다이얼로그 사이즈 세팅
        WindowManager.LayoutParams wm = notification.getWindow().getAttributes();
        wm.copyFrom(notification.getWindow().getAttributes());
        wm.width = (int) (width * 0.7);
        wm.height = height/2;
        //다이얼로그 Listener 세팅
        notification.setDialogListener(this);

        //다이얼로그 띄우기
        notification.show();
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

    public String getRoomIdByPosition(int position) { return rooms.get(position).getRoomId(); }

    public void addItem(ChattingRoom item) {
        rooms.add(item);
    }

    public void removeItem(int position) {
        rooms.remove(position);
    }

}
