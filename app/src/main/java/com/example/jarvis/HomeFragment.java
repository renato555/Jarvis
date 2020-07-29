package com.example.jarvis;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class HomeFragment extends Fragment {

    private List<String> allTasks;
    private LinearLayout taskLayout;
    private TextView welcomeText;
    private Button dontPressMeButton;
    private Map<String, List<String>> calendarData;
    private LinearLayout todayCalendarLayout;

    private OnSwipeTouchListener swipeListener;

    public HomeFragment( OnSwipeTouchListener swipeListener){
        this.swipeListener = swipeListener;
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
        welcomeText = (TextView) view.findViewById(R.id.weclomeText);
        todayCalendarLayout = (LinearLayout) view.findViewById(R.id.todayCalendar_layout);

        dontPressMeButton = (Button) view.findViewById(R.id.dontPressMe);
        dontPressMeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), DontPressMeActivity.class);
                startActivity(intent);
            }
        });

        setUpWelcomeText();
        loadAllTasks();
        loadTodayCalendar();
        return view;
    }

    private void setUpWelcomeText() {
        welcomeText.setText("Welcome, " + Constants.NAME);
    }

    public void loadAllTasks() {
        Map<String, List<String>> todoTasks;
        try {
            FileInputStream fis = getContext().openFileInput(Constants.TODO_DATABASE_FILE);
            ObjectInputStream oi = new ObjectInputStream(fis);

            todoTasks = (Map<String, List<String>>) oi.readObject();
            fis.close();
            oi.close();
            allTasks = todoTasks.get(Constants.ALL_TASKS);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (allTasks != null)
            printTasks();

    }

    private void printTasks(){
        taskLayout.removeAllViews();
        List<String> currentTasks = allTasks;
        for (String currentText : currentTasks) {
            TextView task = new TextView(getContext());
            setTextViewParams(task, currentText);
            taskLayout.addView(task);
        }
    }

    public void setTextViewParams (TextView task, String currentText){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(25, 7, 0, 7);
        task.setLayoutParams(params);
        task.setText(currentText);
        task.setTextSize(TypedValue.COMPLEX_UNIT_PX, 50);
        task.setTypeface(null, Typeface.BOLD);
    }


    private void loadTodayCalendar() {
        Date dateObj = Calendar.getInstance().getTime();

        readCalendarEvents();
        printDate(dateObj);
    }

    private void readCalendarEvents() {
        try{
            FileInputStream fis = getContext().openFileInput(Constants.CALENDAR_EVENTS_FILE);
            ObjectInputStream oi = new ObjectInputStream(fis);

            calendarData = (Map<String, List<String>>) oi.readObject();

            oi.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void printDate( Date date) {
        //updates todays events
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String todayDate = df.format(date);
        List<String> events = calendarData.get( todayDate);
        todayCalendarLayout.removeAllViews();
        if( events != null){
            for( String event : events){
                TextView eventView = makeTextView( event);
                todayCalendarLayout.addView( eventView);
            }
        }
    }

    private TextView makeTextView( String event){
        TextView textView = new TextView( getContext());
        textView.setText( event);
       // textView.setBackgroundResource( R.drawable.bottom_edge);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
        //set textView margin
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(25, 7, 25, 7);
        textView.setLayoutParams( params);

        return textView;
    }

    @Override
    public void onPause() {
        Log.i("Test", "Pause");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Destroy");
    }

    public void disableButtons(){
        dontPressMeButton.setEnabled(false);
    }

    public void enableButtons(){
        dontPressMeButton.setEnabled(true);
    }
}


