package com.example.testfirebase.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

public class QRcodeScan extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scannerView;
    String resultText = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this); /* Initialize object */
        setContentView(scannerView); /* Set the ScannerView as a content of current activity */
    }
    @Override
    public void onResume() {
        super.onResume();
        /* Asking user to allow access of camera */
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            scannerView.setResultHandler(this); /* Set handler for ZXingScannerView */
            scannerView.startCamera(); /* Start camera */
        } else {
            ActivityCompat.requestPermissions(QRcodeScan.this, new
                    String[]{Manifest.permission.CAMERA}, 1024);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        scannerView.stopCamera(); /* Stop camera */
    }
    @Override
    public void handleResult(Result scanResult) {
        resultText = scanResult.getText(); /* Retrieving text from QR Code */
        try {
            JSONObject jsonObject = new JSONObject(resultText);
            Intent intent = new Intent(this, ShowActiviQRcode.class);
            intent.putExtra("ac_des",jsonObject.getString("ac_des"));
            intent.putExtra("ac_id",jsonObject.getString("ac_id"));
            intent.putExtra("ac_name",jsonObject.getString("ac_name"));
            intent.putExtra("ac_place_name",jsonObject.getString("ac_place_name"));
            intent.putExtra("lat",jsonObject.getString("lat"));
            intent.putExtra("mlong",jsonObject.getString("mlong"));
            intent.putExtra("ac_image",jsonObject.getString("ac_image"));
            startActivity(intent);
        } catch (JSONException e) { e.printStackTrace(); }
        //scannerView.resumeCameraPreview(this);  /* If you want resume scanning, call this method */
        //resultText = null;
        //onBackPressed();
    }
}
