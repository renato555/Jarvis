package com.example.jarvis;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

//TODO u mainu je potreno periodicno updejtat CALENDAR_DATA_FILE
public class CalendarFragment extends Fragment {

    private Button buttonToday;
    private TextView currentDateView;
    private CalendarView calendarView;
    private LinearLayout eventsLayout;

    private Map<String, List<String>> calendarData;

    private OnSwipeTouchListener swipeListener;

    public CalendarFragment( OnSwipeTouchListener swipeListener){
        this.swipeListener = swipeListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        //link views
        buttonToday = (Button) view.findViewById( R.id.buttonToday);
        currentDateView = (TextView) view.findViewById( R.id.currentDateView);
        calendarView = (CalendarView) view.findViewById( R.id.calendarView);
        eventsLayout = (LinearLayout) view.findViewById( R.id.eventsLayout);

        calendarData = new HashMap<>();
        loadCalendarData();

        addListeners();

        printDate( new Date());

        writeCalendarData();
        return view;
    }

    private void loadCalendarData(){
        try {
            FileInputStream fin = new FileInputStream( getContext().getExternalFilesDir( null) + "/" + Constants.CALENDAR_DATA_FILE);

            String lines = ConnectionWithWebsite.readStream( fin);
            addEvents( lines);
            fin.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //ASSUMED FORMAT:
    //BEGIN:VEVENT
    //DTSTART;TZID=Europe/Zagreb:20200703T113000
    //DTEND;TZID=Europe/Zagreb:20200703T140000
    //SUMMARY:Menadžment u inženjerstvu - Završni pismeni (A202) [A-202]
    //...
    //END:VEVENT
    private void addEvents(String lines) {
        Scanner scanner = new Scanner( lines);
        while( scanner.hasNext()){
            String line = scanner.nextLine();
            if( line.equals( "BEGIN:VEVENT")){
                String startTime = scanner.nextLine();
                String endTime = scanner.nextLine();
                String summary = scanner.nextLine();
                addEvent( startTime, endTime, summary);
            }
        }
    }

    private void addEvent(String startTime, String endTime, String summary){
        String date = startTime.substring( startTime.length() - 15, startTime.length() - 7); //example: 25.12.2020. is encoded like 20201225
        String timeFromTo = startTime.substring( startTime.length() - 6, startTime.length() - 4) + ":" + startTime.substring( startTime.length() - 4, startTime.length() - 2) +
                "-" + endTime.substring( endTime.length() - 6, endTime.length() - 4) + ":" + endTime.substring( endTime.length() - 4, endTime.length() - 2); //example: turn 112000123000 to 11:20-12:30
        String summ = summary.substring( 8);

        calendarData.putIfAbsent( date, new ArrayList<>());
        List<String> currDate = calendarData.get( date);
        currDate.add( timeFromTo + ": " + summ);
    }

    private void addListeners() {
        buttonToday.setOnClickListener( (View v) -> {
            Date currDate = new Date();
            calendarView.setDate( currDate.getTime());
            printDate( currDate);
        });

        calendarView.setOnDateChangeListener( (CalendarView view, int year, int month, int dayOfMonth) -> {
            printDate( new GregorianCalendar( year, month, dayOfMonth).getTime());
        });

        eventsLayout.setOnTouchListener( swipeListener);
    }

    private void printDate( Date date) {
        //updates top bar text
        DateFormat dateFormat = new SimpleDateFormat( getString( R.string.dateFormat));
        currentDateView.setText( dateFormat.format( date));

        //updates todays events
        SimpleDateFormat formatKey = new SimpleDateFormat("yyyyMMdd");
        String dateKey = formatKey.format( date);
        List<String> events = calendarData.get( dateKey);
        eventsLayout.removeAllViews();
        if( events != null){
            for( String event : events){
                TextView eventView = makeTextView( event);
                eventsLayout.addView( eventView);
            }
        }
    }

    private TextView makeTextView( String event){
        TextView textView = new TextView( getContext());
        textView.setText( event);
        textView.setBackgroundResource( R.drawable.bottom_edge);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
        //set textView margin
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 15, 0, 0);
        textView.setLayoutParams( params);

        return textView;
    }

    private void writeCalendarData() {
        try {
            FileOutputStream fos = getContext().openFileOutput(Constants.CALENDAR_EVENTS_FILE, Context.MODE_PRIVATE);
            ObjectOutputStream oi = new ObjectOutputStream(fos);

            oi.writeObject(calendarData);
            oi.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
