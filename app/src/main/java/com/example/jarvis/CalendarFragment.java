package com.example.jarvis;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;


public class CalendarFragment extends Fragment {
    //TODO lovro premijesti mi ovo
    private static final String CALENDAR_DATA_FILE = "calendarData.ics";
    private static final String USERNAME = "rj52171";
    private static final String PASSWORD = "92564161";

    private Button buttonToday;
    private TextView currentDateView;
    private CalendarView calendarView;
    private LinearLayout eventsLayout;

    private List<String> cookies;
    private HttpsURLConnection conn;

    private Map<String, List<String>> calendarData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        //link views
        buttonToday = (Button) view.findViewById( R.id.buttonToday);
        currentDateView = (TextView) view.findViewById( R.id.currentDateView);
        calendarView = (CalendarView) view.findViewById( R.id.calendarView);
        eventsLayout = (LinearLayout) view.findViewById( R.id.eventsLayout);

        //downloadDataFromWebsite();
        calendarData = new HashMap<>();
        loadCalendarData();

        addListeners();

        printDate( new Date());
        return view;
    }

    private void downloadDataFromWebsite() {
        //turn on cookies
        CookieHandler.setDefault( new CookieManager()); //cookie store = null,
        //tasks are executed on a single thread. DownloadCalendar will start after Login
        new Login().execute( "https://www.fer.unizg.hr/login/?frompage=%2F&return=%2F");
        new DownloadCalendar().execute( "https://www.fer.unizg.hr/kalendar");
    }

    private void loadCalendarData(){
        try {
            FileInputStream fin = new FileInputStream( getContext().getExternalFilesDir( null) + "/" + CALENDAR_DATA_FILE);

            String lines = readStream( fin);
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
        currDate.add( timeFromTo + ":" + summ);
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


    private void loadCalendarData() {
        //TODO wait for it to download and then load
        FileInputStream fin = null;
        do {
            try {
                fin = new FileInputStream(Constants.CALENDAR_DATA_FILE);
                CalendarBuilder builder = new CalendarBuilder();
                Calendar calendar = builder.build(fin);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (ParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (fin == null);
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

    class Login extends AsyncTask<String, Void, Void> {

        private final String USER_AGENT = "Mozilla/5.0";
        private final String BOUNDARY = "----WebKitFormBoundary1ALBGLxu9B9GXZ93";

        @Override
        protected Void doInBackground(String... urlString) {

            String urlAuth = urlString[0];

            try {
                //NOTE in getFormParams we assume the names of input elements which might cause issues if they ever get changed
                String postParams = getFormParams( Constants.USERNAME, Constants.PASSWORD);
                //construct above post's contentn and then send a POST request for authentication
                sendPost( urlAuth, postParams);
                return null;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }catch(Exception e){
                e.printStackTrace();
            }

            return null;
        }

        private String getFormParams(String username, String password) throws UnsupportedEncodingException {

            String result = "--" +  BOUNDARY + "\n" +
                    "Content-Disposition: form-data; name=\"username\"\n" + //assume name = username
                    "\n" +
                    username + "\n" +
                    "--" + BOUNDARY +"\n" +
                    "Content-Disposition: form-data; name=\"password\"\n" + //assume name = password
                    "\n" +
                     password+"\n" +
                    "--" + BOUNDARY + "--";
            return result;
        }

        private void sendPost(String urlAuth, String postParams) throws IOException {
            URL url = new URL( urlAuth);
            conn = (HttpsURLConnection) url.openConnection();

            //Acts like a browser
            conn.setUseCaches( false);
            conn.setRequestMethod( "POST");
            conn.setRequestProperty("Host", "www.fer.unizg.hr");
            conn.setRequestProperty("User-Agent", USER_AGENT);
            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
            conn.setRequestProperty("Accept-Language", "en-US,en;q=0.9");
            if( cookies != null){
                for( String cookie : cookies){
                    conn.addRequestProperty( "Cookie", cookie.split(";", 1)[0]);
                }
            }
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("Referer", urlAuth);
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundary1ALBGLxu9B9GXZ93");
            conn.setRequestProperty("Content-Length", Integer.toString(postParams.length()));

            conn.setDoOutput(true);
            conn.setDoInput(true);
            // Send post request
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();

            setCookies(conn.getHeaderFields().get("Set-Cookie"));
        }

        private void setCookies( List<String> keksi){
            cookies = keksi;
        }

    }

    class DownloadCalendar extends AsyncTask<String, Void, Void>{

        private final String USER_AGENT = "Mozilla/5.0";

        @Override
        protected Void doInBackground(String... strings) {
            try {
                String calendarPage = getPageContent( strings[0]);
                Document doc = Jsoup.parse( calendarPage);
                Elements el = doc.getElementsByTag( "a");
                Element downloadLink = el.get( 40); // Preuzmi u iCal formatu je na indexu 40

                String url = downloadLink.attr( "href");
                //TODO trenutno se skida svaki put kada se pokrene taj fragment... napraviti da se azurira periodicno offiline
                downloadFile( url);


            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        private String getPageContent(String urlCurr) throws IOException {
            URL url = new URL( urlCurr);
            conn = (HttpsURLConnection) url.openConnection();
            //act like a browser
            conn.setRequestMethod( "GET");
            conn.setUseCaches( false);
            conn.setRequestProperty( "UserAgent", USER_AGENT);
            conn.setRequestProperty( "Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
            conn.setRequestProperty( "Accept-Language", "en-US,en;q=0.9");
            if (cookies != null) {
                for (String cookie : cookies) {
                    conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
                }
            }
            int responseCode = conn.getResponseCode();
            String response = readStream( conn.getInputStream());
            setCookies( conn.getHeaderFields().get( "Set-Cookie"));
            return response;
        }

        private void setCookies( List<String> keksi){
            cookies = keksi;
        }

        private void downloadFile(String url) {
            Uri downloadURI = Uri.parse( url);
            DownloadManager manager = ( DownloadManager) ( CalendarFragment.this).getContext().getSystemService( Context.DOWNLOAD_SERVICE);
            try{
                if( manager != null){
                    DownloadManager.Request request = new DownloadManager.Request( downloadURI);
                    request.setAllowedNetworkTypes( DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                            .setTitle( Constants.CALENDAR_DATA_FILE)
                            .setDescription( "Downloading " + Constants.CALENDAR_DATA_FILE)
                            .setAllowedOverMetered( true)
                            .setAllowedOverRoaming( true)
                            .setNotificationVisibility( DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                            .setDestinationInExternalFilesDir( CalendarFragment.this.getContext(), null, Constants.CALENDAR_DATA_FILE)
                            .setMimeType( getMimeType( downloadURI));
                    manager.enqueue( request);
                }else{
                    Intent intent = new Intent(Intent.ACTION_VIEW, downloadURI);
                    startActivity( intent);
                }
            }catch ( Exception e){
                e.printStackTrace();
            }
        }

        private String getMimeType( Uri uri){
            ContentResolver resolver = CalendarFragment.this.getContext().getContentResolver();
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            return mimeTypeMap.getExtensionFromMimeType( resolver.getType( uri));
        }
    }

    private String readStream( InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;
        try{
            reader = new BufferedReader( new InputStreamReader( in));
            String nextLine = "";
            while( (nextLine = reader.readLine()) != null){
                sb.append( nextLine + "\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            if( reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        in.close();
        return sb.toString();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Destroy");
    }

}
