package me.anuraag.eatifi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.andtinder.model.CardModel;
import com.andtinder.model.Orientations;
import com.andtinder.view.CardContainer;
import com.parse.Parse;
import com.parse.ParseUser;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class HomePage extends Activity {
    private TextView mytext,yelptext,locationtext;
    private Button refresh;
    private ParseUser myuser;
    private CardContainer mCardContainer;
    private double lon,lat;
    private String hello;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location curLoc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Parse.initialize(this, "n2koKwQHYtQGedP92Uq6jEpHqMw7WByd6F11yMVh", "VObFmhueGhCXuNqeKZlkgeFkCB5Vw01gk1MkQNM9");
        mytext = (TextView)findViewById(R.id.textView);
        yelptext = (TextView)findViewById(R.id.textView3);
        locationtext = (TextView)findViewById(R.id.textView2);
        doLocation();
        refresh = (Button)findViewById(R.id.button);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestTask task = new RequestTask();
                Log.i("Request thing","https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + lat + "," + lon + "&rankedby=distance&types=food&radius=1000&key=AIzaSyB4wT0Q82Hql9pyd-2G_suhrjubYWp_axM");
                task.execute(new String[]{"https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + lat + "," + lon + "&rankedby=distance&types=food&radius=1000&key=AIzaSyB4wT0Q82Hql9pyd-2G_suhrjubYWp_axM"});
            }
        });
        try{
            myuser = ParseUser.getCurrentUser();
            Log.i("User", myuser.getEmail());
            mytext.setText("Hello " +myuser.getEmail());

        }catch (NullPointerException e){
            Log.i("Something","is wrong");
        }
        mCardContainer = (CardContainer) findViewById(R.id.layoutview);
        mCardContainer.setOrientation(Orientations.Orientation.Ordered);
        CardModel card = new CardModel("Title1", "Description goes here", getResources().getDrawable(R.drawable.ic_launcher));

//        mytext.setText(myuser.toString());

    }
    public void doLocation(){
        locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                displayLocation(location);
                curLoc = location;
                Log.i("Coordinates",curLoc.toString());
                //doTimeCheck();
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }
    public void displayLocation(Location l){
        String loc = "";
         lon = 0;
         lat = 0;
        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(l.getLatitude(), l.getLongitude(), 1);
            if (addresses.size() > 0) {
                loc = addresses.get(0).getPostalCode();
                lon = addresses.get(0).getLongitude();
                lat = addresses.get(0).getLatitude();
            }
            Log.i("Long",lon + "");
            Log.i("Lat",lat + "");
            locationtext.setText(loc);
            Log.i("Location",loc);
        }catch(IOException e){

        }catch(NullPointerException n){

        }


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public class RequestTask extends AsyncTask<String, String, String> {
        private TextView myView;
        private String s;
        @Override
        protected String doInBackground(String... uri) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            try {
                response = httpclient.execute(new HttpGet(uri[0]));
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    responseString = out.toString();
                } else {
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {
                //TODO Handle problems..
            } catch (IOException e) {
                //TODO Handle problems..
            }

//                Log.i("", responseString);
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            hello = result;
            Log.i("Result",result);
        }
    }
}
