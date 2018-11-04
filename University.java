package com.playgame.ilana.easymap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.playgame.ilana.easymap.Gallery.GalleryFromFireBase;

public class University extends AppCompatActivity {

    private ImageView image_container;
    private TextView title;
    private String _university_name;
    private String choose;
    private Button btn_start;


   // DownloadData(Context _cnt, String _university_name)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_university);
        _university_name =  getIntent().getStringExtra("_university_name");
        choose =  getIntent().getStringExtra("choose");
        btn_start= (Button) findViewById(R.id.start_get_data);
        title = (TextView) findViewById(R.id.tv_title);
        image_container = (ImageView) findViewById(R.id.image_container);

        //Set pct fon
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

        if(choose.equals("Online")){
            btn_start.setText(" Online walking ");

        }
        else{
            btn_start.setText(" Offline walking ");
        }
    }

    //StartWalk peredaet
    public void onClickStartWalk(View view) {
        Intent intent = new Intent(this, SelectTypseOfData.class);
        intent.putExtra("_university_name", _university_name);
        intent.putExtra("choose", choose);  //Or offline or online
        intent.putExtra("action", "GO");
        startActivity(intent);
        finish();
    }

    //Download data
    public void onClickDownloadData(View view) {
        Intent intent = new Intent(this, SelectTypseOfData.class);
        intent.putExtra("action", "Download");
        intent.putExtra("_university_name", _university_name);
        intent.putExtra("choose", choose);
        startActivity(intent);
        finish();
    }

    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, PlaceList.class);
        startActivity(intent);
        finish();
    }


    public void getImages(View view) {
        Intent intent = new Intent(this, GalleryFromFireBase.class);
        intent.putExtra("_university_name", _university_name);
        startActivity(intent);
    }
}
