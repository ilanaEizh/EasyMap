package com.playgame.ilana.easymap;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.playgame.ilana.easymap.UserRegistrationAndLogin.LogInActivity;

public class PlaceList extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    SharedPreferences sharedPreferences;
    private TextView name_user;
    Context context = PlaceList.this;
    private String _university_name;
    private final String TAG = "PlaceList:";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.my_navigation_menu);


        Toolbar toolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);

        //getting user name from sharedPreferences
        TextView name_user = (TextView) findViewById(R.id.name_user);
        sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        String name=  (sharedPreferences.getString("user_name", ""));


        //Activating the operation of the left menu
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //Open left menu
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //Set user name to navigation menu
        TextView txtProfileName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.name_user);
        txtProfileName.setText(name);



        //Verify and request data access GPS and Internet
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ( (ContextCompat.checkSelfPermission(PlaceList.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(PlaceList.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(PlaceList.this, android.Manifest.permission.ACCESS_NETWORK_STATE)
                            != PackageManager.PERMISSION_GRANTED))
            {
                requestPermissions(new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_NETWORK_STATE
                }, 0);
            }
        }
    }

    //Choosing a university and transferring the choice to the next Activity
    public void onClickUniversityJerusalem(View view) {
        _university_name ="Hebrew_University";
        alertDialogOfflineOrOnline();
    }

    public void onClickUniversityTechnion(View view) {
        _university_name ="Technion_University";
        alertDialogOfflineOrOnline();
    }

    public void onClickUniversityBarIlan(View view) {
        _university_name ="Bar_Ilan_University";
        alertDialogOfflineOrOnline();
    }

    public void onClickUniversityTelAviv(View view) {
        _university_name ="Tel_Aviv_University";
        alertDialogOfflineOrOnline();
    }

    public void onClickUniversityAriel(View view) {
        _university_name ="Ariel_University";
        alertDialogOfflineOrOnline();
    }


    private void alertDialogOfflineOrOnline(){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_offline_or_online);

        Button dialogButtonOn = (Button) dialog.findViewById(R.id.btn_online);
        Button dialogButtonOff = (Button) dialog.findViewById(R.id.btn_offline);
        Button dialogButtonCencel = (Button) dialog.findViewById(R.id.btn_cencel);

        dialogButtonOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                openUniverOnline("Online");
            }
        });
        dialogButtonOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                openUniverOffline("Offline");
            }
        });
        dialogButtonCencel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void openUniverOnline(String s){
        Intent intent = new Intent(this, University.class);
        intent.putExtra("_university_name", _university_name);
        intent.putExtra("choose", s);
        startActivity(intent);
        finish();
    }

    private void openUniverOffline(String s){
        Intent intent = new Intent(this, SelectTypseOfData.class);
        intent.putExtra("_university_name", _university_name);
        intent.putExtra("choose", s);
        intent.putExtra("action", "GO");
        startActivity(intent);
        finish();
    }

//Managing the left menu (selected items)
   @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
       Intent intent;
        switch(item.getItemId()) {
//            case R.id.nav_add_place:
//                intent = new Intent(PlaceList.this, AddingNewBuild.class);
//                startActivity(intent);
//                return true;

            case R.id.nav_map:
                intent = new Intent(PlaceList.this, MapsActivity.class);
                startActivity(intent);
                return true;

            case R.id.nav_signout:
                setZero();
               intent = new Intent(PlaceList.this, LogInActivity.class);
                startActivity(intent);
                finish();
                return true;

            case R.id.nav_aboutus:
                setZero();
                intent = new Intent(PlaceList.this, AboutUs.class);
                startActivity(intent);
                return true;
        }
        return false;
    }




    private void setZero(){
        //Delete all data about the user from SharedPreferences
        sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("hasVisited", false); //symbol that the data was saved in SharedPreferences
        editor.putString("user_name", "");
        editor.putString("user_password", "");
        editor.putString("user_email", "");
        editor.commit();
    }
}
