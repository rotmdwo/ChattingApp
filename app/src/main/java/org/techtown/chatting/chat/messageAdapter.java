package org.techtown.chatting.chat;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.techtown.chatting.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class messageAdapter extends RecyclerView.Adapter<messageAdapter.ViewHolder> {
    private Context mContext;
    ArrayList<message> items = new ArrayList<message>();
    static int view_num = 0;
    static FirebaseStorage storage = FirebaseStorage.getInstance();
    @NonNull
    @Override
    public messageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView;
        switch(i){  //xml 파일 두 개 쓰는 방법
            case 0:
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                itemView = inflater.inflate(R.layout.message_right,viewGroup,false);
                return new messageAdapter.ViewHolder(itemView);
            case 1:
                LayoutInflater inflater1 = LayoutInflater.from(viewGroup.getContext());
                itemView = inflater1.inflate(R.layout.message_left,viewGroup,false);
                return new messageAdapter.ViewHolder(itemView);
        }
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        itemView = inflater.inflate(R.layout.message_right,viewGroup,false);
        return new messageAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull messageAdapter.ViewHolder viewHolder, int i) {
        message item = items.get(i);
        viewHolder.setItem(item);
    }

    @Override
    public int getItemViewType(int position) {  //xml 파일 두 개 쓰는 방법
        message message = items.get(position);
        if(message.getSender().equals(restoreState())){
            return view_num = 0;
        } else{
            return view_num = 1;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        CircleImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textView= itemView.findViewById(R.id.textView);

            if(view_num == 1){
                imageView = itemView.findViewById(R.id.imageView);
            }

        }

        public void setItem(message message){
            textView.setText(message.getMessage());
            String file_path = "profile_picture/profile_picture_"+message.getSender();
           if(view_num == 1){
               StorageReference ref = storage.getReference().child(file_path);
               ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                   @Override
                   public void onComplete(@NonNull Task<Uri> task) {
                       if(task.isSuccessful()){
                           Context context = (ChatRoomActivity) ChatRoomActivity.mContext;
                           Glide.with(context).load(task.getResult()).into(imageView);
                       }
                   }
               });
           }
        }
    }

    public void addItem(message item,Context context){
        items.add(item);
        this.mContext = context; // getSharedPreferences() 빨간줄 뜰 때 사용
    }

    public void setItems(ArrayList<message> items){
        this.items = items;
    }

    public message getItem(int position){
        return items.get(position);
    }

    public message setItem(int position, message item){
        return items.set(position,item);
    }

    protected String restoreState(){
        SharedPreferences pref = mContext.getSharedPreferences("pref", Activity.MODE_PRIVATE); // getSharedPreferences() 빨간줄 뜰 때 사용
        return pref.getString("id","");
    }
}
