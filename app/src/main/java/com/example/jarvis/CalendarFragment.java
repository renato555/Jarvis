package com.example.jarvis;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;

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
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


public class CalendarFragment extends Fragment {
    //TODO lovro premijesti mi ovo
    private static final String CALENDAR_DATA_FILE = "calendarData.ics";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    private Button buttonToday;
    private TextView currentDateView;
    private CalendarView calendarView;
    private LinearLayout eventsLayout;
    private TextView text; //privermeno

    private Date currentDate;

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
        text = (TextView) view.findViewById( R.id.tekstTest);   //privremeno

        downloadDataFromWebsite();
        //loadCalendarData();

        addListeners();

        setUpCurrentDate();
        syncCalendarDate();

        printCurrentDate();
        return view;
    }

    private void downloadDataFromWebsite() {
        //turn on cookies
        CookieHandler.setDefault( new CookieManager()); //cookie store = null,
        //tasks are executed on a single thread. DownloadCalendar will start after Login
        new Login().execute( "https://www.fer.unizg.hr/login/?frompage=%2F&return=%2F");
        new DownloadCalendar().execute( "https://www.fer.unizg.hr/kalendar");
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

    private void loadCalendarData(){
        //TODO wait for it to download and then load
        FileInputStream fin = null;
        do{
            try {
                fin = new FileInputStream( CALENDAR_DATA_FILE);
                CalendarBuilder builder = new CalendarBuilder();
                Calendar calendar = builder.build( fin);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (ParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }while( fin == null);
    }

    class Login extends AsyncTask<String, Void, Void> {

        private final String USER_AGENT = "Mozilla/5.0";
        private final String BOUNDARY = "----WebKitFormBoundary1ALBGLxu9B9GXZ93";

        @Override
        protected Void doInBackground(String... urlString) {

            String urlAuth = urlString[0];

            try {
                //NOTE in getFormParams we assume the names of input elements which might cause issues if they ever get changed
                String postParams = getFormParams( USERNAME, PASSWORD);
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
                            .setTitle( CALENDAR_DATA_FILE)
                            .setDescription( "Downloading " + CALENDAR_DATA_FILE)
                            .setAllowedOverMetered( true)
                            .setAllowedOverRoaming( true)
                            .setNotificationVisibility( DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                            .setDestinationInExternalFilesDir( CalendarFragment.this.getContext(), null, CALENDAR_DATA_FILE)
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

}
