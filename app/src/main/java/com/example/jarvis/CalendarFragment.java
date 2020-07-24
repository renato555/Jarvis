package com.example.jarvis;

import android.annotation.SuppressLint;
import android.content.ReceiverCallNotAllowedException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CalendarFragment extends Fragment {

    private Button buttonToday;
    private TextView currentDateView;
    private CalendarView calendarView;

    private Date currentDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        //link views
        buttonToday = (Button) view.findViewById( R.id.buttonToday);
        currentDateView = (TextView) view.findViewById( R.id.currentDateView);
        calendarView = (CalendarView) view.findViewById( R.id.calendarView);

        addListeners();

        setUpCurrentDate();
        syncCalendarDate();

        printCurrentDate();
        return view;
    }

    private void addListeners() {
        buttonToday.setOnClickListener( (View v) -> {
            currentDate = new Date();
            syncCalendarDate();
            printCurrentDate();
        });

        calendarView.setOnDateChangeListener( (CalendarView view, int year, int month, int dayOfMonth) -> {
            currentDate = new Date( year, month, dayOfMonth);
            printCurrentDate();
        });
    }

    private void setUpCurrentDate() {
        if( currentDate != null) return;

        currentDate = new Date();
    }

    private void syncCalendarDate() {
        calendarView.setDate( currentDate.getTime());
    }

    private void printCurrentDate() {
        //updates top bar text
        DateFormat dateFormat = new SimpleDateFormat( getString( R.string.dateFormat));
        currentDateView.setText( dateFormat.format( currentDate));

        //updates todays events
    }


}
