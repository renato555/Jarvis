package com.example.jarvis;


import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class HomeFragment extends Fragment {

    private ToDoListFragment toDoListFragment;
    private CalendarFragment calendarFragment;

    private List<String> allTasks;
    private List<String> todayEvents;
    private LinearLayout taskLayout;
    private TextView welcomeText;
    private LinearLayout todayCalendarLayout;

    private OnSwipeTouchListener swipeListener;


    public HomeFragment(OnSwipeTouchListener swipeListener, ToDoListFragment toDoListFragment, CalendarFragment calendarFragment) {
        this.swipeListener = swipeListener;
        this.toDoListFragment = toDoListFragment;
        this.calendarFragment = calendarFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @SuppressLint("ResourceType")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        taskLayout = (LinearLayout) view.findViewById(R.id.tasks_layout);
        welcomeText = (TextView) view.findViewById(R.id.welcomeText);
        todayCalendarLayout = (LinearLayout) view.findViewById(R.id.todayCalendar_layout);
        
        setUpWelcomeText( ConnectionWithWebsite.getUserFullName().split( "\\s+")[0]); //only firstname gets displayed
        printTasks();
        loadTodayCalendar();

        setUpScrollViewListners( view);

        return view;
    }

    private void setUpWelcomeText( String name) {
        welcomeText.setText("Welcome, " + name);
    }

    public void printTasks(){
        allTasks = toDoListFragment.getAllTasks();
        taskLayout.removeAllViews();
        for (String currentText : allTasks) {
            TextView task = new TextView(getContext());
            setTasksTextViewParams(task, currentText);
            taskLayout.addView(task);
        }
    }

    public void setTasksTextViewParams(TextView task, String currentText){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(25, 7, 0, 7);
        task.setLayoutParams(params);
        task.setText(currentText);
        task.setBackgroundResource( R.drawable.bottom_edge);
        task.setTextSize(TypedValue.COMPLEX_UNIT_PX, 50);
        task.setTypeface(null, Typeface.BOLD);
        task.setTextColor(getResources().getColor(R.color.textColorLight));
    }

    private void loadTodayCalendar() {
        Date dateObj = Calendar.getInstance().getTime();
        printDate(dateObj);
    }

    private void printDate( Date date) {
        //updates todays events
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String todayDate = df.format(date);
        todayEvents = calendarFragment.getTodayEvents();
        todayCalendarLayout.removeAllViews();
        if( todayEvents != null){
            for( String event : todayEvents){
                TextView eventView = makeCalendarTextView( event);
                todayCalendarLayout.addView( eventView);
            }
        }else{
            TextView nothingTodayView = new TextView(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(25, 7, 25, 7);
            nothingTodayView.setLayoutParams(params);
            nothingTodayView.setText(getResources().getString(R.string.nothingTodayOnCalendar));
            nothingTodayView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
            //nothingTodayView.setTextColor(getResources().getColor(R.color.black));
            todayCalendarLayout.setGravity(17);
            todayCalendarLayout.addView(nothingTodayView);
        }
    }

    private TextView makeCalendarTextView(String event){
        TextView textView = new TextView( getContext());
        textView.setText( event);
        textView.setBackgroundResource( R.drawable.bottom_edge);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
        textView.setTextColor(getResources().getColor(R.color.textColorLight));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(25, 7, 25, 7);
        textView.setLayoutParams( params);

        return textView;
    }

    private void setUpScrollViewListners( View view){
        ScrollView scroll1 = (ScrollView) view.findViewById( R.id.tasks_scroll);
        scroll1.setOnTouchListener( swipeListener);

        ScrollView scroll2 = (ScrollView) view.findViewById( R.id.todayCalendar_scroll);
        scroll2.setOnTouchListener( swipeListener);
    }
}


