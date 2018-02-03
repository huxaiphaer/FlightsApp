package ug.flights.huza.flightsapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import model.AirportCodesModel;

public class MainActivity extends AppCompatActivity {

    Context context = this;
    Calendar myCalendar = Calendar.getInstance();
    String dateFormat = "yyyy-MM-dd";
    DatePickerDialog.OnDateSetListener date;
    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String MY_TOKEN = "AccessToken";
    public String MY_JSON = "json";
    SharedPreferences sharedpreferences;
    String mytokenFromSharedPref = "";
    private Spinner from, to;
    private EditText date_edt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Spinners initialization
        from = (Spinner) findViewById(R.id.origin_sp);

        to = (Spinner) findViewById(R.id.destination_sp);


        //Initializing Shared Prefeerences
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        retrieveAccessTokenfromServer();

        //get data from  share pref
        mytokenFromSharedPref = sharedpreferences.getString(MainActivity.MY_TOKEN, null);

        date_edt = (EditText) findViewById(R.id.date_edt);

        date_edt = (EditText) findViewById(R.id.date_edt);
        // init - set date to current date
        long currentdate = System.currentTimeMillis();
        String dateString = sdf.format(currentdate);
        date_edt.setText(dateString);

        // set calendar date and update editDate
        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDate();
            }

        };

        // onclick - popup datepicker
        date_edt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(context, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        populateSpinnersWithIATACodes();

    }

    private void updateDate() {
        date_edt.setText(sdf.format(myCalendar.getTime()));
    }


    public void onSearchFlights(View v) {
        String GetOrigin = from.getSelectedItem().toString();
        String GetDestination = to.getSelectedItem().toString();
        String FlightsDate = date_edt.getText().toString();

        System.out.print("Origin " + GetOrigin + " Dest : " + GetDestination);
        Intent i = new Intent(MainActivity.this, FlightListActivity.class);
        i.putExtra("origin", GetOrigin);
        i.putExtra("destination", GetDestination);
        i.putExtra("dateflights", FlightsDate);


        startActivity(i);
    }


    private void populateSpinnersWithIATACodes() {

        Ion.with(MainActivity.this)
                .load(Config.URL_IATA_CODES)
                .setHeader("Accept", "application/json")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, final JsonObject result) {


                        try {
                            if (result.toString() != null) {
                                DataProvider dp = new DataProvider(MainActivity.this);
                                dp.UpdateAirportCodes();
                                dp.storeAirportCodesAndNames(result.toString());

                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString(MY_JSON, result.toString());
                                editor.commit();


                                //Retriving from shared pref

                                String storedJson = "";
                                SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                                storedJson = sharedpreferences.getString(MY_JSON, null);

                                parseJson(storedJson);

                            } else {
                                Toast.makeText(MainActivity.this, Config.POOR_NETWORK_CONNECTION, Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception t) {
                            Toast.makeText(MainActivity.this, Config.POOR_NETWORK_CONNECTION, Toast.LENGTH_LONG).show();
                        }

                    }
                });

    }


    private void parseJson(String result) {
        try {
            if (result != null) {

                JSONObject obj = new JSONObject(result);
                JSONArray arr = obj.getJSONArray("response");
                List<AirportCodesModel> modelClassList = new ArrayList<>();
                for (int i = 0; i < arr.length(); i++) {
                    String AirportCode = arr.getJSONObject(i).getString("code");
                    String Name = arr.getJSONObject(i).getString("name");

                    AirportCodesModel modelClass = new AirportCodesModel();
                    modelClass.setAirportCode(AirportCode);
                    modelClass.setCodeName(Name);
                    modelClassList.add(modelClass);

                    System.out.println(" --> " + "Airport : " + AirportCode + " Name " + Name);

                }
                final List<String> items = new ArrayList<String>();

                for (int i = 0; i < modelClassList.size(); i++) {
                    items.add(modelClassList.get(i).getAirportCode() + " - " + modelClassList.get(i).getCodeName());
                    //Origin spinner
                    ArrayAdapter<String> originAdp = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, items);
                    originAdp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // Attach the Adapter.
                    from.setAdapter(originAdp);

                    //Destination spinner
                    ArrayAdapter<String> destinationAdap = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, items);
                    destinationAdap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // Attach the Adapter.
                    to.setAdapter(destinationAdap);


                }
            } else {
                Toast.makeText(MainActivity.this, Config.POOR_NETWORK_CONNECTION, Toast.LENGTH_LONG).show();

            }
        } catch (JSONException r) {
            System.out.println("ERROR IN JSON --->  : " + r);

        }

    }


    public void retrieveAccessTokenfromServer() {

        String tokenURL = Config.TOKEN_URL;
        final ProgressDialog pd = new ProgressDialog(MainActivity.this);
        pd.setMessage("Loading ` ...");
        pd.show();
        Ion.with(this)
                .load("POST", tokenURL)
                .setHeader("Content-Type", "application/x-www-form-urlencoded")
                .progressDialog(pd)
                .setBodyParameter("client_id", Config.CLIENT_ID)
                .setBodyParameter("client_secret", Config.CLIENT_SECRET)
                .setBodyParameter("grant_type", Config.CLIENT_CREDENTIALS)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null) {

                            try {
                                String RES = "[" + result.toString() + "]";
                                JSONArray ar = new JSONArray(RES);
                                for (int i = 0; i < ar.length(); i++) {
                                    JSONObject obj = ar.getJSONObject(i);
                                    String AccessToken = obj.getString("access_token");
                                    //saving my token in shared preference
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putString(MY_TOKEN, AccessToken);
                                    editor.commit();

                                    // View token in logcat
                                    System.out.println("Access Token : " + AccessToken);
                                    pd.dismiss();


                                }
                            } catch (JSONException ex) {
                                System.out.println("Access Token : " + ex);
                                pd.dismiss();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, Config.POOR_NETWORK_CONNECTION, Toast.LENGTH_LONG).show();
                            System.out.println("MY EXCEPTION : " + e);
                            pd.dismiss();
                        }
                    }
                });


    }
}
