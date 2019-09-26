package com.example.testfirebase.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.testfirebase.GetterSetterClass.Location;
import com.example.testfirebase.R;
import com.squareup.picasso.Picasso;
import java.util.List;

public class LocationList extends ArrayAdapter<Location> {
    private Activity context;
    private List<Location> locations;
    public LocationList(Activity context,List<Location> locations){
        super(context, R.layout.location_list_item_view,locations);
        this.context = context;
        this.locations = locations;
    }
    @Override
    public View getView(int position,View convertView,ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.location_list_item_view,null,true);
        ImageView imageView = listViewItem.findViewById(R.id.image_list_location);
        TextView txt_name = listViewItem.findViewById(R.id.txt_list_name);
        Location location = locations.get(position);
        Picasso.with(context).load(location.getImage_path().toString()).fit().centerCrop().into(imageView);
        txt_name.setText(location.getLocation_name());
        return  listViewItem;
    }
}
