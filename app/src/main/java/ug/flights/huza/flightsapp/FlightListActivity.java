package ug.flights.huza.flightsapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import adapter.FlightAdapter;
import model.FlightModel;

public class FlightListActivity extends AppCompatActivity {

    private RecyclerView flights_rv;
    Intent i ;
    String ORIGIN, DESTINATION,DATEFLIGHTS,CHECKFLIGHTS;
    String mytokenFromSharedPref="";
    public static final String MY_TOKEN = "AccessToken";
    List<FlightModel> itemList= null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_flights);
        flights_rv = (RecyclerView)findViewById(R.id.flights_rv);
        flights_rv.setLayoutManager(new LinearLayoutManager(FlightListActivity.this));

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("FLIGHTS ON :");

        //get data from  share pref
        SharedPreferences  sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        mytokenFromSharedPref = sharedpreferences.getString(MainActivity.MY_TOKEN,null);

        Toast.makeText(FlightListActivity.this,""+mytokenFromSharedPref,Toast.LENGTH_LONG).show();

        System.out.println(" Shared PREF TOKEN : "+ mytokenFromSharedPref);

        i= getIntent();

        if (i!=null) {
            ORIGIN = i.getStringExtra("orign");
            DESTINATION = i.getStringExtra("destination");
            DATEFLIGHTS = i.getStringExtra("dateflights");
            CHECKFLIGHTS = i.getStringExtra("checkflights");
            fetchFlightsOnline(ORIGIN,DESTINATION,DATEFLIGHTS,CHECKFLIGHTS);
        }


    }

    private void fetchFlightsOnline(String Origin, String Dest, String Dateflights, String Checkflights) {

        //Retrieve our token from shared preferences


        String token=  mytokenFromSharedPref;
        String Url ="/v1/operations/schedules/ZRH/FRA/2018-12-02?directFlights=0&access_token="+token;
        final ProgressDialog pd = new ProgressDialog(FlightListActivity.this);
        pd.setMessage("Checking Flights ...");
        pd.show();
        Ion.with(FlightListActivity.this)
                .load(Config.MAIN__URL+Url)
                .setHeader("Accept","application/json")
                .progressDialog(pd)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e,final  JsonObject result) {

                        if(result.toString()!=null){
                        parseJson(result.toString());
                        pd.dismiss();
                        }

                        else{
                            Toast.makeText(FlightListActivity.this,Config.POOR_NETWORK_CONNECTION, Toast.LENGTH_LONG).show();
                            pd.dismiss();
                        }

                    }
                });
    }


    private void parseJson(String  result){

        try {
            if (result!=null) {
                String resultTostring = "" + result;
                JSONObject obj = new JSONObject(resultTostring).getJSONObject("ScheduleResource");
                JSONArray arr = obj.getJSONArray("Schedule");
                itemList = new ArrayList<>();
                for (int i = 0; i < arr.length(); i++)
                {
                    String DepartureAirport = arr.getJSONObject(i).getJSONObject("Flight").getJSONObject("Departure").getString("AirportCode");
                    String ArrivalAirport = arr.getJSONObject(i).getJSONObject("Flight").getJSONObject("Arrival").getString("AirportCode");
                    String Duration = arr.getJSONObject(i).getJSONObject("TotalJourney").getString("Duration");
                    String DepartureTime = arr.getJSONObject(i).getJSONObject("Flight").getJSONObject("Departure").getJSONObject("ScheduledTimeLocal").getString("DateTime");
                    String ArrivalTime = arr.getJSONObject(i).getJSONObject("Flight").getJSONObject("Arrival").getJSONObject("ScheduledTimeLocal").getString("DateTime");
                    String Stops = arr.getJSONObject(i).getJSONObject("Flight").getJSONObject("Details").getJSONObject("Stops").getString("StopQuantity");

                    FlightModel fmodel = new FlightModel();
                    fmodel.setDepartureAirport(DepartureAirport);
                    fmodel.setArrivalAirport(ArrivalAirport);
                    fmodel.setDuration(Duration);
                    fmodel.setDeparturTimee(DepartureTime);
                    fmodel.setArrivalTime(ArrivalTime);
                    fmodel.setStops(Stops);
                    fmodel.Stops= Stops;


                    itemList.add(fmodel);

                    System.out.println("DEPT : " + DepartureAirport + " Arrival " + ArrivalAirport+
                            " Duration : " + Duration + " Dept time : "+ DepartureTime+" Arr Time "+ ArrivalTime
                            +" Stops "+ Stops);

                    Toast.makeText(FlightListActivity.this, "DEPT : " + DepartureAirport + " Arrival " + ArrivalAirport+
                            " Duration : " + Duration + " Dept time : "+ DepartureTime+" Arr Time "+ ArrivalTime
                            +" Stops "+ Stops, Toast.LENGTH_LONG).show();

                    System.out.println("My list "+ itemList.get(i).getDeparturTimee());

                }

                // Setup and Handover data to recyclerview
                final FlightAdapter adapter = new FlightAdapter(itemList,FlightListActivity.this );
                flights_rv.setAdapter(adapter);

            }
            else{
                Toast.makeText(FlightListActivity.this,Config.POOR_NETWORK_CONNECTION, Toast.LENGTH_LONG).show();

            }
        }
        catch (JSONException r){
            System.out.println("ERROR PROB : "+  r);
            //  Toast.makeText(ListOfFlights.this,"ERROR PROB : "+ r,Toast.LENGTH_LONG).show();

        }

    }

}
