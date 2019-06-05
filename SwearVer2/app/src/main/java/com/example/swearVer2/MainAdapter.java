package com.example.swearVer2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dinuscxj.progressbar.CircleProgressBar;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {


    private ArrayList<Item> itemDataList;
    private Activity activity;


    //생성자 - 보여지는 액티비티, 정보 리스트
    //생성자에 activity 혹은 context? 정확한 차이점??
    public MainAdapter(Activity activity, ArrayList<Item> itemDataList) {
        this.activity = activity;
        this.itemDataList = itemDataList;
    }


    //getItemCount(아이템 몇 개?) -> onCreateViewHolder(아이템 틀 잡기) -> MyViewHolder(틀 안의 세부항목 세팅) -> onBindViewHolder(정보넣기) 순으로 호출

    @NonNull
    @Override
    public MainAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        //아이템 레이아웃의 뷰 생성
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);

        //뷰홀더에 아이템 레이아웃의 뷰를 넣어줌
        MainViewHolder viewHolder = new MainViewHolder(view);

        //만들어진 뷰홀더를 리턴
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull MainAdapter.MainViewHolder mainViewHolder, final int position) {

        final Item item = itemDataList.get(position); //위치를 기준으로 데이터를 얻어온다

        mainViewHolder.rate.setText(item.getRate()+"%"); // 위에서 얻어온 데이터 중에서, 뷰홀더에서 세팅한 rate 변수에 맞는 정보를 넣어준다
        mainViewHolder.title.setText(item.getTitle());
        mainViewHolder.date.setText(item.getDate());
//        mainViewHolder.rateImage.setImageResource(item.getRateImage());
        mainViewHolder.prefrenceName.setText(item.getPreferenceName());
        mainViewHolder.progressBar.setProgress(item.getRate());

        //아이템을 클릭하면 -> 해당 아이템의 내용이 저장된 xml파일 이름을 전달한다
        mainViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String preferenceName = itemDataList.get(position).getPreferenceName();
                Intent intent = new Intent(activity, CheckGoals.class);
                intent.putExtra("preferenceName", preferenceName);
                intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemDataList.size(); // 아이템이 몇 개인지 알려줌
    }



    public class MainViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{

        TextView rate;
        TextView title;
        TextView date;
        CircleProgressBar progressBar;

        TextView prefrenceName;


        public MainViewHolder(@NonNull View itemView) {
            super(itemView);

            //뷰홀더와 아이템 레이아웃 내 각 뷰를 연결
            rate = (TextView)itemView.findViewById(R.id.rate_textView);
            title = (TextView)itemView.findViewById(R.id.title_textView);
            date = (TextView)itemView.findViewById(R.id.date_textView);
            progressBar = (CircleProgressBar)itemView.findViewById(R.id.main_progressBar);

            prefrenceName = (TextView)itemView.findViewById(R.id.goalPreference_textView);

            itemView.setOnCreateContextMenuListener(this); // OnCreateContextMenuListener를 현재 클래스에서 구현한다


            //왜 생성자 안에?? 생성자 밖으로 나가면 왜 리스너 안먹음?
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Toast.makeText(activity, position + "번 아이템 선택", Toast.LENGTH_SHORT).show();
                }
            });

        }



//        public boolean isCompleted(){
//            if(itemDataList.get(getAdapterPosition()).getRateImage()==R.drawable.circle_complete){
//                return true;
//            }else
//                return false;
//        }

        //메뉴 항목 선택 시 호출되는 리스너를 등록


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

//            if(isCompleted()){ //이행기간이 지난 약속만 삭제 가능
            //Menu.None: 그룹 ID
            //itemId로 구분
//            MenuItem Edit = menu.add(Menu.NONE, 1001, 1, "수정");
//            MenuItem Delete = menu.add(Menu.NONE, 1002, 2, "삭제");
//            Edit.setOnMenuItemClickListener(onEditMenu);
//            Delete.setOnMenuItemClickListener(onEditMenu);
//             }
        }

        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener(){

            @Override
            public boolean onMenuItemClick(MenuItem item){

                switch(item.getItemId()){
//                    case 1001: //수정 선택 시
//
//
//                        //수정 액티비티 띄우기
//                        Toast.makeText(activity, "해당 아이템을 수정합니다", Toast.LENGTH_SHORT).show();
//
//                        break;

                    case 1002:  //아이템 지우기

                        //정말 지울 것인지 확인
                        //매개변수에 context 넣으면 오류남. 생성자에서 메인 액티비티 가져오기
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        View view = LayoutInflater.from(activity).inflate(R.layout.activity_delete_dialog, null, false);
                        builder.setView(view);

                        final Button confirm = (Button)view.findViewById(R.id.button11);
                        final Button cancel = (Button)view.findViewById(R.id.button12);

                        final AlertDialog dialog = builder.create();


                        confirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                itemDataList.remove(getAdapterPosition());
                                notifyItemRemoved(getAdapterPosition());
                                notifyItemRangeChanged(getAdapterPosition(), itemDataList.size());
                                dialog.dismiss();
                            }
                        });

                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                        break;
                }
                return true;
            }
        };

    }

    public void addItem(Item item, int position){
        itemDataList.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(Item item){
        int position = itemDataList.indexOf(item);
        itemDataList.remove(position);
        notifyItemInserted(position);
    }

}
