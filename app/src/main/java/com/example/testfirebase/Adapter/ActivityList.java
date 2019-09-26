package com.example.testfirebase.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.testfirebase.GetterSetterClass.Activities;
import com.example.testfirebase.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ActivityList extends ArrayAdapter<Activities> {
    private Activity context;
    private List<Activities> activities;
    public ActivityList(Activity context,List<Activities> activities){
        super(context, R.layout.activity_list_item_view,activities);
        this.context = context;
        this.activities = activities;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.activity_list_item_view,null,true);
        ImageView imageView = listViewItem.findViewById(R.id.image_list_ac);
        TextView name = listViewItem.findViewById(R.id.txt_ac_list_name);
        TextView place = listViewItem.findViewById(R.id.txt_ac_list_place);
        TextView des = listViewItem.findViewById(R.id.txt_ac_list_des);
        Activities ac = activities.get(position);
        Picasso.with(context).load(ac.getAc_image().toString()).fit().centerCrop().into(imageView);
        name.setText(ac.getAc_name());
        place.setText(ac.getAc_place_name());
        des.setText(ac.getAc_des());
        return  listViewItem;
    }
}
