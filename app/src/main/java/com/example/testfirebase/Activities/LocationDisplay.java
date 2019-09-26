package com.example.testfirebase.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.testfirebase.GetterSetterClass.Location;
import com.example.testfirebase.Adapter.MainLocationList;
import com.example.testfirebase.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class LocationDisplay extends AppCompatActivity {
    private ListView mainlistview;
    Location location;
    List<Location> locations  = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_display);
        mainlistview = findViewById(R.id.listMain);
        Query query = FirebaseDatabase.getInstance().getReference("Building")
                .orderByChild("type");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                locations.clear();
                for(DataSnapshot postSnapshot  : dataSnapshot.getChildren()){
                    location = postSnapshot.getValue(Location.class);
                    locations.add(location);
                }
                MainLocationList adapter = new MainLocationList(LocationDisplay.this,locations);
                mainlistview.setAdapter(adapter);
                mainlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Location location2 = locations.get(position);
                        Intent intent = new Intent(getApplicationContext(), showMap.class);
                        intent.putExtra("id",location2.getLocation_id());
                        intent.putExtra("name",location2.getLocation_name());
                        intent.putExtra("des",location2.getLocation_des());
                        intent.putExtra("lat",location2.getLat());
                        intent.putExtra("longg",location2.getLongti());
                        intent.putExtra("image",location2.getImage_path());
                        intent.putExtra("type",location2.getType());
                        startActivity(intent);
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
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
                Intent intent = new Intent(this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                startActivity(intent);
                this.finish();
                return true;
        }
        return  super.onOptionsItemSelected(item);
    }
}
