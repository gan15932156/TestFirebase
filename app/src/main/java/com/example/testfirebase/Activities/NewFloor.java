package com.example.testfirebase.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.testfirebase.Adapter.CustomAdapterLocationSpinner;
import com.example.testfirebase.GetterSetterClass.Floor;
import com.example.testfirebase.GetterSetterClass.Location;
import com.example.testfirebase.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NewFloor extends AppCompatActivity {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private Spinner spn_lcoation;
    private EditText edit_floor;
    private Button btn_add;
    private String location_id_spn=null;
    ArrayList<Location> location = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_floor);
        spn_lcoation = findViewById(R.id.sp_location);
        edit_floor = findViewById(R.id.edit_floor);
        btn_add = findViewById(R.id.btn_add);
        fetch_location_firebase();

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InsertFloor();
            }
        });
        spn_lcoation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Location location_obj = (Location) parent.getSelectedItem();
                location_id_spn = location_obj.getLocation_id();
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
                CustomAdapterLocationSpinner customAdapterLocationSpinner = new CustomAdapterLocationSpinner(NewFloor.this, location);
                spn_lcoation.setAdapter(customAdapterLocationSpinner);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void InsertFloor(){
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference("Floor");
        String floor_id = mRef.push().getKey();
        Floor floor = new Floor(floor_id,location_id_spn,edit_floor.getText().toString());
        mRef.child(floor_id).setValue(floor);
        startActivity(new Intent(getApplicationContext(), ManageLocation.class));
    }
}
