package com.example.testfirebase.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.testfirebase.GetterSetterClass.Location;
import com.example.testfirebase.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class CustomAdapterLocationSpinner extends ArrayAdapter<Location> {
     Activity context;
     List<Location> location;
    public CustomAdapterLocationSpinner(Context context, ArrayList<Location> location) {
        super(context, R.layout.custom_spn_location,location);
        this.context = (Activity) context;
        this.location = location;

    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.custom_spn_location,null,true);
        ImageView imageView = listViewItem.findViewById(R.id.cus_spn_lo_image);
        TextView txt_name = listViewItem.findViewById(R.id.cus_spn_lo_name);
        Location locationx = location.get(position);
        Glide.with(context).load(locationx.getImage_path()).into(imageView);
        txt_name.setText(locationx.getLocation_name());
        return  listViewItem;
    }
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.custom_spn_location,null,true);
        ImageView imageView = listViewItem.findViewById(R.id.cus_spn_lo_image);
        TextView txt_name = listViewItem.findViewById(R.id.cus_spn_lo_name);
        Location locationx = location.get(position);
        Picasso.with(context).load(locationx.getImage_path().toString()).fit().centerCrop().into(imageView);
        txt_name.setText(locationx.getLocation_name());
        return  listViewItem;
    }
        private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_spn_location, parent, false);}
            ImageView imageViewFlag = convertView.findViewById(R.id.cus_spn_lo_image);
            EditText edit = convertView.findViewById(R.id.cus_spn_lo_image);
            Location locationnnnn = getItem(position);
            if (locationnnnn != null) {
                Picasso.with(getContext()).load(locationnnnn.getImage_path()).fit().centerCrop().into(imageViewFlag);
                edit.setText(locationnnnn.getLocation_name());
            }
            return convertView;
    }
}
