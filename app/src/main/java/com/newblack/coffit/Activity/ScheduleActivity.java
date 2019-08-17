package com.newblack.coffit.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.newblack.coffit.APIClient;
import com.newblack.coffit.APIInterface;
import com.newblack.coffit.Adapter.ScheduleAdapter;
import com.newblack.coffit.Data.Schedule;
import com.newblack.coffit.Data.TrainerSchedule;
import com.newblack.coffit.DateUtils;
import com.newblack.coffit.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView tv_today;
    MaterialCalendarView calendar;
    List<Schedule> scheduleList;
    List<Schedule> todayList;
    String today;
    String today_object;
    String selectedDay;
    int studentId;

    RecyclerView recyclerView;
    ScheduleAdapter adapter;
    Activity activity;
    APIInterface apiInterface;
    SharedPreferences sp;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("PT일정관리");
        tv_today = findViewById(R.id.tv_today);
        activity = this;

        sp = getSharedPreferences("coffit",MODE_PRIVATE);
        studentId = sp.getInt("student_id",0);


        calendar = findViewById(R.id.calendar);
        calendar.setSelectedDate(CalendarDay.today());
        today_object = dateObject(CalendarDay.today());
        selectedDay = today_object;
        today = dateFormat(CalendarDay.today());
        tv_today.setText(today);
        scheduleList = new ArrayList<>();
        todayList = new ArrayList<>();
        //여기서 처음 한번 retrofit 돌려서 전체 스케쥴 받아오기!! 굳이 여러번 돌리지 맙시다
        retrofit_getSchedule();


        recyclerView = findViewById(R.id.rv_schedule);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ScheduleAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new ScheduleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                //여기서 스케줄 수정용 액티비티 띄우기
                //액티비티 띄우는게 맞는 것 같아서 일단 다시 구현
                Log.d("TAG","list 길이 : " + todayList);
                Schedule schedule = todayList.get(position);
                Intent intent = new Intent(activity,ScheduleDialogActivity.class);
                intent.putExtra("schedule",schedule);
                startActivity(intent);
            }
        });


        calendar.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView materialCalendarView, @NonNull CalendarDay date, boolean b) {
                selectedDay = dateObject(date);
                today = dateFormat(date);
                tv_today.setText(today);
                todayList = getTodaySchedule(date,scheduleList);
                adapter.setSchedules(todayList);

            }
        });

    }

    public void newPT(View view){
        Intent intent = new Intent(this, AddScheduleActivity.class);
        intent.putExtra("date",selectedDay);
        Log.d("TAG","selectedDay : " + selectedDay);
        startActivity(intent);
    }


    public void retrofit_getSchedule(){
        //스케줄 받아서 표시하는 부분
        apiInterface = APIClient.getClient().create(APIInterface.class);

        Call<List<Schedule>> call = apiInterface.getSchedule(studentId);
        call.enqueue(new Callback<List<Schedule>>(){
            @Override
            public void onResponse(Call<List<Schedule>> call, Response<List<Schedule>> response){
                Log.d("TAG", "apiInterface callback onResponse");
                List<Schedule> schedules = response.body();
                scheduleList.addAll(schedules);
                Log.d("TAG","schedulelist size : " +scheduleList.size());

                Collections.sort(scheduleList, new Comparator<Schedule>(){
                    public int compare(Schedule s1, Schedule s2){
                        return s1.getDate().compareTo(s2.getDate());
                    }
                });

                //need to make count later
                for(int i = 1; i< scheduleList.size(); i++){
                    Schedule sc = scheduleList.get(i-1);
                    sc.setCount(i);
                    Log.d("TAG" , "id " + sc.getId()+ " time " +sc.getDate() + " count : "+ sc.getCount());
                }
                //초기 화면만 설정
                todayList = getTodaySchedule(CalendarDay.today(),scheduleList);
                adapter.setSchedules(todayList);
            }

            @Override
            public void onFailure(Call<List<Schedule>> call, Throwable t) {
                t.printStackTrace();
                Log.d("TAG", "통신 실패");
            }
        });
    }



    //오늘 날짜의 스케줄만 가져오는 용도
    public List<Schedule> getTodaySchedule(CalendarDay day, List<Schedule> schedules ){
        List<Schedule> result = new ArrayList<>();
        CalendarDay selected;
        for (Schedule schedule : schedules){
            Date date = schedule.getDate();
            selected = DateUtils.getCalendarDay(date);
            if(selected.equals(day)){
                //날짜 같을때 추가
                result.add(schedule);
            }
        }
        return result;
    }


    public static String dateFormat(CalendarDay date){
        String day = date.getYear() +"년 " + date.getMonth()+"월 " + date.getDay()+"일  ";
        Log.d("TAG","date Format : "+ day);
        return day;
    }

    public static String dateObject(CalendarDay date){
        String day = date.getYear()+"-"+date.getMonth()+"-"+date.getDay();
        return day;
    }


}
