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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

//new Login().execute( "https://www.fer.unizg.hr/login/?frompage=%2F&return=%2F");
//new DownloadCalendar().execute( "https://www.fer.unizg.hr/kalendar");

public class ConnectionWithWebsite {
    public static final String CALENDAR_DATA_FILE = "calendarData.ics";
    private static final String USERNAME = "rj52171";
    private static final String PASSWORD = "92564161";

    private Context mainContext;
    private List<String> cookies;
    private HttpsURLConnection conn;

    public ConnectionWithWebsite( Context context ){
        mainContext = context;
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
            DownloadManager manager = ( DownloadManager) mainContext.getSystemService( Context.DOWNLOAD_SERVICE);
            try{
                if( manager != null){
                    DownloadManager.Request request = new DownloadManager.Request( downloadURI);
                    request.setAllowedNetworkTypes( DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                            .setTitle( CALENDAR_DATA_FILE)
                            .setDescription( "Downloading " + CALENDAR_DATA_FILE)
                            .setAllowedOverMetered( true)
                            .setAllowedOverRoaming( true)
                            .setNotificationVisibility( DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                            .setDestinationInExternalFilesDir( mainContext, null, CALENDAR_DATA_FILE)
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