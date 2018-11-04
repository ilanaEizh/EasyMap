package com.playgame.ilana.easymap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.provider.Settings;

public class CheckInternetConnection {
    Context _ctx;

    public CheckInternetConnection(Context _ctx){
        this._ctx=_ctx;
    }


    public boolean testInternetConnect(){
        if (!checkInternetConnection()) {
            //Poprosit vkluchit

            AlertDialog.Builder dialog = new AlertDialog.Builder(_ctx);
            dialog.setMessage("Internet not available, Cross check your internet connectivity and try again");
            dialog.setPositiveButton(_ctx.getResources().getString(R.string.open_internet_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    //Vkluchit internet
                    Intent myIntent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);  //ACTION_LOCATION_SOURCE_SETTINGS
                    _ctx.startActivity(myIntent);
                }
            });
            dialog.setNegativeButton(_ctx.getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                }
            });
            dialog.show();
            return false;
        }
        return true;
    }

    public boolean checkInternetConnection() {
        try {
            ConnectivityManager conMgr = (ConnectivityManager) _ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected())
                return true;
            else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
