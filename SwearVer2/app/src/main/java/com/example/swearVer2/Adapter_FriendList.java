package com.example.swearVer2;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class Adapter_FriendList extends RecyclerView.Adapter<Adapter_FriendList.ViewHolder> {


    private ArrayList<FriendListItem> itemDataList;
    private Context context;


    public Adapter_FriendList(ArrayList<FriendListItem> itemDataList, Context context) {
        this.itemDataList = itemDataList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friendlist_item_layout, parent, false);

        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final FriendListItem item = itemDataList.get(position);

        holder.profileImage_imageView.setImageBitmap(item.getProfileImage());
        holder.username_textView.setText(item.getUsername());

        holder.profileImage_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent_checkFriendsGoal = new Intent(context, CheckFriendsGoal.class);
                intent_checkFriendsGoal.putExtra("friendsEmail", item.getFriendEmail());
                context.startActivity(intent_checkFriendsGoal);

                //친구가 열람중인지 표시 - CheckFriendsGoal화면에서 backpress 눌렀을 때 false로 바뀜
                Main.isFreindMode = true;
            }
        });
    }


    @Override
    public int getItemCount() {
        return itemDataList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView username_textView, friendEmail_textView;
        ImageView profileImage_imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //뷰홀더와 아이템 레이아웃 내 각 뷰를 연결
            friendEmail_textView = (TextView)itemView.findViewById(R.id.friendlist_item_friendEmail);
            username_textView = (TextView)itemView.findViewById(R.id.friendlist_item_textView);
            profileImage_imageView = (ImageView)itemView.findViewById(R.id.friendlist_item_imageView);
        }
    }

}
