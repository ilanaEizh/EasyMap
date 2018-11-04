package com.playgame.ilana.easymap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.playgame.ilana.easymap.ReadWriteFromSQLite.SQLiteHelper;
import com.playgame.ilana.easymap.ReadWritePlacesFromDB.Build;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OfflineMode extends AppCompatActivity {

    private ImageView image_container;
    private TextView title;

    private final String TAG = "OfflineMode:";

    //Params for TestDistance
    private String _university_name="";
    private String action="";
    private String _buildings_type="";

    //Threads in Background
    Runnable runnable;
    final Handler handler = new Handler();

    static Context _ctx;

    private static ArrayList <Build> allBuildings_;
    private Map saveLastDistance = new HashMap<String, Float>();
    private AlertDialogBuldingData alertDialogBuldingData = new AlertDialogBuldingData(OfflineMode.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_mode);

        title = (TextView) findViewById(R.id.tv_title);
        image_container = (ImageView) findViewById(R.id.image_container);

        _ctx=this;
        _university_name= getIntent().getStringExtra("_university_name");
        _buildings_type = getIntent().getStringExtra("_buildings_type");

        if(_university_name.equals("Hebrew_University")){
            image_container.setImageResource(R.drawable.jerusalem_fon);
            title.setText(R.string.university_jerusalem);
        }
        if(_university_name.equals("Technion_University")){
            image_container.setImageResource(R.drawable.tehnion_fon);
            title.setText(R.string.university_technion);
        }
        if(_university_name.equals("Bar_Ilan_University")){
            image_container.setImageResource(R.drawable.barilan_fon);
            title.setText(R.string.university_bar_ilan);
        }
        if(_university_name.equals("Tel_Aviv_University")){
            image_container.setImageResource(R.drawable.telaviv_fon);
            title.setText(R.string.university_tel_aviv);
        }
        if(_university_name.equals("Ariel_University")){
            image_container.setImageResource(R.drawable.ariel_fon);
            title.setText(R.string.university_ariel);
        }
        if(getAllBuildings()==true){
            startListener();
        }
        else{
            Toast.makeText(_ctx, "You must download data "+_university_name+" - "+_buildings_type, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, PlaceList.class);
            startActivity(intent);
            finish();
        }

    }

    //From SQLite to ArrayList Buildings
    private boolean getAllBuildings() {
        SQLiteHelper helper = new SQLiteHelper(_ctx);

        if (helper.isEmpty(_university_name, _buildings_type) != true) {
            allBuildings_ = helper.getTable(_university_name, _buildings_type);
            return true;
        }
        else{
            return false;
        }
    }

    //To start a handler that accesses an AsyncTask every 5 seconds
    private void startListener() {
        initializationMapOfLastDistance();
        runnable = new Runnable() {
            public void run() {
                doInBackgroundHandler();

                handler.postDelayed(this, 9000);
                Log.d(TAG, "!!!!!!!!!!!!!! RUN HENDLER ");
            }
        };
        handler.post(runnable);
    }


    //Initialization MAP with last distance beetwen user and List of buildings
    private void initializationMapOfLastDistance(){
        Float val = 0.00f;
        for(Build b: allBuildings_){
            saveLastDistance.put(b.getName(), val);
        }
    }


    protected void doInBackgroundHandler() {
        LocationListener myLocationListener = new LocationListener(_ctx);
        LatLng myPosition = myLocationListener.getLocation();

        if(myPosition==null){
            showSettingsAlert();
            return;
        }
        Location userLocation = new Location(LocationManager.GPS_PROVIDER);
        Location buildLocation = new Location(LocationManager.GPS_PROVIDER);

        userLocation.setLatitude(myPosition.latitude);
        userLocation.setLongitude(myPosition.longitude);

        Date currentTime = Calendar.getInstance().getTime();
        Log.d("GPS", "GPS data: ["+currentTime.toString()+"] lan="+userLocation.getLatitude()+", lon="+userLocation.getLongitude());



        for(Build b: allBuildings_){
            buildLocation.setLatitude(b.getLatitude());
            buildLocation.setLongitude(b.getLongitude());
            float distanceInMeters = userLocation.distanceTo(buildLocation);
            if (distanceInMeters<=50){
                // Float lastDist = null;
                Float lastDist = (Float) saveLastDistance.get(b.getName());

                if(lastDist.floatValue()>50 || lastDist.floatValue()==0){
                    onProgressUpdateHandler(b);
                }
            }
            saveLastDistance.put(b.getName(), Float.valueOf(distanceInMeters));
        }
        myPosition =null;
        myLocationListener.stopUsingGPS();
    }


    protected void onProgressUpdateHandler(Build build) {
        alertDialogBuldingData = new AlertDialogBuldingData(_ctx);
        alertDialogBuldingData.showBuldingData(build.getName(), build.getDescription(), _university_name, "","near", "offline");
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(_ctx);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("Problem with GPS. Exit the room and try again.");


        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                onBackPressed();
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    public void onBackPressed(){
        super.onBackPressed();
        //Stop Hendeler and AsynTask
        handler.removeCallbacksAndMessages(null);

        Intent intent = new Intent(this, PlaceList.class);
        startActivity(intent);
        finish();
    }

}
