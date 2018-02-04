package ug.flights.huza.flightsapp;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import model.AirportCodesModel;

/**
 * Created by Huzy_Kamz on 2/4/2018.
 */

public class StoreLocations extends Service {

    String mytokenFromSharedPref = "";
    private SharedPreferences sp;
    private SharedPreferences sharedpreferences;
    private static String MY_LAT_ORIGIN = "latorigin";
    private static final String MY_LONG_ORIGIN = "longorigin";
    private static final String MY_LAT_DEST = "latdest";
    private static final String MY_LONG_DEST = "longdest";
    private static final String GOOGLE_MAPS_PREF = "googleprefs";
    private Context c;

    private String FROM ="";
    private String TO ="";



    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub

        FROM = intent.getStringExtra("fromLocation");
         TO = intent.getStringExtra("toLocation");

        //shared preference initialization for retrieving a token
        sharedpreferences = getApplicationContext().getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        sp = getApplicationContext().getSharedPreferences(GOOGLE_MAPS_PREF, Context.MODE_PRIVATE);

        //getting a token value
        mytokenFromSharedPref = sharedpreferences.getString(MainActivity.MY_TOKEN, "").trim();



        getOriginLocation(FROM);
        getDestinationLocation(TO);

        sp = getSharedPreferences(GOOGLE_MAPS_PREF, Context.MODE_PRIVATE);
        String latitudeOrigin = sp.getString(MY_LAT_ORIGIN, null);
        String longitudeOrigin = sp.getString(MY_LONG_ORIGIN, null);
        String latitudeDest = sp.getString(MY_LAT_DEST, null);
        String longitudeDest = sp.getString(MY_LONG_DEST, null);

        // print in logcat
        System.out.println(" ---> Shared SERVICE NEW ----> " + " LTO :  " + latitudeOrigin +
                " LNGO : " + longitudeOrigin +
                "LTD : " + latitudeDest +
                "LONGD : " + longitudeDest);

        return super.onStartCommand(intent, flags, startId);
    }

    private void getOriginLocation(String airportCode) {
        String token = mytokenFromSharedPref;
        String Limit = "100";
        final String Url = "/v1/references/airports/" + airportCode + "/?limit=" + Limit + "&offset=0&LHoperated=1&access_token=" + token;
        final ProgressDialog pd = new ProgressDialog(c);
        pd.setMessage("Getting Airport Locations ` ...");
        pd.show();
        Ion.with(c)
                .load(Config.MAIN__URL + Url)
                .progressDialog(pd)
                .setHeader("Accept", "application/json")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, final JsonObject result) {


                        try {
                            if (result.toString() != null) {
                                //  DataProvider dp = new DataProvider(MapsActivity.this);
                                // dp.getAirportcodes();
                                parseOriginJson(result.toString());
                                System.out.println(" REAL RESULT :  " + result.toString());
                                pd.dismiss();


                            } else {
                                Toast.makeText(c, Config.POOR_NETWORK_CONNECTION, Toast.LENGTH_LONG).show();
                                pd.dismiss();
                            }
                        } catch (Exception t) {
                            Toast.makeText(c, Config.POOR_NETWORK_CONNECTION, Toast.LENGTH_LONG).show();
                            pd.dismiss();
                        }

                    }
                });

    }

    private void getDestinationLocation(String airportCode) {
        String token = mytokenFromSharedPref;
        String Limit = "100";
        final String Url = "/v1/references/airports/" + airportCode + "/?limit=" + Limit + "&offset=0&LHoperated=1&access_token=" + token;
        Ion.with(c)
                .load(Config.MAIN__URL + Url)
                .setHeader("Accept", "application/json")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, final JsonObject result) {


                        try {
                            if (result.toString() != null) {
                                // DataProvider dp = new DataProvider(MapsActivity.this);
                                // dp.getAirportcodes();
                                parseDestinationJson(result.toString());
                                //System.out.println(" REAL RESULT :  " + result.toString());


                            } else {
                                Toast.makeText(c, Config.POOR_NETWORK_CONNECTION, Toast.LENGTH_LONG).show();

                            }
                        } catch (Exception t) {
                            Toast.makeText(c, Config.POOR_NETWORK_CONNECTION, Toast.LENGTH_LONG).show();

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
                    editor.remove(MY_LAT_ORIGIN);
                    editor.remove(MY_LONG_ORIGIN);
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
                Toast.makeText(c, Config.POOR_NETWORK_CONNECTION, Toast.LENGTH_LONG).show();

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
                    editor.remove(MY_LAT_DEST);
                    editor.remove(MY_LONG_DEST);
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
                Toast.makeText(c, Config.POOR_NETWORK_CONNECTION, Toast.LENGTH_LONG).show();

            }
        } catch (JSONException r) {
            System.out.println("ERROR PROB : " + r);

        }

    }


}
