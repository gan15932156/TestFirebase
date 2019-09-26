package com.example.testfirebase.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.testfirebase.R;

public class ManageAdmin extends AppCompatActivity {
    private Button btnManageBuilding,btnManageFloor,btnManageToilet,btnManageActivity,btnManageVertex,btnManageEdge;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_admin);
        btnManageBuilding = findViewById(R.id.btnManageBuilding);
        btnManageFloor = findViewById(R.id.btnManageFloor);
        btnManageToilet = findViewById(R.id.btnManageToilet);
        btnManageActivity = findViewById(R.id.btnManageActivity);
        btnManageVertex = findViewById(R.id.btnManageVertex);
        btnManageEdge = findViewById(R.id.btnManageEdge);
        btnManageBuilding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ManageLocation.class));
            }
        });
        btnManageFloor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btnManageToilet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btnManageActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ManageActivity.class));
            }
        });
        btnManageVertex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NewVertex.class));
            }
        });
        btnManageEdge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NewEdge.class));
            }
        });
    }
}
