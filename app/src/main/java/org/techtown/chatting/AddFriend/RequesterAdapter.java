package org.techtown.chatting.AddFriend;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.techtown.chatting.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RequesterAdapter extends RecyclerView.Adapter<RequesterAdapter.ViewHolder>{
    ArrayList<Requester> items = new ArrayList<Requester>();
    Context mContext;
    private DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("FriendRequest"); //리퀘스트 삭제 + 수락(과 동시에 삭제)
    String deleteRequester, deleteMe;
    int deletePosition;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("friend");  // 리퀘스트 수락
    String acceptRequester, acceptMe;
    int acceptPosition;

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }

    private RequesterAdapter.OnItemClickListener mListener = null ;

    public void setOnItemClickListener(RequesterAdapter.OnItemClickListener listener) {
        this.mListener = listener ;
    }

    @NonNull
    @Override
    public RequesterAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.friend_request_item,viewGroup,false);

        return new RequesterAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RequesterAdapter.ViewHolder viewHolder, int i) {
        Requester item = items.get(i);
        viewHolder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        ImageButton button;
        ImageButton button2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.imageButton);  //리사이클러뷰 안의 각 버튼 작동하는 법
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {  // 리퀘스트 수락
                    acceptRequester = textView.getText().toString();
                    acceptMe = restoreState();
                    acceptPosition = getAdapterPosition();
                    reference2.addListenerForSingleValueEvent(dataListener3);
                    reference.addListenerForSingleValueEvent(dataListener);
                }
            });

            button2 = itemView.findViewById(R.id.imageButton2);
            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {  //리퀘스트 삭제
                    deleteRequester = textView.getText().toString();
                    deleteMe = restoreState();
                    deletePosition = getAdapterPosition();
                    reference2.addListenerForSingleValueEvent(dataListener2);
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {

                        if (mListener != null) {
                            mListener.onItemClick(view, pos) ;
                        }
                    }
                }
            });

            textView= itemView.findViewById(R.id.textView);
        }

        public void setItem(Requester requester){
            textView.setText(requester.getId());
        }
    }

    public void addItem(Requester item, Context context){
        items.add(item);
        this.mContext = context;
    }

    public void setItems(ArrayList<Requester> items){
        this.items = items;
    }

    public Requester getItem(int position){
        return items.get(position);
    }

    public Requester setItem(int position, Requester item){
        return items.set(position,item);
    }

    ValueEventListener dataListener2 = new ValueEventListener() {  //리퀘스트 삭제
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            Map<String, Object> message = (Map<String, Object>) dataSnapshot.getValue();
            int user_num = Integer.parseInt(message.get("num").toString());

            for(int i=1;i<=user_num;i++){
                Map<String, Object> message2 = (Map<String, Object>) message.get(Integer.toString(i));
                if(((String)message2.get("toWhom")).equals(deleteMe) && ((String)message2.get("requester")).equals(deleteRequester)){
                    Map<String, Object> childUpdates1 = new HashMap<>();
                    Map<String, Object> postValues = new HashMap<>();
                    postValues.put("requester","deleted");
                    postValues.put("toWhom","deleted");
                    childUpdates1.put(Integer.toString(i),postValues);
                    reference2.updateChildren(childUpdates1);

                    // 리사이클러뷰에서 아이템 삭제하고 갱신하는 방법. 이 중 하나라도 코드 빼먹으면 갱신 버그가 생김.
                    items.remove(deletePosition);
                    ((ReceiveFriendRequestActivity)ReceiveFriendRequestActivity.mContext).recyclerView.removeViewsInLayout(deletePosition,1); //다른 액티비티의 변수 사용하는 법
                    ((ReceiveFriendRequestActivity)ReceiveFriendRequestActivity.mContext).adapter.notifyItemRemoved(deletePosition);

                    break;
                }
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    ValueEventListener dataListener = new ValueEventListener() {  //리퀘스트 수락
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Boolean requesterAdded = false, meAdded = false;
            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                if (dataSnapshot1.getKey().equals(acceptMe)) {
                    Map<String, Object> message = (Map<String, Object>) dataSnapshot1.getValue();
                    int num = Integer.parseInt(message.get("num").toString());
                    num++;
                    Map<String, Object> childUpdates1 = new HashMap<>();
                    childUpdates1.put(acceptMe + "/num", num);
                    reference.updateChildren(childUpdates1);

                    Map<String, Object> childUpdates2 = new HashMap<>();
                    childUpdates2.put(acceptMe + "/" + num, acceptRequester);
                    reference.updateChildren(childUpdates2);

                    meAdded = true;
                }

                if (dataSnapshot1.getKey().equals(acceptRequester)) {
                    Map<String, Object> message = (Map<String, Object>) dataSnapshot1.getValue();
                    int num = Integer.parseInt(message.get("num").toString());
                    num++;
                    Map<String, Object> childUpdates1 = new HashMap<>();
                    childUpdates1.put(acceptRequester + "/num", num);
                    reference.updateChildren(childUpdates1);

                    Map<String, Object> childUpdates2 = new HashMap<>();
                    childUpdates2.put(acceptRequester + "/" + num, acceptMe);
                    reference.updateChildren(childUpdates2);

                    requesterAdded = true;
                }
            }

            if (meAdded == false) {
                int num = 1;
                Map<String, Object> childUpdates1 = new HashMap<>();
                childUpdates1.put(acceptMe + "/num", num);
                reference.updateChildren(childUpdates1);

                Map<String, Object> childUpdates2 = new HashMap<>();
                childUpdates2.put(acceptMe + "/" + num, acceptRequester);
                reference.updateChildren(childUpdates2);
            }

            if (requesterAdded == false) {
                int num = 1;
                Map<String, Object> childUpdates1 = new HashMap<>();
                childUpdates1.put(acceptRequester + "/num", num);
                reference.updateChildren(childUpdates1);

                Map<String, Object> childUpdates2 = new HashMap<>();
                childUpdates2.put(acceptRequester + "/" + num, acceptMe);
                reference.updateChildren(childUpdates2);
            }


        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    ValueEventListener dataListener3 = new ValueEventListener() {  //리퀘스트 삭제
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            Map<String, Object> message = (Map<String, Object>) dataSnapshot.getValue();
            int user_num = Integer.parseInt(message.get("num").toString());

            for(int i=1;i<=user_num;i++){
                Map<String, Object> message2 = (Map<String, Object>) message.get(Integer.toString(i));
                if(((String)message2.get("toWhom")).equals(acceptMe) && ((String)message2.get("requester")).equals(acceptRequester)){
                    Map<String, Object> childUpdates1 = new HashMap<>();
                    Map<String, Object> postValues = new HashMap<>();
                    postValues.put("requester","accepted");
                    postValues.put("toWhom","accepted");
                    childUpdates1.put(Integer.toString(i),postValues);
                    reference2.updateChildren(childUpdates1);

                    // 리사이클러뷰에서 아이템 삭제하고 갱신하는 방법. 이 중 하나라도 코드 빼먹으면 갱신 버그가 생김.
                    items.remove(acceptPosition);
                    ((ReceiveFriendRequestActivity)ReceiveFriendRequestActivity.mContext).recyclerView.removeViewsInLayout(acceptPosition,1); //다른 액티비티의 변수 사용하는 법
                    ((ReceiveFriendRequestActivity)ReceiveFriendRequestActivity.mContext).adapter.notifyItemRemoved(acceptPosition);

                    break;
                }
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    protected String restoreState(){
        SharedPreferences pref = mContext.getSharedPreferences("pref", Activity.MODE_PRIVATE);
        return pref.getString("id","");
    }
}
