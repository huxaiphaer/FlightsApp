package ug.flights.huza.flightsapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;
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
    Intent i;
    String ORIGIN, DESTINATION, DATEFLIGHTS;
    private static int CHECKFLIGHTS = 0;
    String mytokenFromSharedPref = "";
    public static final String MY_TOKEN = "AccessToken";
    List<FlightModel> itemList = null;

    private TextView origin_txt, dest_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_flights);
        flights_rv = (RecyclerView) findViewById(R.id.flights_rv);
        flights_rv.setLayoutManager(new LinearLayoutManager(FlightListActivity.this));


        //get token  from  share pref
        SharedPreferences sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        mytokenFromSharedPref = sharedpreferences.getString(MainActivity.MY_TOKEN, null);

        System.out.println(" Shared PREF TOKEN : " + mytokenFromSharedPref);

        i = getIntent();

        if (i != null) {
            ORIGIN = i.getStringExtra("origin");
            DESTINATION = i.getStringExtra("destination");
            origin_txt = (TextView) findViewById(R.id.origin_txt_detail);
            dest_txt = (TextView) findViewById(R.id.dest_txt_detail);

            //set text.
            origin_txt.setText(ORIGIN);
            dest_txt.setText(DESTINATION);

            DATEFLIGHTS = i.getStringExtra("dateflights");
            fetchFlightsOnline(ORIGIN.substring(0, 3), DESTINATION.substring(0, 3), DATEFLIGHTS, CHECKFLIGHTS);
        }

        System.out.print("Check BOX : " + CHECKFLIGHTS);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Flights Schedules  On : " + DATEFLIGHTS);


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

    private void fetchFlightsOnline(String origin, final String Dest, String Dateflights, int Checkflights) {

        //Retrieve our token from shared preferences
        String token = mytokenFromSharedPref;
        final String Url = "/v1/operations/schedules/" + origin + "/" + Dest + "/" + Dateflights + "?directFlights=" + Checkflights + "&access_token=" + token;
        final ProgressDialog pd = new ProgressDialog(FlightListActivity.this);
        pd.setMessage("Checking Flights schedule ...");
        pd.show();
        Ion.with(FlightListActivity.this)
                .load(Config.MAIN__URL + Url)
                .setHeader("Accept", "application/json")
                .progressDialog(pd)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, final JsonObject result) {
                        try {

                            String res = result.toString();
                            JSONObject obj = new JSONObject(res);
                            boolean hasError = obj
                                    .getJSONObject("ProcessingErrors")
                                    .has("ProcessingError");
                            boolean hasData = obj.has("ScheduleResource");


                            if (res != null) {

                                if (hasError) {
                                    Toast.makeText(FlightListActivity.this, Config.NO_SELECTIONS_AVAILABLE, Toast.LENGTH_LONG).show();
                                    pd.dismiss();

                                }


                                pd.dismiss();
                            } else {
                                Toast.makeText(FlightListActivity.this, Config.POOR_NETWORK_CONNECTION, Toast.LENGTH_LONG).show();
                                pd.dismiss();
                            }


                        } catch (Exception r) {

                            if(r.getMessage().equals("No value for ProcessingErrors")){
                                parseJson(result.toString(),Dest);
                                pd.dismiss();
                            }
                           // Toast.makeText(FlightListActivity.this, Config.POOR_NETWORK_CONNECTION, Toast.LENGTH_LONG).show();
                            System.out.println("Error ---> "+ r.getMessage());

                        }
                    }
                });
    }


    private void parseJson(String result , String dest) {

        try {
            if (result != null) {


                JSONObject obj = new JSONObject(result).getJSONObject("ScheduleResource");
                JSONArray arr = obj.getJSONArray("Schedule");
                itemList = new ArrayList<>();

                for (int i = 0; i < arr.length(); i++) {

                    Object intervention = arr.getJSONObject(i).get("Flight");
                    if (intervention instanceof JSONArray) {

                        JSONArray arr1 = arr.getJSONObject(i).getJSONArray("Flight");
                        if (arr1.length() != 0) {
                            System.out.println("My list " + arr1.length());
                            for (int j = 0; j < arr1.length(); j++) {
                                String Duration = arr.getJSONObject(i).getJSONObject("TotalJourney").getString("Duration");
                                String DepartureAirport = arr1.getJSONObject(j).getJSONObject("Departure").getString("AirportCode");
                                String ArrivalAirport = arr1.getJSONObject(j).getJSONObject("Arrival").getString("AirportCode");
                                String DepartureTime = arr1.getJSONObject(j).getJSONObject("Departure").getJSONObject("ScheduledTimeLocal").getString("DateTime");
                                String ArrivalTime = arr1.getJSONObject(j).getJSONObject("Arrival").getJSONObject("ScheduledTimeLocal").getString("DateTime");
                                String Stops = arr1.getJSONObject(j).getJSONObject("Details").getJSONObject("Stops").getString("StopQuantity");

                                FlightModel fmodel = new FlightModel();
                                fmodel.setDepartureAirport(DepartureAirport);
                                fmodel.setArrivalAirport(ArrivalAirport);
                                fmodel.setDuration(Duration);
                                fmodel.setDeparturTimee(DepartureTime);
                                fmodel.setArrivalTime(ArrivalTime);
                                fmodel.setStops(" One Stop ");
                                fmodel.Stops = Stops;

                                itemList.add(fmodel);

                                System.out.println("DEPT : " + DepartureAirport + " Arrival " + ArrivalAirport +
                                        " Duration : " + Duration + " Dept time : " + DepartureTime + " Arr Time " + ArrivalTime
                                        + " Stops " + Stops);

                            }
                        }


                    } else if (intervention instanceof JSONObject) {
                        // It's an object
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
                        fmodel.Stops = Stops;


                        itemList.add(fmodel);

                        System.out.println("DEPT : " + DepartureAirport + " Arrival " + ArrivalAirport +
                                " Duration : " + Duration + " Dept time : " + DepartureTime + " Arr Time " + ArrivalTime
                                + " Stops " + Stops);

                    } else {
                        Toast.makeText(FlightListActivity.this, Config.NO_SELECTIONS_AVAILABLE, Toast.LENGTH_LONG).show();

                    }

                }

                // Setup and Handover data to recyclerview
                final FlightAdapter adapter = new FlightAdapter(itemList, FlightListActivity.this,dest);
                flights_rv.setAdapter(adapter);

            } else {
                Toast.makeText(FlightListActivity.this, Config.POOR_NETWORK_CONNECTION, Toast.LENGTH_LONG).show();

            }
        } catch (JSONException r) {
            System.out.println("ERROR PROB --> : " + r);

        }

    }

}
