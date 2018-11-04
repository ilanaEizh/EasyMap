package com.playgame.ilana.easymap;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.playgame.ilana.easymap.ReadWritePlacesFromDB.AddNewDataAboutBuilding;

public class AlertDialogBuldingData extends AppCompatActivity {
    private Context _ctx;
    private String _buildings_type;
    private String _university_name;
    private String _building_name;



    public AlertDialogBuldingData(Context _ctx){
        this._ctx=_ctx;
    }

    public void showBuldingData(final String _building_name, String description, final String _university_name,
                                String _buildings_type, String action, String mode) {
        final Dialog dialog = new Dialog(_ctx);

        this._building_name=_building_name;
        this._university_name=_university_name;
        this._buildings_type=_buildings_type;

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //  dialog.setCancelable(false);
        dialog.setContentView(R.layout.building_data);

        Button dialogAddData = (Button) dialog.findViewById(R.id.btn_add_new_data);
        Button dialogButtonCencel = (Button) dialog.findViewById(R.id.btn_cencel);

        ImageView imageContainer = (ImageView) dialog.findViewById(R.id.img_container);
        TextView buildingName = (TextView) dialog.findViewById(R.id.building_name);
        TextView buildingNear = (TextView) dialog.findViewById(R.id.building_near);
        TextView buildingDescription = (TextView) dialog.findViewById(R.id.building_description);
        if (mode.equals("offline")){
            dialogAddData.setVisibility(View.GONE);
        }
        if(action.equals("near")){
            buildingNear.setText("You are near the:");
        }

        if(_university_name.equals("Hebrew_University")){
            imageContainer.setImageResource(R.drawable.jerusalem_fon);
        }
        if(_university_name.equals("Technion_University")){
            imageContainer.setImageResource(R.drawable.tehnion_fon);
        }
        if(_university_name.equals("Bar_Ilan_University")){
            imageContainer.setImageResource(R.drawable.barilan_fon);
        }
        if(_university_name.equals("Tel_Aviv_University")){
            imageContainer.setImageResource(R.drawable.telaviv_fon);
        }
        if(_university_name.equals("Ariel_University")){
            imageContainer.setImageResource(R.drawable.ariel_fon);
        }
        buildingName.setText(_building_name);
        buildingDescription.setText(description);


        dialogAddData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(_ctx, AddNewDataAboutBuilding.class);
                intent.putExtra("_university_name", _university_name);
                intent.putExtra("_building_name", _building_name);

                _ctx.startActivity(intent);
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




}
