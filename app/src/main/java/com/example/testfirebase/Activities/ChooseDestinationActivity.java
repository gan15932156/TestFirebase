package com.example.testfirebase.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testfirebase.Helper.DijkstraAlgorithm;
import com.example.testfirebase.GetterSetterClass.Edge_cal;
import com.example.testfirebase.GetterSetterClass.Graph;
import com.example.testfirebase.R;
import com.example.testfirebase.GetterSetterClass.Vertex_cal;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom;

public class ChooseDestinationActivity extends FragmentActivity implements OnMapReadyCallback{
    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Button btn_find_path;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private AutoCompleteTextView autoStart,autoEnd;
    private Polyline line;
    LinkedList<Vertex_cal> path ;
    private TextView txtDistance,txtDuration;

    List<Vertex_cal> vertex_cals = new ArrayList<>();
    Vertex_cal vertex_cal ;
    Edge_cal edge_cal;
    List<Edge_cal> edge_cals = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_destination);
        if (android.os.Build.VERSION.SDK_INT > 9) { StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        autoEnd = findViewById(R.id.autoText_end);
        autoStart = findViewById(R.id.autoText_start);
        txtDistance = findViewById(R.id.tvDistance);
        txtDuration = findViewById(R.id.tvDuration);
        btn_find_path = findViewById(R.id.btnFindPath);
        btn_find_path.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(autoStart.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "กรุณากรอกจุดเริ่มต้น", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(autoEnd.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "กรุณากรอกจุดหมาย", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!autoStart.getText().toString().isEmpty() && !autoEnd.getText().toString().isEmpty()){
                    //sendRequest();
                    cal_path(autoStart.getText().toString(),autoEnd.getText().toString());
                }
            }
        });
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference("Vertex");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    vertex_cal  = new Vertex_cal(
                            dataSnapshot1.child("vertex_id").getValue(String.class),
                            dataSnapshot1.child("vertex_name").getValue(String.class),
                            dataSnapshot1.child("vertex_lat").getValue(String.class),
                            dataSnapshot1.child("vertex_long").getValue(String.class),
                            dataSnapshot1.child("vertex_number").getValue(String.class),
                            dataSnapshot1.child("vertex_type").getValue(String.class),
                            dataSnapshot1.child("vertex_building_name").getValue(String.class)
                    );
                    vertex_cals.add(vertex_cal);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        mRef = mDatabase.getReference("Edge");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()){
                    edge_cal = new Edge_cal(
                            dataSnapshot2.child("edge_id").getValue(String.class),
                            vertex_cals.get(Integer.parseInt(dataSnapshot2.child("edge_source").getValue(String.class))),
                            vertex_cals.get(Integer.parseInt(dataSnapshot2.child("edge_destination").getValue(String.class))),
                            Integer.parseInt(dataSnapshot2.child("edge_distance").getValue(String.class))
                    );
                    edge_cals.add(edge_cal);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });



    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng NEWARK = new LatLng(14.3496251, 100.5632134);
       /* GroundOverlayOptions newarkMap = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromBitmap(getBitmapFromURL("https://scontent.fbkk26-1.fna.fbcdn.net/v/t1.0-9/41669966_934012686790374_8658821314762506240_n.jpg?_nc_cat=101&_nc_eui2=AeEYV9-B0tOivIai2Fx4zrdnDWqWJgBLDyt-NPF_ZdXspywlNxNhnl0MmuU8uOV5FyhJp2Eyy5h4cDWPsE0iNcWJVRjSS8wpPThIcTcIiRKIyw&_nc_oc=AQn223d-4Nx0U8evy26eq3YAerHuvc0UIA7cpSBZpfr6P14INnB465YmCpnYxT3CHyU&_nc_ht=scontent.fbkk26-1.fna&oh=4d59288358aec30529fa319719f8404a&oe=5DE5E63F")))
                .position(NEWARK, 8600f, 6500f);
        mMap.addGroundOverlay(newarkMap);*/

      /*  TileProvider tileProvider = new UrlTileProvider(256,256) {
            @Override
            public synchronized URL getTileUrl(int x, int y, int zoom) {
                int reversedY = (1 << zoom) - y - 1;
                String s = String.format(Locale.US, "https://images.pexels.com/photos/67636/rose-blue-flower-rose-blooms-67636.jpeg?cs=srgb&dl=beauty-bloom-blue-67636.jpg&fm=jpg", zoom, x, reversedY);
                URL url = null;
                try {
                    url = new URL(s);
                } catch (MalformedURLException e) {
                    throw new AssertionError(e);
                }
                return url;
            }
        };*/
        //mMap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));

        mMap.setOnMyLocationButtonClickListener(onMyLocationButtonClickListener);
        mMap.setOnMyLocationClickListener(onMyLocationClickListener);
        enableMyLocationIfPermitted();
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        LatLng ARU = new LatLng(14.3496251, 100.5632134);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ARU));
        mMap.moveCamera(newLatLngZoom(ARU, 18));

        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference("Building");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final List<String> buildinglist = new ArrayList<String>();
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Double Latitude = Double.parseDouble(postSnapshot.child("lat").getValue(String.class));
                    Double Longtitude = Double.parseDouble(postSnapshot.child("longti").getValue(String.class));
                    String name = postSnapshot.child("location_name").getValue(String.class);

                    MarkerOptions marker = new MarkerOptions().position(new LatLng(Latitude, Longtitude)).title(name)/*.snippet(id)*//*  .icon(BitmapDescriptorFactory.fromBitmap(reSizeimage(image,200,200)))*/;
                    Marker m1 =  mMap.addMarker(marker);
                    m1.showInfoWindow();
                    buildinglist.add(name);
                }
                ArrayAdapter<String> adapter=new ArrayAdapter<String>(ChooseDestinationActivity.this,android.R.layout.simple_list_item_1,android.R.id.text1,buildinglist);
                // bind dapter to autocompleteview
                autoEnd.setAdapter(adapter);
                autoStart.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {

                final Dialog dialog = new Dialog(ChooseDestinationActivity.this);

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                dialog.setContentView(R.layout.show_dialog_location_choose_destination);

                int width = (int)(getResources().getDisplayMetrics().widthPixels*0.95);
                int height = (int)(getResources().getDisplayMetrics().heightPixels*0.55);
                dialog.getWindow().setLayout(width,height);

                final TextView txt_name = dialog.findViewById(R.id.txt_dialog_show_location_name);
                final TextView txt_des = dialog.findViewById(R.id.txt_dialog_show_location_des);
                final Button btn_back = dialog.findViewById(R.id.btn_show_location_back);
                final ImageView img = dialog.findViewById(R.id.image_show_location_dialog);

                Query query = FirebaseDatabase.getInstance().getReference("Building")
                        .orderByChild("location_name")
                        .equalTo(marker.getTitle());
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            Picasso.with(ChooseDestinationActivity.this)
                                    .load(dataSnapshot1.child("image_path").getValue(String.class))
                                    .fit()
                                    .centerCrop()
                                    .into(img);
                            txt_name.setText(dataSnapshot1.child("location_name").getValue(String.class));
                            txt_des.setText(dataSnapshot1.child("location_des").getValue(String.class));
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                btn_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                if(autoStart.getText().toString().isEmpty()){
                    //marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    autoStart.setText(marker.getTitle());
                }
                if(!autoStart.getText().toString().isEmpty()){
                    //marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                    autoEnd.setText(marker.getTitle());
                }
                if(autoEnd.getText().toString().isEmpty()){
                    //marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                    autoEnd.setText(marker.getTitle());
                }
                return false;
            }
        });
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
            autoStart.setText("I'm here");
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
    public void cal_path(String source,String destination){
        line.remove();
        ArrayList<String> lo_name = new ArrayList<String>();

        lo_name.add(source);
        lo_name.add(destination);
        int number_source = 0;
        int number_des = 0;

        for(int i=0;i<vertex_cals.size();i++){
            if(vertex_cals.get(i).getVertex_building_name().equals(lo_name.get(0))){
                number_source = Integer.parseInt(vertex_cals.get(i).getVertex_number());
            }
            if(vertex_cals.get(i).getVertex_building_name().equals(lo_name.get(1))){
                number_des = Integer.parseInt(vertex_cals.get(i).getVertex_number());
            }
        }

        Graph graph = new Graph(vertex_cals, edge_cals); //init
        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph); //init
        dijkstra.execute(vertex_cals.get(number_source)); // start
        path = dijkstra.getPath(vertex_cals.get(number_des)); //stop

        int cal = 0;
        double distance_km = 0.0;
        double time = 0.0;
        int speed = 5 ;
        for(int i =0;i<path.size();i++){
            if(path.size() == i+1){
                break;
            }
            else{
                for (int j=0;j<edge_cals.size();j++){
                    if(path.get(i).getVertex_name() == edge_cals.get(j).getEdge_cal_source().getVertex_name() &&
                            path.get(i+1).getVertex_name() == edge_cals.get(j).getEdge_cal_destination().getVertex_name()
                    ){
                        cal+=edge_cals.get(j).getWeight();
                        Log.d("taf",
                                "From "+
                                        edge_cals.get(j).getEdge_cal_source().getVertex_name()
                                        +" To "+edge_cals.get(j).getEdge_cal_destination().getVertex_name()
                                        +" distance "+edge_cals.get(j).getWeight()
                        );
                        line = mMap.addPolyline(new PolylineOptions()
                                .add(
                                        new LatLng(
                                                Double.parseDouble(edge_cals.get(j).getEdge_cal_source().getVertex_lat()),
                                                Double.parseDouble(edge_cals.get(j).getEdge_cal_source().getVertex_long())
                                        ),
                                        new LatLng(
                                                Double.parseDouble(edge_cals.get(j).getEdge_cal_destination().getVertex_lat()),
                                                Double.parseDouble(edge_cals.get(j).getEdge_cal_destination().getVertex_long())
                                        )
                                )
                                .width(10)
                                .color(Color.GREEN));
                    }
                }
            }

        }

        distance_km = (double) cal / 1000;
        time = (distance_km / speed) * 60;
        String s = String.format(("%.2f"),time);
        txtDuration.setText(s+" นาที");
        txtDistance.setText(cal+" เมตร");

    }
}
