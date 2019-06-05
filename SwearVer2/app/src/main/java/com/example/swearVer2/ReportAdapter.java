package com.example.swearVer2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {


    private ArrayList<ReportItem> reportItemArrayList;
    private Context mContext;

    private ReportViewHolder.OnReportListener mOnReportListener;
//    private ReportViewHolder.OnReportLongClickListener mOnReportLongClickListener;

    public ReportAdapter(Context mContext, ArrayList<ReportItem> reportItemArrayList, ReportViewHolder.OnReportListener onReportListener) {
        this.reportItemArrayList = reportItemArrayList;
        this.mContext = mContext;
        this.mOnReportListener = onReportListener;
//        this.mOnReportLongClickListener = onReportLongClickListener;
    }



    @NonNull
    @Override
    public ReportAdapter.ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_item_layout, parent, false);

        ReportViewHolder viewHolder = new ReportViewHolder(view, mOnReportListener);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ReportAdapter.ReportViewHolder viewHolder, int position) {

        final ReportItem item = reportItemArrayList.get(position);

        viewHolder.rTitle.setText(item.getReportTitle());
        viewHolder.rTime.setText(item.getReportTime());
        viewHolder.prefrenceName.setText(item.getReportPreferenceName());
//        viewHolder.rPicture.setImageResource(item.getReportImage());

//        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mContext, ReportView.class);
//                intent.putExtra("title", item.getReportTitle());
//                mContext.startActivity(intent);
//            }
//        });
    }


    @Override
    public int getItemCount() {
        return reportItemArrayList.size();
    }



    public static class ReportViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener,View.OnClickListener{

//        ImageView rPicture;
        TextView rTitle;
        TextView rTime;
        TextView prefrenceName;

        OnReportListener onReportListener;
//        OnReportLongClickListener onReportLongClickListener;

        ReportViewHolder(@NonNull View view, OnReportListener onReportListener){
            super(view);
//            rPicture = (ImageView)view.findViewById(R.id.rPicture_imageView);
            rTitle = (TextView)view.findViewById(R.id.rTitle_textView);
            rTime = (TextView)view.findViewById(R.id.rTime_textView);
            prefrenceName = (TextView)view.findViewById(R.id.rIndex_textView);

            this.onReportListener = onReportListener;
//            this.onReportLongClickListener = onReportLongClickListener;



            //OnClickListener를 뷰홀더 전체에 적용. 아이템마다 클릭리스너를 만들어줌
            //this= 클래스가 implement한 View.OnClickListener를 참조
            itemView.setOnClickListener(this);

//            itemView.setOnLongClickListener(this);

            itemView.setOnCreateContextMenuListener(this);
        }


        @Override
        public void onClick(View v) {
            onReportListener.onReportClick(getAdapterPosition()); //아이템이 클릭되면 -> 해당 아이템의 position을 넘겨줌
        }

//        @Override
//        public boolean onLongClick(View v) {
//            onReportLongClickListener.onLongClick(getAdapterPosition());
//            return true;
//        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//            menu.setHeaderTitle("Select an Option");
//            menu.add(this.getAdapterPosition(), 121,0, "Edit");
            menu.add(this.getAdapterPosition(), 122,1, "Delete");
        }


        //onBindViewHolder 안에 클릭리스너를 넣는 것보다, 인터페이스를 사용하는 것이 퍼포먼스 측면에서 낫다?
        //클릭 인터페이스를 액티비티에 적용 -> 클릭된 아이템의 position을 알려줌
        public interface OnReportListener{
            void onReportClick(int position);
        }

//        public interface OnReportLongClickListener{
//            void onLongClick(int position);
//        }



    }



    public void addItem(ReportItem item, int position){
        reportItemArrayList.add(position, item);
        notifyItemInserted(position);
    }

    public void removeItem(int position){
        reportItemArrayList.remove(position);
        Toast.makeText(mContext, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
        notifyDataSetChanged();
    }


//    public void remove(ReportItem item){
//        int position = reportItemArrayList.indexOf(item);
//        reportItemArrayList.remove(position);
//        notifyItemInserted(position);
//    }

}



