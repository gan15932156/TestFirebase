package com.example.testfirebase.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.testfirebase.GetterSetterClass.Floor;
import com.example.testfirebase.R;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapterFloorSpinner extends ArrayAdapter<Floor> {
    Activity context;
    List<Floor> floors;
    public CustomAdapterFloorSpinner(Context context, ArrayList<Floor> floors) {
        super(context, R.layout.custom_spn_floor,floors);
        this.context = (Activity) context;
        this.floors = floors;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.custom_spn_floor,null,true);
        TextView name = listViewItem.findViewById(R.id.cus_txt_floor_name);
        Floor floor = floors.get(position);
        name.setText(floor.getFloor_name());
        return  listViewItem;
    }
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.custom_spn_floor,null,true);
        TextView name = listViewItem.findViewById(R.id.cus_txt_floor_name);
        Floor floor = floors.get(position);
        name.setText(floor.getFloor_name());
        return  listViewItem;
    }
}
