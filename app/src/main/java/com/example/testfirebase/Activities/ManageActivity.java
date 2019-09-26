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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.testfirebase.Adapter.ActivityList;
import com.example.testfirebase.GetterSetterClass.Activities;
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

public class ManageActivity extends AppCompatActivity {
    ListView listView;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    List<Activities> activities  = new ArrayList<>();
    private Uri FilePathUri;
    private ImageView image_update ;
    private Button btn_choose_image_update;
    private EditText edit_lat,edit_long;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
        listView = findViewById(R.id.listViewActivity);
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference("Activity");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                activities.clear();
                for(DataSnapshot postSnapshot  : dataSnapshot.getChildren()){
                    Activities ac = postSnapshot.getValue(Activities.class);
                    activities.add(ac);
                }
                ActivityList adapter = new ActivityList(ManageActivity.this,activities);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Activities acc = activities.get(position);
                showUpdateActivity(
                        acc.getAc_id(),
                        acc.getAc_name(),
                        acc.getAc_place_name(),
                        acc.getAc_des(),
                        acc.getAc_image(),
                        acc.getLat(),
                        acc.getMlong()
                );
                return false;
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manage_activity_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.manageactivity_menu1:
                startActivity(new Intent(getApplicationContext(), NewActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void showUpdateActivity(
            final String id,
            String name,
            String place,
            String des,
            final String image,
            String lat,
            String longg
    )
    {
        FilePathUri = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.uodate_activity_dialog,null);
        builder.setView(dialogView);
        final EditText edit_name = dialogView.findViewById(R.id.edit_ac_name_update);
        final EditText edit_des = dialogView.findViewById(R.id.edit_ac_dea_update);
        final EditText edit_place = dialogView.findViewById(R.id.edit_place_name_update);
        edit_lat = dialogView.findViewById(R.id.edit_place_lat_update);
        edit_long = dialogView.findViewById(R.id.edit_place_long_update);
        final Button btn_update = dialogView.findViewById(R.id.btn_update);
        final Button btn_delete = dialogView.findViewById(R.id.btn_delete);
        final Button btn_choose_place_update = dialogView.findViewById(R.id.btn_chose_place_update);
        btn_choose_image_update = dialogView.findViewById(R.id.btnChooseImage_ac_update);
        image_update = dialogView.findViewById(R.id.image_activity_update);
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
        builder.setTitle("แก้ไขกิจกรรม:"+name);
        edit_name.setText(name);
        edit_place.setText(place);
        edit_des.setText(des);
        edit_lat.setText(lat);
        edit_long.setText(longg);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FilePathUri == null) {
                    Toast.makeText(getApplicationContext(), "แก้ไขแล้ว", Toast.LENGTH_SHORT).show();
                    updateActivity(
                            id,
                            edit_name.getText().toString(),
                            edit_place.getText().toString(),
                            edit_des.getText().toString(),
                            edit_lat.getText().toString(),
                            edit_long.getText().toString(),
                            image
                    );
                    startActivity(new Intent(getApplicationContext(),ManageActivity.class));
                }
                else {
                    final ProgressDialog pssss = new ProgressDialog(dialogView.getContext());
                    pssss.setTitle("กำลังแก้ไข......");
                    pssss.show();
                    FirebaseStorage storage;
                    StorageReference storageReference;
                    storage = FirebaseStorage.getInstance();
                    storageReference = storage.getReference();
                    final StorageReference ref = storageReference.child("Activity_image/" + System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));
                    ref.putFile(FilePathUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    pssss.dismiss();
                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Toast.makeText(getApplicationContext(), "แก้ไขแล้ว", Toast.LENGTH_SHORT).show();
                                            updateActivity(
                                                    id,
                                                    edit_name.getText().toString(),
                                                    edit_place.getText().toString(),
                                                    edit_des.getText().toString(),
                                                    edit_lat.getText().toString(),
                                                    edit_long.getText().toString(),
                                                    uri.toString()
                                            );
                                            startActivity(new Intent(getApplicationContext(),ManageActivity.class));
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pssss.dismiss();
                            Toast.makeText(getApplicationContext(), "ล้มเหลว", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progess  = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            pssss.setMessage("อัพโหลดแล้ว "+(int)progess+"%");
                        }
                    });
                }
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ManageActivity.this, "ลบแล้ว", Toast.LENGTH_SHORT).show();
                deleteActivity(id);
                startActivity(new Intent(getApplicationContext(),ManageActivity.class));
            }
        });
        btn_choose_place_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(ManageActivity.this, PlaceChooserMap.class);
                startActivityForResult(intent,1234);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private boolean updateActivity(
            String id,
            String name,
            String place,
            String des,
            String lat,
            String mlong,
            String image
    ){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Activity").child(id);
        Activities accc = new Activities();
        accc.setAc_id(id);
        accc.setAc_name(name);
        accc.setAc_des(des);
        accc.setAc_place_name(place);
        accc.setLat(lat);
        accc.setMlong(mlong);
        accc.setAc_image(image);
        databaseReference.setValue(accc);
        return true;
    }
    private void deleteActivity(String id){
        DatabaseReference drefLocation = FirebaseDatabase.getInstance().getReference("Activity").child(id);
        drefLocation.removeValue();
    }
    public String GetFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == 7){
                FilePathUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);
                    image_update.setImageBitmap(bitmap);
                    btn_choose_image_update.setText("รูปที่เลือก");
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(requestCode == 1234){
                edit_lat.setText(data.getStringExtra("lat"));
                edit_long.setText(data.getStringExtra("mlong"));
            }
        }

    }
}
