package ug.flights.huza.flightsapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.util.HashList;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.AirportCodesModel;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String from, to;
    private Intent i;
    SharedPreferences sharedpreferences;
    String mytokenFromSharedPref="";
    HashMap<String,String> hashMapOrigin=null;
     HashMap<String,String> hashMapDestination=null;
    private static final int COLOR_BLACK_ARGB = 0xff000000;
    private static final int POLYLINE_STROKE_WIDTH_PX = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        //Shared preference data
        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        mytokenFromSharedPref = sharedpreferences.getString(MainActivity.MY_TOKEN,null);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Flight Routes");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        i= getIntent();
        if(i!=null){
            from = i.getStringExtra("fromLocation");
            to = i.getStringExtra("toLocation");

            hashMapOrigin = new HashMap<>();
            hashMapDestination = new HashMap<>();

            getOriginLocation(from);
            getDestinationLocation(to);

            DataProvider dp = new DataProvider(MapsActivity.this);
            double lto = Double.parseDouble(dp.GetOriginLatitude());
            double longo = Double.parseDouble(dp.GetOriginLongitude());
            double ltd = Double.parseDouble(dp.GetDestLatitude());
            double longd= Double.parseDouble(dp.GetDestLongitude());

            Toast.makeText(MapsActivity.this," LTO :  "+ lto+
                    " LNGO : "+ longo+
                    "LTD : "+ ltd+
                    "LONGD : "+ longd,Toast.LENGTH_LONG).show();

            System.out.println(" LTO :  "+ lto+
                    " LNGO : "+ longo+
                    "LTD : "+ ltd+
                    "LONGD : "+ longd);


        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                break;


        }
        return super.onOptionsItemSelected(item);
    }



    private void getOriginLocation(String airportCode)
    {
        String token=  mytokenFromSharedPref;
        String Limit = "100";
        final String Url ="/v1/references/airports/"+airportCode+"/?limit="+Limit+"&offset=0&LHoperated=1&access_token="+token;
        final ProgressDialog pd = new ProgressDialog(MapsActivity.this);
        pd.setMessage("Getting Airport Locations ` ...");
        pd.show();
        Ion.with(MapsActivity.this)
                .load(Config.MAIN__URL+Url)
                .progressDialog(pd)
                .setHeader("Accept","application/json")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e,final  JsonObject result) {


                        try {
                            if (result.toString() != null) {
                                DataProvider dp = new DataProvider(MapsActivity.this);
                                dp.getAirportcodes();
                                parseOriginJson(result.toString());
                              //  System.out.println(" REAL RESULT :  " + result.toString());
                                pd.dismiss();


                            } else {
                                Toast.makeText(MapsActivity.this, Config.POOR_NETWORK_CONNECTION, Toast.LENGTH_LONG).show();
                                pd.dismiss();
                            }
                        }
                        catch (Exception t){
                            Toast.makeText(MapsActivity.this, Config.POOR_NETWORK_CONNECTION, Toast.LENGTH_LONG).show();
                            pd.dismiss();
                        }

                    }
                });

    }

    private void getDestinationLocation(String airportCode)
    {
        //https://api.lufthansa.com/v1/references/airports/?limit=100&offset=0&LHoperated=1&access_token=8yvqxdkbjpw74dzrujtnf9c3
        String token=  mytokenFromSharedPref;
        String Limit = "100";
        final String Url ="/v1/references/airports/"+airportCode+"/?limit="+Limit+"&offset=0&LHoperated=1&access_token="+token;
        final ProgressDialog pd = new ProgressDialog(MapsActivity.this);
        pd.setMessage("Getting Flight Routes ...");
        pd.show();
        Ion.with(MapsActivity.this)
                .load(Config.MAIN__URL+Url)
                .progressDialog(pd)
                .setHeader("Accept","application/json")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e,final  JsonObject result) {


                        try {
                            if (result.toString() != null) {
                                DataProvider dp = new DataProvider(MapsActivity.this);
                                dp.getAirportcodes();
                                parseDestinationJson(result.toString());
                                //System.out.println(" REAL RESULT :  " + result.toString());
                                pd.dismiss();


                            } else {
                                Toast.makeText(MapsActivity.this, Config.POOR_NETWORK_CONNECTION, Toast.LENGTH_LONG).show();
                                pd.dismiss();
                            }
                        }
                        catch (Exception t){
                            Toast.makeText(MapsActivity.this, Config.POOR_NETWORK_CONNECTION, Toast.LENGTH_LONG).show();
                            pd.dismiss();
                        }

                    }
                });

    }


    private void parseOriginJson(String  result)
    {
        try {
            if (result!=null) {
                List<AirportCodesModel> itemList = new ArrayList<>();
                JSONArray arr = new JSONArray("["+result+"]");
                for (int i = 0; i < arr.length(); i++)
                {
                    String AirportCode = arr.getJSONObject(i)
                            .getJSONObject("AirportResource")
                            .getJSONObject("Airports")
                            .getJSONObject("Airport")
                            .getString("AirportCode");
                    String Latitude = arr.getJSONObject(i)
                            .getJSONObject("AirportResource")
                            .getJSONObject("Airports")
                            .getJSONObject("Airport")
                            .getJSONObject("Position")
                            .getJSONObject("Coordinate")
                            .getString("Latitude");

                    String Longitude =  arr.getJSONObject(i)
                            .getJSONObject("AirportResource")
                            .getJSONObject("Airports")
                            .getJSONObject("Airport")
                            .getJSONObject("Position")
                            .getJSONObject("Coordinate")
                            .getString("Longitude");

                    DataProvider dp = new DataProvider(MapsActivity.this);
                    dp.UpdateLatitudeOrigin();
                    dp.UpdateLongitudeOrigin();
                    dp.storeOriginLatitude(Latitude);
                    dp.storeOriginLongitude(Longitude);

                    Toast.makeText(MapsActivity.this," AirportCode : "+ AirportCode+" Latitude : "
                            + Latitude+" Longitude :"+Longitude,Toast.LENGTH_LONG).show();

                    System.out.println(" AirportCode : "+ AirportCode+" Latitude : "
                            + Latitude+" Longitude :"+Longitude);

                    AirportCodesModel model = new AirportCodesModel();
                    model.setAirportCode(AirportCode);
                    model.setLatitude(Latitude);
                    model.setLongitude(Longitude);
                    itemList.add(model);
                }


                for (int k=0; k<itemList.size(); k++){

                    hashMapDestination.put(itemList.get(0).getLatitude(),itemList.get(0).getLongitude());
                }

            }
            else{
                Toast.makeText(MapsActivity.this,Config.POOR_NETWORK_CONNECTION, Toast.LENGTH_LONG).show();

            }
        }
        catch (JSONException r){
            System.out.println("ERROR PROB ORIGIN  : "+  r);

        }

    }
    private void parseDestinationJson(String  result)
    {
        try {
            if (result!=null) {

                List<AirportCodesModel> itemList = new ArrayList<>();

                //  JSONObject obj = new JSONObject(result).getJSONObject("AirportResource").getJSONObject("Airports").getJSONObject("Airport");
                JSONArray arr = new JSONArray("["+result+"]");
                for (int i = 0; i < arr.length(); i++)
                {
                    String AirportCode = arr.getJSONObject(i)
                            .getJSONObject("AirportResource")
                            .getJSONObject("Airports")
                            .getJSONObject("Airport")
                            .getString("AirportCode");
                    String Latitude = arr.getJSONObject(i)
                            .getJSONObject("AirportResource")
                            .getJSONObject("Airports")
                            .getJSONObject("Airport")
                            .getJSONObject("Position")
                            .getJSONObject("Coordinate")
                            .getString("Latitude");

                    String Longitude =  arr.getJSONObject(i)
                            .getJSONObject("AirportResource")
                            .getJSONObject("Airports")
                            .getJSONObject("Airport")
                            .getJSONObject("Position")
                            .getJSONObject("Coordinate")
                            .getString("Longitude");


                    DataProvider dp = new DataProvider(MapsActivity.this);
                    dp.UpdateLatitudeDest();
                    dp.UpdateLongitudeDest();
                    dp.storeDestLatitude(Latitude);
                    dp.storeDestLongitude(Longitude);


                    Toast.makeText(MapsActivity.this," AirportCode : "+ AirportCode+" Latitude : "
                            + Latitude+" Longitude :"+Longitude,Toast.LENGTH_LONG).show();

                    System.out.println(" AirportCode : "+ AirportCode+" Latitude : "
                            + Latitude+" Longitude :"+Longitude);

                    AirportCodesModel model = new AirportCodesModel();
                    model.setAirportCode(AirportCode);
                    model.setLatitude(Latitude);
                    model.setLongitude(Longitude);
                    itemList.add(model);
                }


                for (int k=0; k<itemList.size(); k++){

                    hashMapDestination.put(itemList.get(0).getLatitude(),itemList.get(0).getLongitude());
                }
            }
            else{
                Toast.makeText(MapsActivity.this,Config.POOR_NETWORK_CONNECTION, Toast.LENGTH_LONG).show();

            }
        }
        catch (JSONException r){
            System.out.println("ERROR PROB : "+  r);

        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    @Override
    public void onMapReady(GoogleMap googleMap)
    {

        mMap = googleMap;


            DataProvider dp = new DataProvider(MapsActivity.this);

            double lto = Double.parseDouble(dp.GetOriginLatitude());
            double longo = Double.parseDouble(dp.GetOriginLongitude());
            double ltd = Double.parseDouble(dp.GetDestLatitude());
            double longd= Double.parseDouble(dp.GetDestLongitude());

                Polyline polyline = googleMap.addPolyline(new PolylineOptions()
                        .clickable(true)
                        .add(
                                new LatLng(lto, longo),
                                new LatLng(ltd, longd)
                        ));
                 stylePolyline(polyline);

                // Add a marker and move the camera Origin
                LatLng origin = new LatLng(lto, longo);
                mMap.addMarker(new MarkerOptions()
                        .position(origin)
                        .title("ORIGIN : "+from)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.airport_mark)
                        ));
               // mMap.moveCamera(CameraUpdateFactory.newLatLng(origin));

                // Add a marker and move the camera Destination
                LatLng destination = new LatLng(ltd, longd);
                mMap.addMarker(new MarkerOptions()
                        .position(destination)
                        .title("DESTINATION : "+to)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.airport_mark)
                        ));
               // mMap.moveCamera(CameraUpdateFactory.newLatLng(destination));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(lto, longo));
        builder.include(new LatLng(ltd, longd));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 48));

    }

    private void stylePolyline(Polyline polyline) {
        String type = "";
        // Get the data object stored with the polyline.
        if (polyline.getTag() != null) {
            type = polyline.getTag().toString();
        }

        switch (type) {
            // If no type is given, allow the API to use the default.
            case "A":
                // Use a custom bitmap as the cap at the start of the line.
                polyline.setStartCap(
                        new CustomCap(
                                BitmapDescriptorFactory.fromResource(R.drawable.flight_arrow), 10));
                break;
            case "B":
                // Use a round cap at the start of the line.
                polyline.setStartCap(new RoundCap());
                break;
        }

        polyline.setEndCap(new RoundCap());
        polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
        polyline.setColor(Color.parseColor("#257c38"));
        polyline.setJointType(JointType.ROUND);
    }

}
