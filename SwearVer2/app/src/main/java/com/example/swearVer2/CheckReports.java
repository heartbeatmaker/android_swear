package com.example.swearVer2;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CheckReports extends AppCompatActivity implements View.OnCreateContextMenuListener,ReportAdapter.ReportViewHolder.OnReportListener {

    static final int REQUEST_EDIT = 2000;
    static final int REQUEST_NEW = 3000;

    RecyclerView rcv;
    LinearLayoutManager llm;
    ReportAdapter reportAdapter;
    public static ArrayList<ReportItem> reportItemArrayList;

    String resultTitle, editResultTitle;
    int itemPosition;

    String goalPreferenceName;
    String reportListPreferenceName;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("오류확인중","보고서 목록화면 backPressed");
        finish();
        Log.d("오류확인중","보고서 목록화면 finish");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_reports);
        Log.d("오류찾는다","보고서 목록화면 onCreate");

        //약속의 제목, 기간을 표시해 줄 텍스트뷰 지정
        TextView title = (TextView)findViewById(R.id.textView14);
        TextView period = (TextView)findViewById(R.id.textView28);

        //<약속 확인창>에서 보낸 '약속이 저장된 xml 이름', '약속 제목', '약속 수행 기간' 값을 받음
        Intent intent = getIntent();
        goalPreferenceName = intent.getStringExtra("goalPreferenceName");
        String goalTitle = intent.getStringExtra("goalTitle");
        String goalPeriod = intent.getStringExtra("goalPeriod");
        title.setText(goalTitle);
        period.setText(goalPeriod);

        Log.d("오류찾는다","보고서 목록화면, 인텐트 - <약속 확인창>에서 보낸 '약속이 저장된 xml 이름', '약속 제목', '약속 수행 기간' 값을 받음");


        //리사이클러뷰
        rcv = (RecyclerView)findViewById(R.id.report_recycler);
        llm = new LinearLayoutManager(this);

        rcv.setHasFixedSize(true); //리사이클러뷰 자체의 크기가 변하지 않을 때
        rcv.setLayoutManager(llm); //리사이클러뷰의 레이아웃을 linear로 맞춘다

        reportItemArrayList = new ArrayList<>(); //ReportItem 클래스 형의 데이터를 리스트화 시킨다

        //현재 액티비티와 reportItem리스트를 매개변수로 갖는 어댑터 객체를 만든다
        //세번째 매개변수 this=onReportListener 인터페이스. 클래스가 onReportListener를 implement함 -> this로 표기
        reportAdapter = new ReportAdapter(this, reportItemArrayList, this);


        //어댑터 객체를 현 액티비티의 리사이클러뷰와 연결시킨다
        rcv.setAdapter(reportAdapter);

        //약속별로 보고서 목록을 저장한다. 이 목록을 저장하는 xml의 이름을 지정한다(저장은 onPause에서, 불러오기는 onCreate에서)
        reportListPreferenceName = goalPreferenceName+";reportList";

        SharedPreferences sp = getSharedPreferences(reportListPreferenceName, MODE_PRIVATE);
        int listSize = sp.getInt("size", 0);

        Log.d("오류찾는다","보고서 목록화면, 목록 사이즈: "+listSize);

        if(listSize>0){
            for(int i=0; i<listSize; i++){
                String itemPreference = sp.getString(goalPreferenceName+";report;"+String.valueOf(i),""); //보고서 별 preference 이름을 불러오기

                SharedPreferences sspp = getSharedPreferences(itemPreference, MODE_PRIVATE);
                String reportTitle = sspp.getString("reportTitle","제목못찾음");

                if(sspp.contains("reportEditTime")){ //수정된 적이 있으면 수정시각을 표시한다
                    String reportEditTime = sspp.getString("reportEditTime","yyMMdd");
                    reportItemArrayList.add(i, new ReportItem(reportTitle, "마지막 수정 : "+reportEditTime,itemPreference));
                }
                else { //수정된 적이 없으면 보고서 최초 작성 시각을 표시한다
                    String reportTime = sspp.getString("reportTime", "yyMMdd");
                    reportItemArrayList.add(i, new ReportItem(reportTitle, reportTime,itemPreference));
                }

                Log.d("오류찾는다","보고서 목록화면, 목록의 "+i+"번 아이템이 채워짐");
            }


        }else{
//            Toast.makeText(this, "리스트 사이즈: 0", Toast.LENGTH_SHORT).show();
        }


        //화면 중앙의 CREATE 버튼을 누르면 -> 보고서 새로 쓰기 화면이 나타난다
        Button new_report = (Button)findViewById(R.id.button5);

        //지금 화면을 보는 사람이 친구라면 -> 보고서 새로 쓰기 버튼을 숨긴다
        if(Main.isFreindMode){
            new_report.setVisibility(View.GONE);
        }

        SharedPreferences shared = getSharedPreferences(goalPreferenceName, MODE_PRIVATE);
        //최종결과가 확정된 이후에는 CREATE 버튼이 사라진다 -- 틀림. 이행기간이 지나면 무조건 사라져야함!!
        if(shared.contains("SuccessOrFailure")){
            new_report.setVisibility(View.GONE);
        }

        new_report.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v){

                Log.d("오류찾는다","보고서 목록화면, 보고서 새로만들기 버튼 누름");

                Intent intent = new Intent(getApplicationContext(),NewReports.class);
                intent.putExtra("goalPreferenceName", goalPreferenceName); //약속이 저장된 xml 이름을 보냄

                Log.d("오류찾는다","보고서 목록화면, 약속이 저장된 xml 이름을 보냄. goalPreferenceName: "+goalPreferenceName);

                intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }

        });


        //홈으로 가기
        ImageButton home = (ImageButton)findViewById(R.id.imageButton9);
        home.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v){

                Main.isFreindMode = false;
                Intent intent = new Intent(getApplicationContext(),Main.class);
                intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.addFlags (Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

        });


    }

    //현재 날짜를 구하기
    public String showDate(){
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd aaa hh:mm");
        String date = format.format(d);
        return date;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("오류찾는다","보고서 목록화면 onResume");

        reportAdapter.notifyDataSetChanged();
        Log.d("오류찾는다","보고서 목록화면- reportAdapter.notifyDataSetChanged()");
        rcv.scrollToPosition(0);
        Log.d("오류찾는다","보고서 목록화면- rcv.scrollToPosition(0)");
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d("오류찾는다","보고서 목록화면 onPause");
        //보고서 목록을 저장하기: 목록에 있는 아이템 개수, 목록 내 아이템의 index, 아이템 별 preference 이름
        SharedPreferences sp = getSharedPreferences(reportListPreferenceName, MODE_PRIVATE); //
        SharedPreferences.Editor editor = sp.edit();

        editor.clear(); //전에 저장했던 데이터를 삭제한다
        Log.d("오류찾는다","목록을 저장하기 시작. 전에 저장했던 데이터를 삭제한다. editor.clear()");

        //비워진 Preference에 현재 데이터를 저장
        editor.putInt("size", reportItemArrayList.size()); //아이템의 개수를 저장
        // 그냥 사이즈가 아니라, 약속명 + 보고서 목록 size 이렇게 가야함

        Log.d("오류찾는다","목록의 크기를 저장. 목록의 크기는 :"+reportItemArrayList.size());

        for(int i=0; i<reportItemArrayList.size(); i++) {
            //(key, value) = (아이템의 index, 보고서별 preference 이름)
            editor.putString(goalPreferenceName+";report;"+String.valueOf(i),reportItemArrayList.get(i).getReportPreferenceName());

            Log.d("오류찾는다","목록의 "+i+"번째 아이템을 저장함");
        }

        editor.commit();
        Log.d("오류찾는다","목록 저장 끝났다");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("오류찾는다","보고서 목록화면 onStop()");
    }

    @Override
    public void onReportClick(int position) {

       //아이템을 클릭하면 어떤 이벤트가 발생하는지? 보고서 내용 확인 화면이 뜸

        Log.d("오류찾는다", "보고서 목록화면 onReportClick()- 클릭한 아이템의 position:"+position);

       Intent intent = new Intent(this, ReportView.class);
       String preferenceName = reportItemArrayList.get(position).getReportPreferenceName(); //각 보고서가 저장된 xml의 이름을 보낸다
       intent.putExtra("goalPreferenceName", goalPreferenceName); // 약속 저장파일 이름도 보낸다(최종결과 확정여부 알기위해)
       intent.putExtra("preferenceName", preferenceName);
       intent.putExtra("itemPosition", String.valueOf(position));// 수정을 대비하여 해당 아이템의 포지션을 함께 보낸다
       intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
       startActivity(intent);

    }


//    @Override
//    public void onLongClick(int position) {
//
//        //롱클릭하면 어떤 이벤트가 발생하는지 - 수정, 삭제
//        Toast.makeText(this, position+"번째 항목이 롱 클릭됨", Toast.LENGTH_SHORT).show();
//    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {

        switch (item.getItemId()){
//            case 121: //수정
//
//                editFlag = 1;
//                //item.getGroupId()= 아이템의 position. 어댑터 클래스에서 메뉴아이템 만들 때 그렇게 설정해놓음
//                String rTitle = reportItemArrayList.get(item.getGroupId()).getReportTitle();
//                int rPosition = item.getGroupId();
//
//                Intent intent = new Intent(this, NewReports.class);
//                intent.putExtra("itemPosition",rPosition);
//                intent.putExtra("title",rTitle);
//                startActivityForResult(intent, REQUEST_EDIT);
//
//                return true;
            case 122:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                View view = LayoutInflater.from(this).inflate(R.layout.activity_delete_dialog, null, false);
                builder.setView(view);

                final Button confirm = (Button)view.findViewById(R.id.button11);
                final Button cancel = (Button)view.findViewById(R.id.button12);

                final AlertDialog dialog = builder.create();

                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reportAdapter.removeItem(item.getGroupId()); //groupId = Position
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

                //삭제
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }


    //시연을 위해 미리 띄워놓은 더미 데이터
//    public void showItems(){
//        ReportItem item = new ReportItem("1일차 보고", "2019-02-27 am 08:30");
//        ReportItem item2 = new ReportItem("2일차 보고", "2019-03-01 pm 10:39");
//        ReportItem item3 = new ReportItem("3일차 보고", "2019-03-17 am 11:20");
//        ReportItem item4 = new ReportItem("4일차 보고", "2019-04-03 pm 07:10");
//
//        reportItemArrayList.add(item);
//        reportItemArrayList.add(item2);
//        reportItemArrayList.add(item3);
//        reportItemArrayList.add(item4);
//    }
}
