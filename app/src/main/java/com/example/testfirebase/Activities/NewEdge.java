package com.example.testfirebase.Activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.testfirebase.GetterSetterClass.Edge;
import com.example.testfirebase.R;
import com.example.testfirebase.GetterSetterClass.Vertex;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom;

public class NewEdge extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Button btn_load_data;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    Polyline line;
    ArrayList<HashMap<String,String>> myArrList = new ArrayList<HashMap<String, String>>();
    HashMap<String,String> map;

    List<Vertex> vextexs = new ArrayList<>();
    Vertex vertex ;

    List<Edge> edges = new ArrayList<>();
    Edge edge;

    ArrayList<HashMap<String,String>> myListVertexBase = new ArrayList<HashMap<String, String>>();
    HashMap<String,String> myHashVertexBase ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_edge);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        btn_load_data = findViewById(R.id.btn_loadData);

        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference("Vertex");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    MarkerOptions marker = new MarkerOptions().position(new LatLng(
                            Double.parseDouble(postSnapshot.child("vertex_lat").getValue(String.class)),
                            Double.parseDouble(postSnapshot.child("vertex_long").getValue(String.class))
                    ))
                            .title(postSnapshot.child("vertex_name").getValue(String.class))
                            .snippet(postSnapshot.child("vertex_number").getValue(String.class));
                    mMap.addMarker(marker);
                    myHashVertexBase = new HashMap<String, String>();
                    //vertex_cal  = new Vertex_cal(postSnapshot.child("vertex_id").getValue(String.class),postSnapshot.child("vertex_name").getValue(String.class));
                    vertex  = new Vertex(
                            postSnapshot.child("vertex_id").getValue(String.class),
                            postSnapshot.child("vertex_name").getValue(String.class),
                            postSnapshot.child("vertex_lat").getValue(String.class),
                            postSnapshot.child("vertex_long").getValue(String.class),
                            postSnapshot.child("vertex_number").getValue(String.class),
                            postSnapshot.child("vertex_type").getValue(String.class),
                            postSnapshot.child("vertex_building_name").getValue(String.class)
                    );
                    vextexs.add(vertex);

                    myHashVertexBase.put("vertex_id",postSnapshot.child("vertex_id").getValue(String.class));
                    myHashVertexBase.put("vertex_lat",postSnapshot.child("vertex_lat").getValue(String.class));
                    myHashVertexBase.put("vertex_long",postSnapshot.child("vertex_long").getValue(String.class));
                    myHashVertexBase.put("vertex_name",postSnapshot.child("vertex_name").getValue(String.class));
                    myHashVertexBase.put("vertex_number",postSnapshot.child("vertex_number").getValue(String.class));
                    myListVertexBase.add(myHashVertexBase);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        mRef = mDatabase.getReference("Building");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    MarkerOptions marker = new MarkerOptions()
                            .title(dataSnapshot1.child("location_name").getValue(String.class))
                            .position(new LatLng(
                                    Double.parseDouble(dataSnapshot1.child("lat").getValue(String.class)),
                                    Double.parseDouble(dataSnapshot1.child("longti").getValue(String.class))
                            ))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    mMap.addMarker(marker);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });







        mRef = mDatabase.getReference("Edge");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    edge  = new Edge(
                            dataSnapshot1.child("edge_id").getValue(String.class),
                            dataSnapshot1.child("edge_source").getValue(String.class),
                            dataSnapshot1.child("edge_destination").getValue(String.class),
                            dataSnapshot1.child("edge_distance").getValue(String.class)
                    );
                    edges.add(edge);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        btn_load_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int j=0;j<edges.size();j++){
                    line = mMap.addPolyline(new PolylineOptions()
                        .add(
                                new LatLng(
                                        Double.parseDouble(vextexs.get(Integer.parseInt(edges.get(j).getEdge_source())).getVertex_lat()),
                                        Double.parseDouble(vextexs.get(Integer.parseInt(edges.get(j).getEdge_source())).getVertex_long())
                                ),
                                new LatLng(
                                        Double.parseDouble(vextexs.get(Integer.parseInt(edges.get(j).getEdge_destination())).getVertex_lat()),
                                        Double.parseDouble(vextexs.get(Integer.parseInt(edges.get(j).getEdge_destination())).getVertex_long())
                                )
                        )
                        .width(5)
                        .color(Color.RED));
                }
            }
        });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMyLocationButtonClickListener(onMyLocationButtonClickListener);
        mMap.setOnMyLocationClickListener(onMyLocationClickListener);
        enableMyLocationIfPermitted();
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        LatLng sydney = new LatLng(14.3472549, 100.5653264);
        mMap.moveCamera(newLatLngZoom(sydney, 15));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Double distance = 0.0;
                ArrayList<String> arrList = new ArrayList<String>();
                ArrayList<LatLng> arrListLatLng = new ArrayList<LatLng>();
                map = new HashMap<String, String>();
                map.put("vertex_name",marker.getTitle());
                map.put("lat",String.valueOf(marker.getPosition().latitude));
                map.put("long",String.valueOf(marker.getPosition().longitude));
                myArrList.add(map);

                MarkerOptions options = new MarkerOptions();
                options.position(marker.getPosition());

                if (myArrList.size() == 0) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                }
                else if (myArrList.size() == 1) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                }
                mMap.addMarker(options);

                if (myArrList.size() > 1) {
                    for(int j=0;j<myArrList.size();j++){
                        for(int i =0;i<myListVertexBase.size();i++){
                            if(myArrList.get(j).get("vertex_name").equals(myListVertexBase.get(i).get("vertex_name"))){
                                Log.d("tag",myListVertexBase.get(i).get("vertex_name")+" "+myListVertexBase.get(i).get("vertex_number"));
                                arrList.add(myListVertexBase.get(i).get("vertex_number"));
                                arrListLatLng.add(new LatLng(
                                        Double.parseDouble(myListVertexBase.get(i).get("vertex_lat")),
                                        Double.parseDouble(myListVertexBase.get(i).get("vertex_long"))

                                ));
                                Log.d("tag","yes");
                            }
                        }
                    }
                    mRef = mDatabase.getReference("Edge");
                    String edge_id = mRef.push().getKey();

                    distance = SphericalUtil.computeDistanceBetween(
                            new LatLng(
                                    arrListLatLng.get(0).latitude,
                                    arrListLatLng.get(0).longitude
                            ),
                            new LatLng(
                                    arrListLatLng.get(1).latitude,
                                    arrListLatLng.get(1).longitude
                            )

                    );

                    Edge edge = new Edge(
                            edge_id,
                            arrList.get(0),
                            arrList.get(1),
                            String.valueOf(Math.round(distance))

                    );
                    mRef.child(edge_id).setValue(edge);

                    String edge_id2 = mRef.push().getKey();
                    Edge edge1 = new Edge(
                            edge_id2,
                            arrList.get(1),
                            arrList.get(0),
                            String.valueOf(Math.round(distance))
                    );
                    mRef.child(edge_id2).setValue(edge1);

                    arrList.clear();
                    arrListLatLng.clear();
                    myArrList.clear();
                }
                return false;
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
