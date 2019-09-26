package com.example.testfirebase.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.testfirebase.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom;

public class ShowActiviQRcode extends AppCompatActivity {
    private ImageView imageView;
    private TextView txt_name,txt_des,txt_place_name;
    private MapView mapView;
    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_activi_qrcode);

        imageView = findViewById(R.id.imageView_qrcode);
        txt_name = findViewById(R.id.txt_qrcode_name);
        txt_des = findViewById(R.id.txt_qrcode_des);
        txt_place_name = findViewById(R.id.txt_qrcode_placename);
        mapView = findViewById(R.id.mapView);
        MapsInitializer.initialize(this);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        txt_name.setText(getIntent().getExtras().getString("ac_name"));
        txt_des.setText(getIntent().getExtras().getString("ac_des"));
        txt_place_name.setText(getIntent().getExtras().getString("ac_place_name"));
        Glide.with(ShowActiviQRcode.this).load(getIntent().getExtras().getString("ac_image")).into(imageView);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.setOnMyLocationButtonClickListener(onMyLocationButtonClickListener);
                mMap.setOnMyLocationClickListener(onMyLocationClickListener);
                enableMyLocationIfPermitted();
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                LatLng latLng = new LatLng(
                        Double.parseDouble(getIntent().getExtras().getString("lat")),
                        Double.parseDouble(getIntent().getExtras().getString("mlong"))
                );
                MarkerOptions marker = new MarkerOptions().position(latLng).title(getIntent().getExtras().getString("ac_name"));
                mMap.moveCamera(newLatLngZoom(latLng, 18));
                mMap.addMarker(marker);
            }
        });
    }
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.other_page,menu);
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.home_page:
                startActivity(new Intent(this, MainActivity.class));

                return true;
        }
        return  super.onOptionsItemSelected(item);
    }
    private void enableMyLocationIfPermitted() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else if (mMap != null) { mMap.setMyLocationEnabled(true); }
    }
    private GoogleMap.OnMyLocationButtonClickListener onMyLocationButtonClickListener = new GoogleMap.OnMyLocationButtonClickListener() {
        @Override
        public boolean onMyLocationButtonClick() { return false; }
    };
    private GoogleMap.OnMyLocationClickListener onMyLocationClickListener = new GoogleMap.OnMyLocationClickListener() {
        @Override
        public void onMyLocationClick(@NonNull Location location) {
            //autoStart.setText("I'm here");
        }
    };
    private void showDefaultLocation() {
        Toast.makeText(this, "Location permission not granted, " + "showing default location", Toast.LENGTH_SHORT).show();
        LatLng build100 = new LatLng(14.3472549, 100.5653264);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(build100));
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocationIfPermitted();
                } else { showDefaultLocation(); }
                return;
            }
        }
    }
}
