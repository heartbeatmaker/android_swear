package com.example.swearVer2;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.gson.Gson;
//import com.google.gson.JsonObject;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

public class CreateNewGoals extends AppCompatActivity {

//    public class CreateNewGoals extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    static final int RESULT_OK = 1000;

    DatePickerDialog datePickerDialog;
    DatePicker mDate;
    Calendar calendar;
    int year, month, dayOfMonth;
    private int minYear, minMonth, minDay;

    EditText goalTitle, goalDsrp, goalDeposit;
    TextView s_date, e_date;

    String title, description, startDate, endDate;
    int deposit;

    String preferenceName; //약속 별로 preference를 만든다


    public String getData(String key){

        String data = "데이터 없음";

        //현재 사용자의 email(회원 식별자)를 불러온다
        SharedPreferences sp = getSharedPreferences(SignUp.userInfoPreference, MODE_PRIVATE);
        String currentUser = sp.getString("currentUser","현재사용자 불러오기 실패");

        //Preference에서 현재 사용자의 정보(string 형태로 변환된 json객체)를 불러온다
        String userInfo = sp.getString(currentUser, "");
        Log.d("마이페이지", "json 파싱함. userInfo에 있는 정보 전부 출력: "+userInfo);

        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(userInfo);
            Log.d("마이페이지", "json 파싱함. object에 있는 정보 전부 출력: " + jsonObject);

            if(jsonObject.containsKey(key)) {
                data = (String) jsonObject.get(key);
            }

        }catch(Exception e){

        }
        return data;
    }

    public void setData(String key, String newValue){

        SharedPreferences sp = getSharedPreferences(SignUp.userInfoPreference, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        String currentUser = sp.getString("currentUser","현재사용자 불러오기 실패");
        String userInfo = sp.getString(currentUser, "");

        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(userInfo);

            //기존에 해당 키가 있으면 삭제한다
            if(jsonObject.containsKey(key)){
                jsonObject.remove(key);
            }

            //새로운 값을 넣어준다
            jsonObject.put(key, newValue);

            //값을 string 형으로 변환한 후, sharedPreference에 저장한다
            editor.putString(currentUser, jsonObject.toString());
            Log.d("마이페이지", "닉네임 변경함. object에 있는 정보 전부 출력: " + jsonObject);

            editor.commit();

            Map<String, ?> allEntries = sp.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                Log.d("마이페이지", "preference에 있는 데이터 전부출력: "+entry.getKey() + ": " + entry.getValue().toString());
            }



        }catch(Exception e){

        }
    }

    private String getCurrentUser(){
        SharedPreferences infoPref = getSharedPreferences(SignUp.userInfoPreference, MODE_PRIVATE);
        return infoPref.getString("currentUser","유저정보없음");
    }

    public boolean isTitleNull(){

        if (goalTitle.getText().toString().getBytes().length <= 0) {
            Toast.makeText(this, "제목을 입력하세요", Toast.LENGTH_SHORT).show();
            return true;
        }
        else {
            return false;
        }
    }

    public boolean isDsrpNull(){

        if (goalDsrp.getText().toString().getBytes().length <= 0) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean isSdayNull(){

        if (s_date.getText().toString().getBytes().length <= 0) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean isEdayNull(){

        if (e_date.getText().toString().getBytes().length <= 0) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean isDepositNull(){

        if (goalDeposit.getText().toString().getBytes().length <= 0) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean isContentNull(){

        if(isTitleNull() || isDsrpNull() || isSdayNull() || isEdayNull() || isDepositNull()){
            Toast.makeText(this, "내용을 입력하세요", Toast.LENGTH_SHORT).show();
            return true;
        }
        else
            return false;
    }

    public boolean isPointEnough(){

        int currentPoint = Integer.parseInt(getData("point"));
        int typedDeposit = Integer.parseInt(goalDeposit.getText().toString());

        if(typedDeposit >= 100) {
            if (currentPoint >= typedDeposit) {
                return true;
            } else {
                Toast.makeText(this, "포인트가 부족합니다. 현재 포인트: " + currentPoint, Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            Toast.makeText(this, "100 P 이상의 보증금을 걸어야 합니다.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }


    public void setMinYear(int y){
        minYear = y;
    }

    public void setMinMonth(int m){
        minMonth = m;
    }

    public void setMinDay(int d){
        minDay = d;
    }

    public int getMinYear(){
        return minYear;
    }

    public int getMinMonth(){
        return minMonth;
    }

    public int getMinDay(){
        return minDay;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_goals);

        Log.d(this.getClass().getName(), "mylog, onCreate()");

        //글자를 담을 텍스트뷰 지정
        goalTitle = (EditText) findViewById(R.id.editText5); //약속 제목
        goalDsrp = (EditText) findViewById(R.id.editText6); //약속 설명
        s_date = (TextView) findViewById(R.id.textView89); //시작일
        e_date = (TextView) findViewById(R.id.textView90); //종료일
        goalDeposit = (EditText) findViewById(R.id.editText10); //약속 보증금


        //'시작일'글자 밑의 달력 그림을 누르면 -> 달력 팝업창이 뜬다 -> 약속 시작일을 선택한다
        ImageButton s_calendar = findViewById(R.id.start_date_btn);
        s_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance(); //이게 뭐지?
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                datePickerDialog = new DatePickerDialog(CreateNewGoals.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int pickedYear, int pickedMonth, int pickedDay) {

                                //선택 날짜를 연/월/일의 형태로 정리한다
                                // -> 향후 시작일~종료일 사이의 날짜 수 계산을 위해 yyMMdd 형태로 변환하고 싶음(앞으로 할 것@@@@@@@@)

                                int month = pickedMonth + 1;
                                String formattedMonth = ""+ month;
                                String formattedDay = ""+ pickedDay;

                                if(month < 10){
                                    formattedMonth = "0" + month;
                                }else{
                                    formattedMonth = ""+ month;
                                }
                                if(pickedDay < 10){
                                    formattedDay = "0" + pickedDay;
                                }

                                String fullDate = year+"-"+ formattedMonth+ "-" + formattedDay;

                                //선택한 날짜(=시작일)를 달력그림 아래의 텍스트뷰에 넣는다
                                s_date.setText(fullDate);

                                //종료일을 제한하기 위한 메소드
                                // 종료일은 시작일보다 늦어야 하기 때문에, 시작일을 받아서 종료일의 minimum 날짜를 정해놓는다
                                setMinYear(pickedYear);
                                setMinMonth(pickedMonth);
                                setMinDay(pickedDay);

                            }
                        }, year, month, dayOfMonth);

                //오늘날짜부터 선택이 가능하도록 설정. 어제 날짜는 선택 불가
//                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();// 대화상자 띄워주기

            }
        });


        //'종료일'글자 밑의 달력 그림을 누르면 -> 달력 팝업창이 뜬다 -> 약속 종료일을 선택한다
        final ImageButton e_calendar = findViewById(R.id.end_date_btn);
        e_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                year = getMinYear();
                month = getMinMonth();
                dayOfMonth = getMinDay();

                datePickerDialog = new DatePickerDialog(CreateNewGoals.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int pickedYear, int pickedMonth, int pickedDay) {


                                int month = pickedMonth + 1;
                                String formattedMonth = ""+ month;
                                String formattedDay = ""+ pickedDay;

                                if(month < 10){
                                    formattedMonth = "0" + month;
                                }else{
                                    formattedMonth = ""+ month;
                                }
                                if(pickedDay < 10){
                                    formattedDay = "0" + pickedDay;
                                }

                                String fullDate = year+"-"+ formattedMonth+ "-" + formattedDay;

                                e_date.setText(fullDate);

                            }
                        }, year, month, dayOfMonth);

                calendar.set(year,month,dayOfMonth+1); //종료날짜 최소=시작날짜 다음날부터
                datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

                calendar.set(year+1,month,dayOfMonth+1); //종료날짜 최대=현재로부터 1년까지
                datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());

                datePickerDialog.show();

            }
        });


        //사용자가 취소버튼을 눌렀을 때-> 약속이 만들어지지 않고, 화면이 종료된다.
        Button b = (Button) findViewById(R.id.create_cncl_btn);
        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }
        });


        //사용자가 글 작성을 마치고 확인버튼을 눌렀을 때 -> 약속이 생성된다. 약속 내용을 확인하는 화면이 나타난다.
        Button cb = (Button) findViewById(R.id.confirmBtn);
        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!isContentNull()&&isPointEnough()) { //사용자가 빈 칸 없이 모든 값을 입력했는지 확인한다


                    AlertDialog.Builder builder = new AlertDialog.Builder(CreateNewGoals.this);
                    View v = LayoutInflater.from(CreateNewGoals.this).inflate(R.layout.activity_confirm_dialog, null, false);
                    builder.setView(v);

                    final Button confirm = (Button)v.findViewById(R.id.confirm_dialog_confirmBtn);
                    final Button cancel = (Button)v.findViewById(R.id.confirm_dialog_cancelBtn);

                    final AlertDialog dialog = builder.create();

                    //확인을 눌렀을 때 -> 내용 저장, 메인화면의 약속목록에 아이템 추가
                    confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Log.d("알람", "약속 확인버튼 누름");

                            //포인트 차감
                            int currentPoint = Integer.parseInt(getData("point"));
                            int typedDeposit = Integer.parseInt(goalDeposit.getText().toString());

                            currentPoint -= typedDeposit;
                            setData("point", String.valueOf(currentPoint));


                            int goalNumber; // 약속 별 preference를 구별하는 중요한 지표. 반드시 저장해야함

                            //goalNumber 지정하기
                            SharedPreferences sp = getSharedPreferences(getCurrentUser()+";goalNumber", MODE_PRIVATE);
                            if(sp.contains("goalNumber")) { //기존에 저장한 값이 있으면 -> 그것을 불러옴
                                goalNumber = sp.getInt("goalNumber", 0);
                            }
                            else{
                                goalNumber = 0; //기존에 저장한 값이 없으면(앱 최초실행) -> 0부터 시작
                            }

                            //약속 별 preference 이름
                            preferenceName = getCurrentUser()+";goal;"+Integer.toString(goalNumber);

                            //약속의 내용을 새로운 preference에 저장한다
                            saveThisGoal();

                            goalNumber++; //약속 1 -> 약속 2 -> 약속 3... 다음 약속을 저장하는 Preference를 다르게 하기 위해

                            //goalNumber를 저장하기
                            // 저장하지 않으면 -> 화면이 켜질 때마다 이 변수가 한 값으로 초기화됨
                            //-> goalNumber 중복. 서로 다른 약속이 하나의 넘버를 공유함
                            SharedPreferences sspp = getSharedPreferences(getCurrentUser()+";goalNumber", MODE_PRIVATE);; //이 액티비티에서만 사용하는 변수이므로, getPreference로 선언
                            SharedPreferences.Editor editor = sspp.edit();
                            editor.putInt("goalNumber", goalNumber);
                            editor.commit();

                            //메인창의 목록에 이 약속을 추가하기
                            Main.itemArrayList.add(0,new Item(0, title, startDate+"~"+endDate, preferenceName));
                            Main.mainAdapter.notifyDataSetChanged();
                            Log.d("알람", "Main.mainAdapter.notifyDataSetChanged();");
                            Log.d("알람", "아이템 개수: "+Main.mainAdapter.getItemCount());


                            //메인화면의 목록(itemArrayList)을 Gson으로 저장
//                            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(CreateNewGoals.this);
//                            SharedPreferences.Editor pref_editor = pref.edit();
//                            Gson gson = new Gson();
//                            String json = gson.toJson(Main.itemArrayList);
//                            pref_editor.putString("itemArrayList", json);
//                            pref_editor.commit();


                            //약속 목록을 저장하기: 목록에 있는 아이템 개수, 목록 내 아이템의 index, 아이템 별 preference 이름
                            SharedPreferences pref = getSharedPreferences(getCurrentUser()+";goalList", MODE_PRIVATE);
                            SharedPreferences.Editor pref_editor = pref.edit();

                            pref_editor.clear(); //전에 저장했던 데이터를 삭제한다

                            //비워진 Preference에 현재 데이터를 저장
                            pref_editor.putInt("size", Main.itemArrayList.size()); //아이템의 개수를 저장

                            for(int i=0; i<Main.itemArrayList.size(); i++) {
                                //(key, value) = (아이템의 index, 약속별 preference 이름)
                                pref_editor.putString(String.valueOf(i), Main.itemArrayList.get(i).getPreferenceName());
                            }

                            pref_editor.commit();


                            Toast.makeText(CreateNewGoals.this, "약속이 확정되었습니다.", Toast.LENGTH_SHORT).show();

                            Intent confirm = new Intent(getApplicationContext(), CheckGoals.class); //약속 확인창으로 가기
                            confirm.putExtra("preferenceName", preferenceName); //데이터를 저장한 preference의 이름을 함께 보내기
                            startActivity(confirm);

                            //@@@종료일에 맞추어 알람을 setting하는 작업이 여기서 필요함
                            setNotification();

                            finish();


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

                }
            }

        });

    }//onCreate() 닫음


    //약속 목록 위의 아이템 하나마다 preference xml 파일이 하나 있어야함
    //사용자가 create 창에서 '완료'버튼을 누를 때, 새로운 xml 파일+약속 내용이 저장되어야함

    //사용자가 입력한 약속의 내용을, Shared Preference에 저장
    public void saveThisGoal(){

        //사용자가 입력한 값을 추출한다
        title = goalTitle.getText().toString();
        description = goalDsrp.getText().toString();
        startDate = s_date.getText().toString();
        endDate = e_date.getText().toString();
        int numberOfDays = calDateBetweenDates();
        deposit = Integer.parseInt(goalDeposit.getText().toString());
        String currentTime = getCurrentTime();

        //추출한 값을 저장한다
        SharedPreferences sp = getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("goalTitle", title);
        editor.putString("goalDescription", description);
        editor.putString("goalStartDate", startDate); //시작일, 종료일은 yy/M/dd 형식의 String임
        editor.putString("goalEndDate", endDate);
        editor.putInt("numberOfDays", numberOfDays);
        editor.putInt("goalDeposit", deposit);
        editor.putString("contractDate", currentTime); //약속체결일 저장
        editor.putBoolean("isLocked", false); //비밀글 여부 저장 - 공개가 디폴트값. 약속 확인 창에서 변경 가능
        editor.commit();

    }


    //현재 날짜, 시간을 구하기
    public String getCurrentTime(){

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd aaa hh:mm");
        String date = format.format(System.currentTimeMillis());
        return date;
    }


    //두 날짜 사이의 차이 계산
    public int calDateBetweenDates(){

        int result = 0;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date formattedStartDate = format.parse(startDate);
            Date formattedEndDate = format.parse(endDate);

            long calDate = formattedStartDate.getTime() - formattedEndDate.getTime();
            long days = Math.abs(calDate / (24 * 60 * 60 * 1000));
            result = (int)days;

        }catch (Exception e){
//            StringWriter sw = new StringWriter();
//            e.printStackTrace(new PrintWriter(sw));
//            String ex = sw.toString();
//
//            Log.d("날짜계산 오류",ex);
        }

        return result;
    }



    @Override
    public void onStart(){
        super.onStart();
        Log.d(this.getClass().getName(), "mylog, onStart()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(this.getClass().getName(), "mylog, onStop()");

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(this.getClass().getName(), "mylog, onDestroy()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(this.getClass().getName(), "mylog, onPause()");

//        Intent adInsert = new Intent(this, AdInsertion.class);
//
//        if(Main.count%3==0) {
//            startActivity(adInsert);
//            Log.d(this.getClass().getName(), "Ad Inserted");
//        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(this.getClass().getName(), "mylog, onResume()");
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(this.getClass().getName(), "mylog, onRestart()");

    }



    public void setAlarm(){

        Log.d("알람", "setAlarm 들어옴");
        Long alertTime = new GregorianCalendar().getTimeInMillis()+5*1000;
        Intent alertIntent = new Intent(this, OriginAlarmReceiver.class);
        alertIntent.putExtra("preferenceName", preferenceName);
        alertIntent.putExtra("goalTitle", goalTitle.getText().toString());
        alertIntent.putExtra("endDate", endDate);

        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, alertTime, PendingIntent.getBroadcast(
                this,1, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        Log.d("알람", "알람 set 완료");
    }

    public void setNotification(){
        Log.d("알람", "setNotification 들어옴");

        long millis = 0;

        try {
            String theDay = endDate + " 08:00:00";
            Log.d("알람", "종료일의 시간을 지정: "+theDay);

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = format.parse(theDay);
            millis = date.getTime();
        }catch (Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String ex = sw.toString();

            Log.d("알람", ex);
        }

        Intent alertIntent = new Intent(this, AlarmReceiver.class);

        alertIntent.putExtra("userEmail", getCurrentUser());
        alertIntent.putExtra("goalTitle", goalTitle.getText().toString());
//        alertIntent.putExtra("endDate", endDate);
        alertIntent.putExtra("preferenceName", preferenceName); //알림메시지를 눌렀을 때 해당 화면으로 이동하기 위해

        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, millis, PendingIntent.getBroadcast(
                this,1, alertIntent, PendingIntent.FLAG_ONE_SHOT));

        Log.d("알람", "알람 set 완료");
    }

}