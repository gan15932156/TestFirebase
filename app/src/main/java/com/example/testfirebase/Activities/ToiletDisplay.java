package com.example.testfirebase.Activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.testfirebase.Adapter.CustomAdapterFloorSpinner;
import com.example.testfirebase.Adapter.CustomAdapterLocationSpinner;
import com.example.testfirebase.GetterSetterClass.Floor;
import com.example.testfirebase.GetterSetterClass.Location;
import com.example.testfirebase.R;
import com.example.testfirebase.GetterSetterClass.Toilet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class ToiletDisplay extends AppCompatActivity {
    private ImageView image_view_show_toilet;
    private Spinner spn_show_location,spn_show_floor,spn_show_toilet;
    private TextView txt_show_toilet_name;
    private String location_id_spn=null;
    private String floor_id_spn=null;
    private String toilet_id_spn=null;
    ArrayList<Location> location = new ArrayList<>();
    ArrayList<Floor> floors = new ArrayList<>();
    ArrayList<Toilet> toiletssssss = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toilet_display);
        image_view_show_toilet = findViewById(R.id.image_toilet_show);
        spn_show_location = findViewById(R.id.spn_location_toilet_show);
        spn_show_floor = findViewById(R.id.spn_floor_toilet_show);
        txt_show_toilet_name = findViewById(R.id.txt_show_toilet_name);
        fetch_location_firebase();
        spn_show_location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Location location_obj = (Location) parent.getSelectedItem();
                location_id_spn = location_obj.getLocation_id();
                fetch_floor_firebase(location_id_spn);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spn_show_floor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Floor floor_obj = (Floor) parent.getSelectedItem();
                floor_id_spn = floor_obj.getFloor_id();
                show_toilet_selected(floor_id_spn);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void fetch_location_firebase(){
        Query query = FirebaseDatabase.getInstance().getReference("Building")
                .orderByChild("type")
                .equalTo("อาคาร");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                location.clear();
                for(DataSnapshot postSnapshot  : dataSnapshot.getChildren()){
                    Location locationn = postSnapshot.getValue(Location.class);
                    location.add(locationn);
                }
                CustomAdapterLocationSpinner customAdapterLocationSpinner = new CustomAdapterLocationSpinner(ToiletDisplay.this, location);
                spn_show_location.setAdapter(customAdapterLocationSpinner);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void fetch_floor_firebase(String location_id){
        Query query = FirebaseDatabase.getInstance().getReference("Floor")
                .orderByChild("location_id")
                .equalTo(location_id);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                floors.clear();
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    Floor floor = dataSnapshot1.getValue(Floor.class);
                    floors.add(floor);
                }
                CustomAdapterFloorSpinner customAdapterFloorSpinner = new CustomAdapterFloorSpinner(ToiletDisplay.this, floors);
                spn_show_floor.setAdapter(customAdapterFloorSpinner);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void show_toilet_selected(String floor_id){
        Query query = FirebaseDatabase.getInstance().getReference("Toilet")
                .orderByChild("floor_id")
                .equalTo(floor_id);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                toiletssssss.clear();
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    String pic = dataSnapshot1.child("toiley_map_pic").getValue(String.class);
                    String name = dataSnapshot1.child("toilet_name").getValue(String.class);
                    txt_show_toilet_name.setText(name);
                    Picasso.with(getApplicationContext()).load(pic).fit().centerCrop().into(image_view_show_toilet);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
