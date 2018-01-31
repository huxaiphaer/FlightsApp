package ug.flights.huza.flightsapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText date_edt;
    Context context = this;
    Calendar myCalendar = Calendar.getInstance();
    String dateFormat = "yyyy-MM-dd";
    DatePickerDialog.OnDateSetListener date;
    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String MY_TOKEN = "AccessToken";
    SharedPreferences sharedpreferences;
    private CheckBox directflight_chkbx;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Initializing Shared Prefeerences
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        retrieveAccessTokenfromServer();


        date_edt = (EditText)findViewById(R.id.date_edt);
        directflight_chkbx= (CheckBox)findViewById(R.id.directflight_chkbx);
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

    }

    private void updateDate() {
        date_edt.setText(sdf.format(myCalendar.getTime()));
    }



    public String CheckDirectFlights(){

        String check = "0";
        if (directflight_chkbx.isChecked()){
            check ="1";
        }
        return check;
    }

    public void onSearchFlights(View v){
        String GetOrigin = "ZRH";
        String GetDestination ="FRA";
        String FlightsDate = date_edt.getText().toString();
        String CheckFlights= CheckDirectFlights();
        Intent i = new Intent(MainActivity.this,FlightListActivity.class);
        i.putExtra("origin",GetOrigin);
        i.putExtra("destination",GetDestination);
        i.putExtra("dateflights",FlightsDate);
        i.putExtra("checkflights",CheckFlights);
        startActivity(i);
    }

    public void  retrieveAccessTokenfromServer(){

        String tokenURL = Config.TOKEN_URL;
        final ProgressDialog pd = new ProgressDialog(MainActivity.this);
        pd.setMessage("Checking Flights ...");
        pd.show();
        Ion.with(this)
                .load("POST",tokenURL)
                .setHeader("Content-Type","application/x-www-form-urlencoded")
                .progressDialog(pd)
                .setBodyParameter("client_id",Config.CLIENT_ID)
                .setBodyParameter("client_secret",Config.CLIENT_SECRET)
                .setBodyParameter("grant_type",Config.CLIENT_CREDENTIALS)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result !=null) {

                            try {
                                String RES = "[" + result.toString() + "]";
                                JSONArray ar = new JSONArray(RES);
                                for (int i = 0; i < ar.length(); i++) {
                                    JSONObject obj = ar.getJSONObject(i);
                                    String AccessToken = obj.getString("access_token");
                                    //saving my token in shared preference
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putString(MY_TOKEN,AccessToken);
                                    editor.commit();

                                    // View token in logcat
                                    System.out.println("Access Token : " + AccessToken);
                                    pd.dismiss();


                                }
                            }
                            catch (JSONException ex)
                            {
                                System.out.println("Access Token : "+ ex);
                                pd.dismiss();
                            }
                        }

                        else {
                            Toast.makeText(MainActivity.this,Config.POOR_NETWORK_CONNECTION,Toast.LENGTH_LONG).show();
                            System.out.println("MY EXCEPTION : "+ e);
                            pd.dismiss();
                        }
                    }
                });



    }
}
