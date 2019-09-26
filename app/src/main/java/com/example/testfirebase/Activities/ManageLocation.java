package com.example.testfirebase.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.testfirebase.GetterSetterClass.Location;
import com.example.testfirebase.Adapter.LocationList;
import com.example.testfirebase.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ManageLocation extends AppCompatActivity {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private Uri FilePathUri;
    private  ImageView image_update ;
    private Button btn_choose_image_update;
    List<Location> locations  = new ArrayList<>();
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_location);
        listView = findViewById(R.id.listViewLocation);
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference("Building");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                locations.clear();
                for(DataSnapshot postSnapshot  : dataSnapshot.getChildren()){
                    Location location = postSnapshot.getValue(Location.class);
                    locations.add(location);
                }
                LocationList adapter = new LocationList(ManageLocation.this,locations);
                listView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
       listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
           @Override
           public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
               Location location = locations.get(position);
               showUpdateBuilding(location.getLocation_id(),location.getLocation_name(),location.getLocation_des(),
                       location.getImage_path(),location.getType(),location.getLat(),location.getLongti());
               return false;
           }
       });
    }
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.admin_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.admin_tab1:
                startActivity(new Intent(this, NewLocation.class));
                return true;
            case R.id.admin_floor:
                startActivity(new Intent(this, NewFloor.class));
                return true;
            case R.id.admin_toilet:
                startActivity(new Intent(this, NewToilet.class));
                return true;
        }
        return  super.onOptionsItemSelected(item);
    }
    private void showUpdateBuilding(final String id, String name, String des, final String image, String type, String lat, String longg){
        FilePathUri = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_dialog,null);
        builder.setView(dialogView);
        final EditText edit_name_update = dialogView.findViewById(R.id.edit_name_update);
        final EditText edit_des_update = dialogView.findViewById(R.id.edit_des_update);
        final EditText edit_lat_update = dialogView.findViewById(R.id.edit_lat_update);
        final EditText edit_long_update = dialogView.findViewById(R.id.edit_long_update);
        final Spinner sp_type_update = dialogView.findViewById(R.id.sp_type_update);
        final Button btn_update = dialogView.findViewById(R.id.btn_submit_update);
        final Button btn_delete = dialogView.findViewById(R.id.btn_delete);
        btn_choose_image_update = dialogView.findViewById(R.id.btnChooseImage_update);
        image_update = dialogView.findViewById(R.id.image_building_update);
        btn_choose_image_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "โปรดเลือกรุปภาพ"), 7);
            }
        });
        Picasso.with(getApplicationContext()).load(image).fit().centerCrop().into(image_update);
        builder.setTitle("แก้ไขข้อมูลอาคาร:"+name);
        edit_name_update.setText(name);
        edit_des_update.setText(des);
        edit_lat_update.setText(lat);
        edit_long_update.setText(longg);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.sp_type,android.R.layout.simple_spinner_item);
        sp_type_update.setAdapter(adapter);
        String compareValue = type;
        ArrayAdapter<CharSequence> type_adapter = ArrayAdapter.createFromResource(this, R.array.sp_type, android.R.layout.simple_spinner_item);
        type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_type_update.setAdapter(type_adapter);
        if (compareValue != null) {
            int spinnerPosition = type_adapter.getPosition(compareValue);
            sp_type_update.setSelection(spinnerPosition);
        }
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FilePathUri == null) {
                    Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                    updateLocation(id,edit_name_update.getText().toString(),edit_des_update.getText().toString(),
                            image,sp_type_update.getSelectedItem().toString(),edit_lat_update.getText().toString(),edit_long_update.getText().toString());
                    startActivity(new Intent(getApplicationContext(),ManageLocation.class));
                }
                else {
                    final ProgressDialog pssss = new ProgressDialog(dialogView.getContext());
                    pssss.setTitle("Uploading......");
                    pssss.show();
                    FirebaseStorage storage;
                    StorageReference storageReference;
                    storage = FirebaseStorage.getInstance();
                    storageReference = storage.getReference();
                    final StorageReference ref = storageReference.child("Building_image/" + System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));
                    ref.putFile(FilePathUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    pssss.dismiss();
                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                                            updateLocation(id,edit_name_update.getText().toString(),edit_des_update.getText().toString(),uri.toString(),
                                                    sp_type_update.getSelectedItem().toString(),edit_lat_update.getText().toString(),edit_long_update.getText().toString());
                                            startActivity(new Intent(getApplicationContext(),ManageLocation.class));
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pssss.dismiss();
                            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progess  = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            pssss.setMessage("Uploaded "+(int)progess+"%");
                        }
                    });
                }
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ManageLocation.this, "Deleted", Toast.LENGTH_SHORT).show();
                deleteLocation(id);
                startActivity(new Intent(getApplicationContext(),ManageLocation.class));
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    public String GetFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;
    }
    private boolean updateLocation(String id,String name,String des,String image,String type,String lat,String longg){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Building").child(id);
        Location location = new Location();
        location.setLocation_id(id);
        location.setLocation_name(name);
        location.setLocation_des(des);
        location.setImage_path(image);
        location.setType(type);
        location.setLat(lat);
        location.setLongti(longg);
        databaseReference.setValue(location);
        return true;
    }
    private void deleteLocation(String id){
        DatabaseReference drefLocation = FirebaseDatabase.getInstance().getReference("Building").child(id);
        drefLocation.removeValue();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 7 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            FilePathUri = data.getData();
            try {
                image_update.setImageBitmap(null);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);
                image_update.setImageBitmap(bitmap);
                btn_choose_image_update.setText("รูปที่เลือก");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
