package com.example.swearVer2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

public class NewGoalsWithCalendar extends AppCompatActivity {

    int year, month, date;
    private Context context;
    TextView main_label;

//    public NewGoalsWithCalendar(Context context){
//        this.context = context;
//    }
//
    public void setYear(int y){
        this.year = y;
    }

    public void setMonth(int m){
        this.month = m;
    }

    public void setDate(int d){
        this.date = d;
    }

    public int getYear(){
        return this.year;
    }

    public int getMonth(){
        return this.month;
    }

    public int getDate(){
        return this.date;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_goals_with_calendar);

        CalendarView cal = (CalendarView) findViewById(R.id.calendarView3);

        Toast.makeText(this, "날짜를 선택하세요", Toast.LENGTH_SHORT).show();


        cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                final String message = year+"/"+month+"/"+dayOfMonth;

                setYear(year);
                setMonth(month);
                setDate(dayOfMonth);

                Toast.makeText(NewGoalsWithCalendar.this, message, Toast.LENGTH_SHORT).show();


            }
        });



        Button confirm = (Button)findViewById(R.id.cal_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v){

                Intent e = new Intent(getApplicationContext(), CreateNewGoals.class);

                finish();

            }

        });


        Button cancel = (Button)findViewById(R.id.cal_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v){
                finish();

            }

        });


    }





}
