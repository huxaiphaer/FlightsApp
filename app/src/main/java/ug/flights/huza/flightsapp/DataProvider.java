package ug.flights.huza.flightsapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Huzy_Kamz on 2/1/2018.
 */

public class DataProvider extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "flights_db.db";

    String SQL_CREATE_FLIGHT_SCHEDULES = "CREATE TABLE schedules(_ID INT PRIMARY KEY,DepartureAirport TEXT , ArrivalAirport TEXT , Duration TEXT," +
            "DeparturTime TEXT  ,ArrivalTime TEXT ,Stops TEXT,DirectFlights TEXT, DateToday TEXT )";
    String SQL_CREATE_AIRPORT_CODE = "CREATE TABLE airportcodes(_ID INT PRIMARY KEY,json TEXT)";

    String SQL_CREATE_ORIGIN_LATITUDE = "CREATE TABLE latitudeorigin(_ID INT PRIMARY KEY,latitude TEXT )";
    String SQL_CREATE_ORIGIN_LONGITUDE = "CREATE TABLE longitudeorigin(_ID INT PRIMARY KEY,longitude TEXT )";

    String SQL_CREATE_DESTINATION_LATITUDE = "CREATE TABLE latitudedest(_ID INT PRIMARY KEY,latitude TEXT )";
    String SQL_CREATE_DESTINATION_LONGITUDE = "CREATE TABLE longitudedest(_ID INT PRIMARY KEY,longitude TEXT )";

    String SQL_DELETE_FLIGHT_SCHEDULES = "DROP TABLE IF EXISTS schedules";
    String SQL_DELETE_AIRPORT_CODE = "DROP TABLE IF EXISTS airportcodes";
    String SQL_DELETE_ORIGIN_LATITUDE = "DROP TABLE IF EXISTS latitudeorigin";
    String SQL_DELETE_ORIGIN_LONGITUDE = "DROP TABLE IF EXISTS longitudeorigin";
    String SQL_DELETE_DEST_LATITUDE = "DROP TABLE IF EXISTS latitudedest";
    String SQL_DELETE_DEST_LONGITUDE = "DROP TABLE IF EXISTS longitudedest";

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

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("INSERT INTO  airportcodes(json) " +
                    "VALUES ('" + json + "')");
            return "Updates Saved";
        } catch (Exception ex) {
            return "Error! " + ex.getMessage();
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


}
