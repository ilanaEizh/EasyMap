package com.playgame.ilana.easymap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;

import com.playgame.ilana.easymap.ReadWritePlacesFromDB.Build;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    //Params for TestDistance
    private String _university_name="";
    private String action="";
    private String _buildings_type="";

    //Threads in Background
    Runnable runnable;
    final Handler handler = new Handler();

    //Params for GoogleMap
    private GoogleMap mMap;
    private static int RADIUS = 100;
    private float zoom = (float) 16.0f;

    JSONArray myjsonArray;

    Build exist_build_ = null;
    private static ArrayList <Build> allBuildings_;
    private Map saveLastDistance = new HashMap<String, Float>();
    private AlertDialogBuldingData alertDialogBuldingData = new AlertDialogBuldingData(MapsActivity.this);

    //User location
    MarkerOptions userMarkerOptions;
    Marker userMarker ;
    Drawable circleDrawable;
    BitmapDescriptor markerIcon;
    LocationListener mLocationListener;

    //
    static Context _ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        _ctx=this;

        allBuildings_ = new ArrayList<>();
        if(getIntent().getStringExtra("_university_name") == null){
            _university_name= "";
        }
        else {
            //_university_name==null  in mode - VIEW USER LOCATION
            //_buildings_type==null    in mode - VIEW ALL BUIlDINGS IN THE MAP -> VIEW ALL BUILDINGS ON THE MAP

            String jsonArray = getIntent().getStringExtra("jsonArray");

            try {
                myjsonArray = new JSONArray(jsonArray);
                getAllBuildings(myjsonArray);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            _university_name = getIntent().getStringExtra("_university_name");
            _buildings_type = getIntent().getStringExtra("_buildings_type");
            if(_buildings_type==null) {
                _buildings_type = "";
            }
            action = getIntent().getStringExtra("action");
            if(action==null) {
                action = "";
            }
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }

    //From jsonArray to ArrayList Buildings
    private void getAllBuildings(JSONArray myjsonArray) {
        try {
            for(int j=0; j<myjsonArray.length(); j++) {
                JSONObject o = myjsonArray.getJSONObject(j);
                Build b = new Build(o.getString("description"),Double.parseDouble(o.getString("latitude")),
                        Double.parseDouble( o.getString("longitude")), o.getString("name"));
                allBuildings_.add(b);
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
    public void onMapReady(GoogleMap googleMap) {
        //Settings for user Icon

        mMap = googleMap;
        mMap.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener) this);

        Drawable circleDrawable = getResources().getDrawable(R.drawable.you_are_here_png);
        BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);
        BitmapDescriptor markerTypeOfBuildings = getMarkerIconFromDrawableForEducation();

        if(_buildings_type.equals("Education")){    markerTypeOfBuildings = getMarkerIconFromDrawableForEducation();    }
        if(_buildings_type.equals("Administration")){    markerTypeOfBuildings = getMarkerIconFromDrawableForAdministration(); }
        if(_buildings_type.equals("Entertainment")){    markerTypeOfBuildings = getMarkerIconFromDrawableForEntertainment();    }
        if(_buildings_type.equals("Religion")){    markerTypeOfBuildings = getMarkerIconFromDrawableForReligion();    }
        if(_buildings_type.equals("Food")){    markerTypeOfBuildings = getMarkerIconFromDrawableForFood(); }


        if(_university_name=="") {
            setUserLocation(markerIcon);
        }
        else{
            //Set all buildings on Map
            for(Build b: allBuildings_){
                LatLng buildingsPosition = new LatLng(b.getLatitude(), b.getLongitude());
                mMap.addMarker(new MarkerOptions().position(buildingsPosition).title("Building "+b.getName()).icon(markerTypeOfBuildings).snippet(b.getDescription()));
                mMap.getUiSettings().setZoomControlsEnabled(true);
                //   mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(buildingsPosition, zoom));
            }
            setUserLocation(markerIcon);
            //Nachat slushat tolko esly ACTION = ONLINE WALKING
            startListener();
        }

    }

    public void setUserLocation(BitmapDescriptor markerIcon) {
        mLocationListener = new LocationListener(MapsActivity.this);
        LatLng userPosition = mLocationListener.getLocation();
        userMarkerOptions = new MarkerOptions().position(userPosition).title("Are you here!").icon(markerIcon);
        userMarker = mMap.addMarker(userMarkerOptions);

        mLocationListener.stopUsingGPS();
        if(_university_name==null || _university_name.equals("")){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userPosition, zoom));
        }
    }

    public void updateUserLocation(LatLng myPosition){
        userMarker.setPosition(myPosition);
    }

    //If on marker click - open alert Dialog with data about Building
    @Override
    public boolean onMarkerClick(Marker marker) {
        if(!marker.getTitle().equals("Are you here!")) {
            alertDialogBuldingData.showBuldingData(marker.getTitle(), marker.getSnippet(), _university_name, _buildings_type, "", "");
            return false;
        }
        return true;
    }

    //Create Custom Icom for user
    private BitmapDescriptor getMarkerIconFromDrawableForEducation() {
        Drawable drawable = getResources().getDrawable(R.drawable.ic_education);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    //Create Custom Icom for user
    private BitmapDescriptor getMarkerIconFromDrawableForAdministration() {
        Drawable drawable = getResources().getDrawable(R.drawable.ic_administration);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    //Create Custom Icom for user
    private BitmapDescriptor getMarkerIconFromDrawableForEntertainment() {
        Drawable drawable = getResources().getDrawable(R.drawable.ic_entertainment);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    //Create Custom Icom for user
    private BitmapDescriptor getMarkerIconFromDrawableForReligion() {
        Drawable drawable = getResources().getDrawable(R.drawable.ic_religion);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    //Create Custom Icom for user
    private BitmapDescriptor getMarkerIconFromDrawableForFood() {
        Drawable drawable = getResources().getDrawable(R.drawable.ic_food);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    //Create Custom Icom for user
    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    //To start a handler that accesses an AsyncTask every 5 seconds
    private void startListener() {
        initializationMapOfLastDistance();
        runnable = new Runnable() {
           public void run() {
               doInBackgroundHandler();
               handler.postDelayed(this, 9000);
           }
       };
       handler.post(runnable);
    }



    protected Void doInBackgroundHandler() {
        //Get user coordinates
        LocationListener myLocationListener = new LocationListener(_ctx);
        LatLng myPosition = myLocationListener.getLocation();
        //Create a variable of type Location for the location of the user and the location of buildings
        Location userLocation = new Location(LocationManager.GPS_PROVIDER);
        Location buildLocation = new Location(LocationManager.GPS_PROVIDER);
        //Set data to location of the user
        userLocation.setLatitude(myPosition.latitude);
        userLocation.setLongitude(myPosition.longitude);
        //Start comparing distance
        for(Build b: allBuildings_){
            buildLocation.setLatitude(b.getLatitude());
            buildLocation.setLongitude(b.getLongitude());
            float distanceInMeters = userLocation.distanceTo(buildLocation);
            if (distanceInMeters<=50){
                Float lastDist = (Float) saveLastDistance.get(b.getName());
                if(lastDist.floatValue()>50 || lastDist.floatValue()==0){
                    onProgressUpdateHandler(b);
                }
            }
            saveLastDistance.put(b.getName(), Float.valueOf(distanceInMeters));
        }
        updateUserLocation(myPosition);
        myPosition =null;
        mLocationListener.stopUsingGPS();
        return null;
    }


    protected void onProgressUpdateHandler(Build build) {
        alertDialogBuldingData = new AlertDialogBuldingData(_ctx);
        alertDialogBuldingData.showBuldingData(build.getName(), build.getDescription(), _university_name,
                                              "","near", "");
    }

    //Initialization MAP with last distance beetwen user and List of buildings
    private void initializationMapOfLastDistance(){
        Float val = 0.00f;
        for(Build b: allBuildings_){
            saveLastDistance.put(b.getName(), val);
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        //Stop Hendeler
        handler.removeCallbacksAndMessages(null);

        Intent intent = new Intent(this, PlaceList.class);
        startActivity(intent);
        finish();
    }

    public static Context getContext(){ return _ctx;}

}
