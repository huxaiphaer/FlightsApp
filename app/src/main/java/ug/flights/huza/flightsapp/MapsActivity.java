package ug.flights.huza.flightsapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.gson.JsonObject;
import com.google.maps.android.clustering.ClusterManager;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import maps.MyItem;
import model.AirportCodesModel;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private String from, to;
    private Intent i=null;
    String mytokenFromSharedPref = "";
    private static final int POLYLINE_STROKE_WIDTH_PX = 9;
    private SharedPreferences sharedpreferences;
    private SharedPreferences sp;
    private static String MY_LAT_ORIGIN = "latorigin";
    private static final String MY_LONG_ORIGIN = "longorigin";
    private static final String MY_LAT_DEST = "latdest";
    private static final String MY_LONG_DEST = "longdest";
    private static final String GOOGLE_MAPS_PREF = "googleprefs";
    private ClusterManager<MyItem> mClusterManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //Shared preference data


        getSupportActionBar().setDisplayShowHomeEnabled(true);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Flight Routes");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        sp = getSharedPreferences(GOOGLE_MAPS_PREF, Context.MODE_PRIVATE);

        //getting a token value
        mytokenFromSharedPref = sharedpreferences.getString(MainActivity.MY_TOKEN, "").trim();
        mMap = googleMap;

        i = getIntent();
        if (i != null) {

            from = i.getStringExtra("fromLocation");
            to = i.getStringExtra("toLocation");
            getOriginLocation(from);
            getDestinationLocation(to);
        }

        setUpClusterer(mMap);
    }


    //clustered map

    private void setUpClusterer(GoogleMap mMap) {
        // Position the map.

       // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-23.684, 133.903), 4));
        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<MyItem>(this, mMap);


        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);


        // Add cluster items (markers) to the cluster manager.
        addItems(mMap);
    }

    private void addItems(GoogleMap mMap) {

        // Set some lat/lng coordinates to start with.
        String latitudeOrigin = sp.getString(MY_LAT_ORIGIN, "0.00").trim();
        String longitudeOrigin = sp.getString(MY_LONG_ORIGIN, "0.00").trim();
        String latitudeDest = sp.getString(MY_LAT_DEST, "0.00").trim();
        String longitudeDest = sp.getString(MY_LONG_DEST, "0.00").trim();


        // print in logcat
        System.out.println(" ---> Shared NEW ----> " + " LTO :  " + latitudeOrigin +
                " LNGO : " + longitudeOrigin +
                "LTD : " + latitudeDest +
                "LONGD : " + longitudeDest);


        double lto = Double.parseDouble(latitudeOrigin);
        double longo = Double.parseDouble(longitudeOrigin);
        double ltd = Double.parseDouble(latitudeDest);
        double longd = Double.parseDouble(longitudeDest);


        HashMap<Double, Double> hm = new HashMap<>();

        hm.put(lto, longo);
        hm.put(ltd, longd);

        try {
            /*Polyline polyline = mMap.addPolyline(new PolylineOptions()
                    .clickable(true)
                    .add(
                            new LatLng(lto, longo),
                            new LatLng(ltd, longd)

                    ));

            stylePolyline(polyline);*/



            for (Map.Entry m : hm.entrySet())

            {
                System.out.println(m.getKey() + " " + m.getValue());
                MyItem offsetItem = new MyItem(Double.parseDouble(m.getKey().toString()), Double.parseDouble(m.getValue().toString()));
                mClusterManager.addItem(offsetItem);


            }

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(new LatLng(lto, longo));
            builder.include(new LatLng(ltd, longd));
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 48));
        }
        catch (Exception k){

        }

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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                break;


        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private void getOriginLocation(String airportCode) {
        String token = mytokenFromSharedPref;
        String Limit = "100";
        final String Url = "/v1/references/airports/" + airportCode + "/?limit=" + Limit + "&offset=0&LHoperated=1&access_token=" + token;
        final ProgressDialog pd = new ProgressDialog(MapsActivity.this);
        pd.setMessage("Getting Airport Locations ` ...");
        pd.show();
        Ion.with(MapsActivity.this)
                .load(Config.MAIN__URL + Url)
                .progressDialog(pd)
                .setHeader("Accept", "application/json")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, final JsonObject result) {


                        try {
                            if (result.toString() != null) {

                                parseOriginJson(result.toString());
                                System.out.println(" REAL RESULT :  " + result.toString());
                                pd.dismiss();


                            } else {
                                Toast.makeText(MapsActivity.this, Config.POOR_NETWORK_CONNECTION, Toast.LENGTH_LONG).show();
                                pd.dismiss();
                            }
                        } catch (Exception t) {
                            Toast.makeText(MapsActivity.this, Config.POOR_NETWORK_CONNECTION, Toast.LENGTH_LONG).show();
                            pd.dismiss();
                        }

                    }
                });

    }

    private void getDestinationLocation(String airportCode) {
        String token = mytokenFromSharedPref;
        String Limit = "100";
        final String Url = "/v1/references/airports/" + airportCode + "/?limit=" + Limit + "&offset=0&LHoperated=1&access_token=" + token;
        final ProgressDialog pd = new ProgressDialog(MapsActivity.this);
        pd.setMessage("Getting Flight Routes ...");
        pd.show();
        Ion.with(MapsActivity.this)
                .load(Config.MAIN__URL + Url)
                .progressDialog(pd)
                .setHeader("Accept", "application/json")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, final JsonObject result) {


                        try {
                            if (result.toString() != null) {

                                parseDestinationJson(result.toString());
                                //System.out.println(" REAL RESULT :  " + result.toString());
                                pd.dismiss();


                            } else {
                                Toast.makeText(MapsActivity.this, Config.POOR_NETWORK_CONNECTION, Toast.LENGTH_LONG).show();
                                pd.dismiss();
                            }
                        } catch (Exception t) {
                            Toast.makeText(MapsActivity.this, Config.POOR_NETWORK_CONNECTION, Toast.LENGTH_LONG).show();
                            pd.dismiss();
                        }

                    }
                });


    }

    private void parseOriginJson(String result) {
        try {
            if (result != null) {
                List<AirportCodesModel> itemList = new ArrayList<>();
                JSONArray arr = new JSONArray("[" + result + "]");
                for (int i = 0; i < arr.length(); i++) {
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

                    String Longitude = arr.getJSONObject(i)
                            .getJSONObject("AirportResource")
                            .getJSONObject("Airports")
                            .getJSONObject("Airport")
                            .getJSONObject("Position")
                            .getJSONObject("Coordinate")
                            .getString("Longitude");


                    sp = getSharedPreferences(GOOGLE_MAPS_PREF, Context.MODE_PRIVATE);

                    SharedPreferences.Editor editor = sp.edit();
                    /*editor.remove(MY_LAT_ORIGIN);
                    editor.remove(MY_LONG_ORIGIN);*/
                    editor.putString(MY_LAT_ORIGIN, Latitude);
                    editor.putString(MY_LONG_ORIGIN, Longitude);
                    editor.commit();

                    System.out.println(" AirportCode : " + AirportCode + " Latitude : "
                            + Latitude + " Longitude :" + Longitude);

                    AirportCodesModel model = new AirportCodesModel();
                    model.setAirportCode(AirportCode);
                    model.setLatitude(Latitude);
                    model.setLongitude(Longitude);
                    itemList.add(model);
                }


            } else {
                Toast.makeText(MapsActivity.this, Config.POOR_NETWORK_CONNECTION, Toast.LENGTH_LONG).show();

            }
        } catch (JSONException r) {
            System.out.println("ERROR PROB ORIGIN  : " + r);

        }

    }

    private void parseDestinationJson(String result) {
        try {
            if (result != null) {

                List<AirportCodesModel> itemList = new ArrayList<>();

                JSONArray arr = new JSONArray("[" + result + "]");
                for (int i = 0; i < arr.length(); i++) {
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

                    String Longitude = arr.getJSONObject(i)
                            .getJSONObject("AirportResource")
                            .getJSONObject("Airports")
                            .getJSONObject("Airport")
                            .getJSONObject("Position")
                            .getJSONObject("Coordinate")
                            .getString("Longitude");


                    //Initializing Shared Prefeerences
                    sp = getSharedPreferences(GOOGLE_MAPS_PREF, Context.MODE_PRIVATE);

                    SharedPreferences.Editor editor = sp.edit();
                    /*editor.remove(MY_LAT_DEST);
                    editor.remove(MY_LONG_DEST);*/
                    editor.putString(MY_LAT_DEST, Latitude);
                    editor.putString(MY_LONG_DEST, Longitude);
                    editor.commit();


                    System.out.println(" AirportCode : " + AirportCode + " Latitude : "
                            + Latitude + " Longitude :" + Longitude);

                    AirportCodesModel model = new AirportCodesModel();
                    model.setAirportCode(AirportCode);
                    model.setLatitude(Latitude);
                    model.setLongitude(Longitude);
                    itemList.add(model);
                }


            } else {
                Toast.makeText(MapsActivity.this, Config.POOR_NETWORK_CONNECTION, Toast.LENGTH_LONG).show();

            }
        } catch (JSONException r) {
            System.out.println("ERROR PROB : " + r);

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


    //Dashed polyline
    public static void createDashedPolyLine(GoogleMap map, LatLng latLngOrig, LatLng latLngDest) {
        double difLat = latLngDest.latitude - latLngOrig.latitude;
        double difLng = latLngDest.longitude - latLngOrig.longitude;

        double zoom = map.getCameraPosition().zoom;

        double divLat = difLat / (zoom * 2);
        double divLng = difLng / (zoom * 2);

        LatLng tmpLatOri = latLngOrig;

        for (int i = 0; i < (zoom * 2); i++) {
            LatLng loopLatLng = tmpLatOri;

            if (i > 0) {
                loopLatLng = new LatLng(tmpLatOri.latitude + (divLat * 0.25f), tmpLatOri.longitude + (divLng * 0.25f));
            }

            Polyline polyline = map.addPolyline(new PolylineOptions()
                    .add(loopLatLng)
                    .add(new LatLng(tmpLatOri.latitude + divLat, tmpLatOri.longitude + divLng))
                    .color(Color.parseColor("#257c38"))
                    .width(5f));

            tmpLatOri = new LatLng(tmpLatOri.latitude + divLat, tmpLatOri.longitude + divLng);
            polyline.setEndCap(new RoundCap());
            polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
            polyline.setColor(Color.parseColor("#257c38"));
            polyline.setJointType(JointType.ROUND);
        }
    }


}
