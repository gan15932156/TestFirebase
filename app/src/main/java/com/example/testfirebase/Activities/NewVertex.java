package com.example.testfirebase.Activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.testfirebase.R;
import com.example.testfirebase.GetterSetterClass.Vertex;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom;

public class NewVertex extends FragmentActivity implements OnMapReadyCallback {
    private EditText edit_vertex_name,edit_vertex_number;
    private Spinner spn_vertex_type,spn_vertex_b_name;
    private GoogleMap mMap;
    private Button btn_insert;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private String lat,mlong;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_vertex);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        btn_insert = findViewById(R.id.btn_insert_vertex);
        spn_vertex_b_name = findViewById(R.id.spn_vertex_building_name);
        spn_vertex_type = findViewById(R.id.spn_vertex_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.sp_vertex_type,android.R.layout.simple_spinner_item);
        spn_vertex_type.setAdapter(adapter);

        Query query = FirebaseDatabase.getInstance().getReference("Building");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final List<String> buildinglist = new ArrayList<String>();
                buildinglist.add("ไม่มี");
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    String bname = dataSnapshot1.child("location_name").getValue(String.class);
                    buildinglist.add(bname);
                    MarkerOptions marker = new MarkerOptions()
                            .title(dataSnapshot1.child("location_name").getValue(String.class))
                            .position(new LatLng(
                                Double.parseDouble(dataSnapshot1.child("lat").getValue(String.class)),
                                Double.parseDouble(dataSnapshot1.child("longti").getValue(String.class))
                            ))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                    mMap.addMarker(marker);

                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(NewVertex.this, android.R.layout.simple_spinner_item, buildinglist);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spn_vertex_b_name.setAdapter(arrayAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        edit_vertex_name = findViewById(R.id.edit_vertex_name);
        edit_vertex_number = findViewById(R.id.edit_vertex_number);
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference("Vertex");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    int number = Integer.parseInt(postSnapshot.child("vertex_number").getValue(String.class)) + 1 ;
                    edit_vertex_number.setText(String.valueOf(number));
                    MarkerOptions marker = new MarkerOptions().position(new LatLng(
                        Double.parseDouble(postSnapshot.child("vertex_lat").getValue(String.class)),
                            Double.parseDouble(postSnapshot.child("vertex_long").getValue(String.class))
                    ));
                    mMap.addMarker(marker);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        btn_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String vertex_id = mRef.push().getKey();
                Vertex vertex = new Vertex(
                        vertex_id,
                        edit_vertex_name.getText().toString(),
                        lat,
                        mlong,
                        edit_vertex_number.getText().toString(),
                        spn_vertex_type.getSelectedItem().toString(),
                        spn_vertex_b_name.getSelectedItem().toString()
                );
                mRef.child(vertex_id).setValue(vertex);
                edit_vertex_name.setText(null);
            }
        });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setOnMyLocationButtonClickListener(onMyLocationButtonClickListener);
        mMap.setOnMyLocationClickListener(onMyLocationClickListener);
        enableMyLocationIfPermitted();
        mMap.getUiSettings().setZoomControlsEnabled(true);

        LatLng sydney = new LatLng(14.3472549, 100.5653264);
        mMap.moveCamera(newLatLngZoom(sydney, 15));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                MarkerOptions markerOptions = new MarkerOptions();
                mDatabase = FirebaseDatabase.getInstance();
                mRef = mDatabase.getReference("Vertex");

                edit_vertex_name.setText("Vertex_"+spn_vertex_b_name.getSelectedItem().toString()+"_"+edit_vertex_number.getText().toString());
                markerOptions.position(point);
                mMap.addMarker(markerOptions);
                lat = String.valueOf(point.latitude);
                mlong = String.valueOf(point.longitude);
            }
        });
    }
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
            //editStart.setText(location.getLatitude()+","+location.getLongitude());
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
