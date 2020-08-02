package com.example.jarvis;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.webkit.MimeTypeMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

public class ConnectionWithWebsite {

    private static List<String> cookies;
    private static HttpsURLConnection conn;

    //______________________________________________________________________________________
    public static boolean tryLogin( Context context, String username, String password){
        loadCookies( context, username, password);
        //first login, no saved data was found
        if( cookies == null) return tryNewLogin( context, username, password);

        //check if cookies are expired
        String expireDateString = cookies.get(0).split( ";")[1].substring( " expiers=".length());
        LocalDateTime expireDate = LocalDateTime.parse( expireDateString, DateTimeFormatter.ofPattern( "EEE, dd-MMM-yyyy HH:mm:ss zzz"));
        if( expireDate.isBefore( LocalDateTime.now())) return tryNewLogin( context, username, password);

        //cookies are not expired
        return true;
    }

    private static boolean tryNewLogin( Context context, String username, String password){
        boolean result = false;
        try {
            result = new Login().execute( "https://www.fer.unizg.hr/login/?frompage=%2F&return=%2F", username, password).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if( result) writeCookies(context, username, password);
        return result;
    }

    private static class Login extends AsyncTask<String, Void, Boolean> {
        private final String USER_AGENT = "Mozilla/5.0";
        private final String BOUNDARY = "----WebKitFormBoundary1ALBGLxu9B9GXZ93";

        @Override
        protected Boolean doInBackground(String... urlString) {

            String urlAuth = urlString[0];
            String username = urlString[1];
            String password = urlString[2];
            try {
                //NOTE in getFormParams we assume the names of input elements which might cause issues if they ever get changed
                String postParams = getFormParams( username, password);
                //construct above post's contentn and then send a POST request for authentication
                int responseCode = sendPost( urlAuth, postParams);
                return responseCode == 302; // 302 means that the username was found
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

        private int sendPost(String urlAuth, String postParams) throws IOException {
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
            return conn.getResponseCode();
        }

        private void setCookies( List<String> keksi){
            cookies = keksi;
        }

    }

    private static void loadCookies( Context context, String username, String password){
        try{
            FileInputStream fis = context.openFileInput( username + password + ".txt");
            ObjectInputStream oi = new ObjectInputStream( fis);
            cookies =(List<String>) oi.readObject();

            fis.close();
            oi.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            cookies = null;
        } catch (IOException e) {
            e.printStackTrace();
            cookies = null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            cookies = null;
        }
    }

    private static void writeCookies( Context context, String username, String password){
        try{
            FileOutputStream fos = context.openFileOutput(username + password + ".txt", Context.MODE_PRIVATE);
            ObjectOutputStream o = new ObjectOutputStream( fos);
            o.writeObject( cookies);

            fos.close();
            o.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void deleteCookies( Context context, String username){
        try{
            FileOutputStream fos = context.openFileOutput(username + ".txt", Context.MODE_PRIVATE);
            ObjectOutputStream o = new ObjectOutputStream( fos);
            o.writeObject( null);

            o.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //______________________________________________________________________________________

    //______________________________________________________________________________________
    //returns true if downloading has started and false otherwise
    public static boolean calendarPeriodicDownload(Context context){
        //download calendar if it was not downloaded today
        Date lastDate = readDate( context);
        Date nowDate = new Date();
        SimpleDateFormat formatDate = new SimpleDateFormat( "yyyyMMdd");
        if( lastDate == null || !formatDate.format( lastDate).equals( formatDate.format( nowDate))){
            downloadCalendar( context);
            writeDate( context, nowDate);
            return true;
        }
        return false;
    }

    private static Date readDate( Context context){
        Date result = null;
        try{
            FileInputStream fis = context.openFileInput( Constants.CALENDAR_LAST_SAVED_DATE_FILE);
            ObjectInputStream oi = new ObjectInputStream( fis);
            result = (Date) oi.readObject();
            fis.close();
            oi.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static void writeDate( Context context, Date date){
        try{
            FileOutputStream fos = context.openFileOutput( Constants.CALENDAR_LAST_SAVED_DATE_FILE, Context.MODE_PRIVATE);
            ObjectOutputStream o = new ObjectOutputStream( fos);
            o.writeObject( date);
            o.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void downloadCalendar( Context context){
        new DownloadCalendar( context).execute( "https://www.fer.unizg.hr/kalendar");
    }

    static class DownloadCalendar extends AsyncTask<String, Void, Void>{


        private Context mainContext;
        private final String USER_AGENT = "Mozilla/5.0";

        public DownloadCalendar( Context context){
            mainContext = context;
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                String calendarPage = getPageContent( strings[0]);
                Document doc = Jsoup.parse( calendarPage);
                Elements el = doc.getElementsByTag( "a");
                Element downloadLink = el.get( 40); // Preuzmi u iCal formatu je na indexu 40

                String url = downloadLink.attr( "href");
                downloadFile( url);
            } catch (IOException e) {
                e.printStackTrace();
            }catch( IndexOutOfBoundsException e){
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
            return response;
        }

        private void downloadFile(String url) {
            Uri downloadURI = Uri.parse( url);
            DownloadManager manager = ( DownloadManager) mainContext.getSystemService( Context.DOWNLOAD_SERVICE);
            try{
                if( manager != null){
                    DownloadManager.Request request = new DownloadManager.Request( downloadURI);
                    request.setAllowedNetworkTypes( DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                            .setTitle( Constants.CALENDAR_DATA_FILE)
                            .setDescription( "Downloading " + Constants.CALENDAR_DATA_FILE)
                            .setAllowedOverMetered( true)
                            .setAllowedOverRoaming( true)
                            .setNotificationVisibility( DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                            .setDestinationInExternalFilesDir( mainContext, null, Constants.CALENDAR_DATA_FILE)
                            .setMimeType( getMimeType( downloadURI));
                    manager.enqueue( request);
                }else{
                    Intent intent = new Intent(Intent.ACTION_VIEW, downloadURI);
                    mainContext.startActivity( intent);
                }
            }catch ( Exception e){
                e.printStackTrace();
            }
        }

        private String getMimeType( Uri uri){
            ContentResolver resolver = mainContext.getContentResolver();
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            return mimeTypeMap.getExtensionFromMimeType( resolver.getType( uri));
        }

    }
    //______________________________________________________________________________________

    //______________________________________________________________________________________
    public static String getUserFullName(){
        try {
            return new GetUserFullname().execute( "https://www.fer.unizg.hr/cip/podrska/korisnicki_racuni").get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Error";
    }

    private static class GetUserFullname extends AsyncTask<String, Void, String>{
        private final String USER_AGENT = "Mozilla/5.0";

        @Override
        protected String doInBackground(String... strings) {
            try {
                String pageHtml = getPageContent( strings[0]);
                Document doc = Jsoup.parse( pageHtml);
                Elements elements = doc.getElementsByTag( "b");

                String fullname = elements.get( 0).text();
                if( fullname == null) throw new NotLoggedInException( "nije ucitano ime");
                return fullname;
            } catch (IOException e) {
                e.printStackTrace();
            } catch( IndexOutOfBoundsException e){
                e.printStackTrace();
            } catch( NotLoggedInException e){
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
            return response;
        }
    }
    //______________________________________________________________________________________
    static String readStream(InputStream in) throws IOException {
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
