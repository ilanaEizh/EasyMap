package com.playgame.ilana.easymap;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.playgame.ilana.easymap.ReadWriteFromSQLite.DownloadData;
import com.playgame.ilana.easymap.ReadWritePlacesFromDB.Build;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SelectTypseOfData extends AppCompatActivity {

    private RadioGroup radioGroup;
    private TextView title;
    private Button btn_action;

    private String _university_name;
    private String _buildings_type = "";
    private String choose;

    private String action;

    //Params for FirebaseDatabase
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mRootRef;
    private final String TAG = "ReadBuildingsFrom:";

    JSONArray myJSONArray = new JSONArray();

    // flag for network status
    boolean isNetworkEnabled = false;
    Context _ctx = SelectTypseOfData.this;

    Build exist_build_ = null;
    CheckGPSConnection myAlertGPS = new CheckGPSConnection(SelectTypseOfData.this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_typse_of_data);

        _university_name = getIntent().getStringExtra("_university_name");
        choose = getIntent().getStringExtra("choose");
        action = getIntent().getStringExtra("action");

        btn_action = (Button) findViewById(R.id.btn_action);
        btn_action.setText(action);
        title = (TextView) findViewById(R.id.university_name);
        title.setText(_university_name.replace('_', ' '));
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup_BuildingsType);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_btn_Education:
                        _buildings_type = "Education";
                        break;
                    case R.id.radio_btn_Administration:
                        _buildings_type = "Administration";
                        break;
                    case R.id.radio_btn_Entertainment:
                        _buildings_type = "Entertainment";
                        break;
                    case R.id.radio_btn_Religion:
                        _buildings_type = "Religion";
                        break;
                    case R.id.radio_btn_Food:
                        _buildings_type = "Food";
                        break;
                }
            }
        });
    }



    //Button
    public void start_onClick(View view) {
        if (!_buildings_type.equals(""))
        {

            //Offline mode
            if (action.equals("GO") && choose.equals("Offline")) {  //If GPS enable
                if(myAlertGPS.checkGPSEnable()){
                    Intent intent = new Intent(this, OfflineMode.class);

                    intent.putExtra("_university_name", _university_name);
                    intent.putExtra("_buildings_type", _buildings_type);
                    startActivity(intent);
                    finish();
                }
                else{
                    myAlertGPS.showSettingsAlert();

                }
            }
            //Online mode
            else {
                CheckInternetConnection myAlert = new CheckInternetConnection(SelectTypseOfData.this);
                //Proverit internet. I dlya Download i dlya Online Go
                if( myAlert.testInternetConnect()){
                    if (action.equals("Download")) {
                        DownloadData download = new DownloadData(getApplicationContext(), _university_name, _buildings_type);
                        onBackPressed();
                    }
                    if (action.equals("GO") && choose.equals("Online")) {
                        ProgressDialog progressDialog = new ProgressDialog(SelectTypseOfData.this,R.style.MyTheme);
                        progressDialog.setCancelable(false);
                        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
                        progressDialog.show();
                        readFromDB();
                    }
                }
            }
        }
        else{
            Toast.makeText(SelectTypseOfData.this, "You must select a subject", Toast.LENGTH_SHORT).show();
        }
    }


    //Read all Buildings from FireBase in ArrayList. For use in MapActivity
    private void readFromDB() {
        mRootRef=database.getReference();
        mRootRef.child("Universities").child(_university_name).child(_buildings_type)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                exist_build_ = child.getValue(Build.class);
                                try {
                                    JSONObject myJSONObjectBuildings= new JSONObject();
                                    myJSONObjectBuildings.put("name", exist_build_.getName());
                                    myJSONObjectBuildings.put("longitude", exist_build_.getLongitude());
                                    myJSONObjectBuildings.put("latitude", exist_build_.getLatitude());
                                    myJSONObjectBuildings.put("description", exist_build_.getDescription());

                                    myJSONArray.put(myJSONObjectBuildings);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                        openMap();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, " " + databaseError.getDetails());
                        throw databaseError.toException();
                    }
                });
    }

    private void openMap() {
        Intent intent = new Intent(this, MapsActivity.class);

        intent.putExtra("jsonArray", myJSONArray.toString());
        intent.putExtra("_university_name", _university_name);
        intent.putExtra("_buildings_type", _buildings_type);
        intent.putExtra("action", "walk");
        startActivity(intent);
        finish();
    }



    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(this, PlaceList.class);
        startActivity(intent);
        finish();
    }
}

