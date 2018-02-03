package ug.flights.huza.flightsapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Huzy_Kamz on 2/1/2018.
 */

public class DataProvider extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1
            ;
    public static final String DATABASE_NAME = "flights_db.db";

    String SQL_CREATE_FLIGHT_SCHEDULES = "CREATE TABLE schedules(_ID INT PRIMARY KEY,DepartureAirport TEXT , ArrivalAirport TEXT , Duration TEXT," +
            "DeparturTime TEXT  ,ArrivalTime TEXT ,Stops TEXT,DirectFlights TEXT, DateToday TEXT )";
    String SQL_CREATE_AIRPORT_CODE = "CREATE TABLE airportcodes(_ID INT PRIMARY KEY,json TEXT)";

    String SQL_CREATE_ORIGIN_LATITUDE ="CREATE TABLE latitudeorigin(_ID INT PRIMARY KEY,latitude TEXT )";
    String SQL_CREATE_ORIGIN_LONGITUDE ="CREATE TABLE longitudeorigin(_ID INT PRIMARY KEY,longitude TEXT )";

    String SQL_CREATE_DESTINATION_LATITUDE ="CREATE TABLE latitudedest(_ID INT PRIMARY KEY,latitude TEXT )";
    String SQL_CREATE_DESTINATION_LONGITUDE ="CREATE TABLE longitudedest(_ID INT PRIMARY KEY,longitude TEXT )";

    String SQL_DELETE_FLIGHT_SCHEDULES = "DROP TABLE IF EXISTS schedules";
    String SQL_DELETE_AIRPORT_CODE = "DROP TABLE IF EXISTS airportcodes";
    String SQL_DELETE_ORIGIN_LATITUDE = "DROP TABLE IF EXISTS latitudeorigin";
    String SQL_DELETE_ORIGIN_LONGITUDE ="DROP TABLE IF EXISTS longitudeorigin";
    String SQL_DELETE_DEST_LATITUDE = "DROP TABLE IF EXISTS latitudedest";
    String SQL_DELETE_DEST_LONGITUDE ="DROP TABLE IF EXISTS longitudedest";
    public DataProvider(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_FLIGHT_SCHEDULES);
        db.execSQL(SQL_CREATE_AIRPORT_CODE);
        db.execSQL(SQL_CREATE_ORIGIN_LATITUDE);
        db.execSQL(SQL_CREATE_ORIGIN_LONGITUDE);
        db.execSQL(SQL_CREATE_DESTINATION_LATITUDE);
        db.execSQL(SQL_CREATE_DESTINATION_LONGITUDE);

    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_FLIGHT_SCHEDULES);
        db.execSQL(SQL_DELETE_AIRPORT_CODE);
        db.execSQL(SQL_DELETE_ORIGIN_LATITUDE);
        db.execSQL(SQL_DELETE_ORIGIN_LONGITUDE);
        db.execSQL(SQL_DELETE_DEST_LATITUDE);
        db.execSQL(SQL_DELETE_DEST_LONGITUDE);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


    public String storeAirportCodesAndNames(String json) {

        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("INSERT INTO  airportcodes(json) " +
                    "VALUES ('"+json+"')");
            return "Updates Saved";
        } catch (Exception ex) {
            return "Error! "+ex.getMessage();
        }
    }


    public String storeOriginLatitude(String lat) {

        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("INSERT INTO  latitudeorigin(latitude) VALUES ('"+lat+"')");
            return "Updates Saved";
        } catch (Exception ex) {
            return "Error! "+ex.getMessage();
        }
    }

    public String storeOriginLongitude(String longi) {

        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("INSERT INTO  longitudeorigin  (longitude) VALUES ('"+longi+"')");
            return "Updates Saved";
        } catch (Exception ex) {
            return "Error! "+ex.getMessage();
        }
    }

    public String storeDestLatitude(String lat) {

        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("INSERT INTO  latitudedest  (latitude) VALUES ('"+lat+"')");
            return "Updates Saved";
        } catch (Exception ex) {
            return "Error! "+ex.getMessage();
        }
    }

    public String storeDestLongitude(String longi) {

        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("INSERT INTO  longitudedest  (longitude) VALUES ('"+longi+"')");
            return "Updates Saved";
        } catch (Exception ex) {
            return "Error! "+ex.getMessage();
        }
    }
    public void UpdateLatitudeOrigin() {

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("delete from latitudeorigin ");
            //return "Updates Saved";
        } catch (Exception ex) {
            // return "Error! " + ex.getMessage();

        }

    }

    public void UpdateLongitudeOrigin() {

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("delete from longitudeorigin ");
            //return "Updates Saved";
        } catch (Exception ex) {
            // return "Error! " + ex.getMessage();

        }

    }

    public void UpdateLatitudeDest() {

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("delete from latitudedest");
            //return "Updates Saved";
        } catch (Exception ex) {
            // return "Error! " + ex.getMessage();

        }

    }
    public void UpdateLongitudeDest (){

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("delete from longitudedest ");
            //return "Updates Saved";
        } catch (Exception ex) {
            // return "Error! " + ex.getMessage();

        }

    }

    public String GetOriginLatitude(){
        String selectQuery = "SELECT latitude FROM latitudeorigin";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null && c.moveToFirst()) {
            return  c.getString(c.getColumnIndex("latitude")).toUpperCase();
        }
        else

        return "0.0000";
    }
    public String GetOriginLongitude(){
        String selectQuery = "SELECT longitude FROM longitudeorigin";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null && c.moveToFirst()) {
            return  c.getString(c.getColumnIndex("longitude")).toUpperCase();
        }
        else

            return "0.0000";
    }
    public String GetDestLatitude(){

        String selectQuery = "SELECT latitude FROM latitudedest";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null && c.moveToFirst()) {
            return  c.getString(c.getColumnIndex("latitude")).toUpperCase();
        }
        else

            return "0.0000";
    }
    public String GetDestLongitude(){
        String selectQuery = "SELECT longitude FROM longitudedest";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null && c.moveToFirst()) {
            return  c.getString(c.getColumnIndex("longitude")).toUpperCase();
        }
        else

            return "0.0000";
    }



    public String storeFlightsinSql(String departureAirport, String arrivalAirport, String duration,
                                    String departuretime, String arrivalTime,
                                    String stops,String directFlighgts, String dateToDay) {

        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("INSERT INTO  schedules(DepartureAirport,ArrivalAirport,Duration,DeparturTime" +
                    ",ArrivalTime,Stops,DirectFlights,DateToday) VALUES ('"+departureAirport+"','"+arrivalAirport+"'," +
                    "'"+duration+"','"+departuretime+"','"+arrivalTime
                    +"','"+stops+"','"+directFlighgts+"','"+dateToDay+"')");
            return "Updates Saved";
        } catch (Exception ex) {
            return "Error! "+ex.getMessage();
        }
    }

    public void UpdateFlightsData() {

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("delete from schedules ");
            //return "Updates Saved";
        } catch (Exception ex) {
           // return "Error! " + ex.getMessage();

        }

    }

    public void UpdateAirportCodes() {

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("delete from airportcodes");
            //return "Updates Saved";
        } catch (Exception ex) {
            // return "Error! " + ex.getMessage();

        }

    }


    public JSONArray getFlightSchedulesFromSql() {


        SQLiteDatabase db = this.getReadableDatabase();
        String searchQuery = "SELECT  * FROM schedules";
        Cursor cursor = db.rawQuery(searchQuery, null);

        JSONArray resultSet = new JSONArray();
       // JSONObject returnObj = new JSONObject();

        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {

            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();

            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {

                    try {

                        if (cursor.getString(i) != null) {
                            Log.d("TAG_NAME", cursor.getString(i));
                            rowObject.put(cursor.getColumnName(i), cursor.getString(i));
                        } else {
                            rowObject.put(cursor.getColumnName(i), "");
                        }
                    } catch (Exception e) {
                        Log.d("TAG_NAME", e.getMessage());
                    }
                }

            }

            resultSet.put(rowObject);
            cursor.moveToNext();
        }

        cursor.close();
        Log.d("TAG_NAME", resultSet.toString());
        return resultSet;
    }


    public String getIATAJson(){


        String selectQuery = "SELECT json FROM airportcodes";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null && c.moveToFirst()) {
            return  c.getString(c.getColumnIndex("json")).toUpperCase();
        }
        else
            return "Kampala";

    }

    public JSONArray getAirportcodes() {


        SQLiteDatabase db = this.getReadableDatabase();
        String searchQuery = "SELECT  * FROM airportcodes";
        Cursor cursor = db.rawQuery(searchQuery, null);

        JSONArray resultSet = new JSONArray();
        // JSONObject returnObj = new JSONObject();

        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {

            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();

            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {

                    try {

                        if (cursor.getString(i) != null) {
                            Log.d("TAG_NAME", cursor.getString(i));
                            rowObject.put(cursor.getColumnName(i), cursor.getString(i));
                        } else {
                            rowObject.put(cursor.getColumnName(i), "");
                        }
                    } catch (Exception e) {
                        Log.d("TAG_NAME", e.getMessage());
                    }
                }

            }

            resultSet.put(rowObject);
            cursor.moveToNext();
        }

        cursor.close();
        Log.d("TAG_NAME", resultSet.toString());
        return resultSet;
    }




}
