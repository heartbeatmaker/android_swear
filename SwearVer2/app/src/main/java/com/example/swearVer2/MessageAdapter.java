package com.example.swearVer2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private static final String TAG = "MsgRecyclerViewAdapter";

    private ArrayList<MessageItem> mItemArrayList;
    ReadMessage readMessageActivity;
    private Context mContext;

    public MessageAdapter(ArrayList<MessageItem> mItemArrayList, Context mContext) {
        this.mItemArrayList = mItemArrayList;
        this.mContext = mContext;
        readMessageActivity = (ReadMessage) mContext;
    }

    @NonNull
    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout, parent, false);

        MessageViewHolder viewHolder = new MessageViewHolder(view, readMessageActivity);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MessageViewHolder viewHolder, int position) {

        final MessageItem item = mItemArrayList.get(position);

        viewHolder.profile.setImageBitmap(item.getMessageProfile());
        viewHolder.name.setText(item.getMessageName());
        viewHolder.content.setText(item.getMessageContent());
        viewHolder.time.setText(item.getMessageTime());
        viewHolder.count.setText(item.getMessageCount());

        //액션모드일때만 체크박스가 보이도록
        if(!readMessageActivity.is_in_action_mode){
            viewHolder.checkBox.setVisibility(View.GONE);
        }
        else{
            viewHolder.checkBox.setVisibility(View.VISIBLE);
            viewHolder.checkBox.setChecked(false);
        }

        //프로필 사진을 클릭하면 해당 사용자의 프로필 화면이 뜬다
        viewHolder.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "프로필로 이동합니다", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mItemArrayList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView profile;
        TextView name, content, time, count;
        CheckBox checkBox;
        ReadMessage readMessageActivity;
        RelativeLayout parentLayout;

        public MessageViewHolder(@NonNull View itemView, ReadMessage readMessageActivity) {
            super(itemView);
            this.profile = itemView.findViewById(R.id.messageProfile);
            this.name = itemView.findViewById(R.id.messageName);
            this.content = itemView.findViewById(R.id.messageContent);
            this.time = itemView.findViewById(R.id.messageTime);
            this.count = itemView.findViewById(R.id.messageCount);
            this.checkBox = (CheckBox)itemView.findViewById(R.id.checkBox);
            this.readMessageActivity = readMessageActivity;
            this.parentLayout = itemView.findViewById(R.id.messageLayout);
            parentLayout.setOnLongClickListener(readMessageActivity);
            checkBox.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            readMessageActivity.prepareSelection(v, getAdapterPosition());
        }
    }

    public void updateAdapter(ArrayList<MessageItem> list){ //리스트에 담긴 항목을 삭제한다

        for(MessageItem message : list){
            mItemArrayList.remove(message);
        }
        notifyDataSetChanged();
    }

}
