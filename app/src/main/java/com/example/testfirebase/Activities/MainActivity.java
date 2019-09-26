package com.example.testfirebase.Activities;

import android.Manifest;
import android.app.Dialog;
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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.example.testfirebase.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import java.util.HashMap;

import static com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom;

public class MainActivity extends AppCompatActivity implements
        BaseSliderView.OnSliderClickListener,
        ViewPagerEx.OnPageChangeListener{
    private Button /* btn_toilet,*/btn_location,btn_destination,btn_test;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private GoogleMap mMap;
    SliderLayout sliderLayout;
    HashMap<String, String> HashMapForURL ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_location = findViewById(R.id.btn_show_location);
        //btn_toilet = findViewById(R.id.btn_show_toilet);
        btn_destination = findViewById(R.id.btn_destination);
        btn_test = findViewById(R.id.btn_test);
        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LocationDisplay.class));
            }
        });
       /* btn_toilet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ToiletDisplay.class));
            }
        });*/
        btn_destination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ChooseDestinationActivity.class));
            }
        });
        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),testActivity.class));
            }
        });
        sliderLayout = findViewById(R.id.slider);
        sliderLayout.startAutoCycle();
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference("Activity");
        HashMapForURL = new HashMap<String, String>();
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    HashMapForURL.put(postSnapshot.child("ac_name").getValue(String.class),postSnapshot.child("ac_image").getValue(String.class));
                    TextSliderView textSliderView = new TextSliderView(MainActivity.this);
                    textSliderView
                            .description(postSnapshot.child("ac_name").getValue(String.class))
                            .image(postSnapshot.child("ac_image").getValue(String.class))
                            .setScaleType(BaseSliderView.ScaleType.Fit)
                            .setOnSliderClickListener(MainActivity.this);
                    textSliderView.bundle(new Bundle());
                    textSliderView.getBundle().putString("id",postSnapshot.child("ac_id").getValue(String.class));
                    sliderLayout.addSlider(textSliderView);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.DepthPage);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setDuration(3000);
        sliderLayout.addOnPageChangeListener(MainActivity.this);
    }
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.type,menu);
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.tab_2:
                startActivity(new Intent(this, LoginAdmin.class));
                return true;
            case R.id.home_page:
                startActivity(new Intent(this,MainActivity.class));

                return true;
            case R.id.scan_qr_code:
                startActivity(new Intent(this, QRcodeScan.class));
        }
        return  super.onOptionsItemSelected(item);
    }
    @Override
    protected void onStop() { sliderLayout.stopAutoCycle();
        super.onStop();
    }
    @Override
    public void onSliderClick(final BaseSliderView slider) {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.setContentView(R.layout.show_dialog_activitie);

        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.95);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.92);
        dialog.getWindow().setLayout(width,height);

        final TextView txt_ac_name = dialog.findViewById(R.id.txt_dialog_ac_name);
        final TextView txt_ac_des = dialog.findViewById(R.id.txt_dialog_ac_des);
        final TextView txt_ac_place = dialog.findViewById(R.id.txt_dialog_ac_place);
        final ImageView img_ac = dialog.findViewById(R.id.image_activitie_dialog);
        final Button btn_dismiss = dialog.findViewById(R.id.btn_back);

        final MapView mapView = dialog.findViewById(R.id.mapView);
        MapsInitializer.initialize(MainActivity.this);
        mapView.onCreate(dialog.onSaveInstanceState());
        mapView.onResume();

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                mMap = googleMap;
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                mMap.setOnMyLocationButtonClickListener(onMyLocationButtonClickListener);
                mMap.setOnMyLocationClickListener(onMyLocationClickListener);
                enableMyLocationIfPermitted();
                mMap.getUiSettings().setZoomControlsEnabled(true);
                Query query = FirebaseDatabase.getInstance().getReference("Activity")
                        .orderByChild("ac_id")
                        .equalTo(slider.getBundle().get("id").toString());
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            txt_ac_name.setText(dataSnapshot1.child("ac_name").getValue(String.class));
                            txt_ac_des.setText("รายละเอียด: "+dataSnapshot1.child("ac_des").getValue(String.class));
                            txt_ac_place.setText("สถานที่: "+dataSnapshot1.child("ac_place_name").getValue(String.class));
                            Picasso.with(MainActivity.this).load(dataSnapshot1.child("ac_image").getValue(String.class)).fit().centerCrop().into(img_ac);

                            LatLng latLng = new LatLng(Double.parseDouble(dataSnapshot1.child("lat").getValue(String.class)), Double.parseDouble(dataSnapshot1.child("mlong").getValue(String.class)));
                            MarkerOptions marker = new MarkerOptions().position(latLng).title(dataSnapshot1.child("ac_place_name").getValue(String.class));
                            mMap.moveCamera(newLatLngZoom(latLng, 18));
                            mMap.addMarker(marker);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });
        btn_dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
    @Override
    public void onPageSelected(int position) {
        //Log.d("Slider Demo", "Page Changed: " + position);
    }
    @Override
    public void onPageScrollStateChanged(int state) { }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if( result != null){
            if(result.getContents() == null){
                Toast.makeText(MainActivity.this, "คุณยกเลิกการสแกน", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(MainActivity.this, result.getContents(), Toast.LENGTH_SHORT).show();
            }

        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }

    }*/
    private void enableMyLocationIfPermitted() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
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
