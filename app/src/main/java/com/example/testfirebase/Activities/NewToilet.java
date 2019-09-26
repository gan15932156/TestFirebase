package com.example.testfirebase.Activities;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.testfirebase.Adapter.CustomAdapterFloorSpinner;
import com.example.testfirebase.Adapter.CustomAdapterLocationSpinner;
import com.example.testfirebase.GetterSetterClass.Floor;
import com.example.testfirebase.GetterSetterClass.Location;
import com.example.testfirebase.R;
import com.example.testfirebase.GetterSetterClass.Toilet;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;

public class NewToilet extends AppCompatActivity {
    private ImageView imageView;
    private EditText edit_tpilet_name;
    private Spinner spn_location_toilet,spn_floor_toilet;
    private Button btn_add,btn_chooser;
    private Uri FilePathUri;
    private int Image_Request_Code = 7;
    private String location_id_spn=null;
    private String floor_id_spn=null;
    ArrayList<Location> location = new ArrayList<>();
    ArrayList<Floor> floors = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_toilet);
        imageView = findViewById(R.id.image_toilet);
        edit_tpilet_name = findViewById(R.id.edit_toilet_name);
        spn_location_toilet = findViewById(R.id.spn_location_toilet);
        spn_floor_toilet = findViewById(R.id.spn_floor_toilet);
        btn_add = findViewById(R.id.btn_adddddddddddddddddddd);
        btn_chooser = findViewById(R.id.btnChooseImageToilet);
        btn_chooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "โปรดเลือกรุปภาพ"), Image_Request_Code);
            }
        });
        fetch_location_firebase();
        spn_location_toilet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        spn_floor_toilet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Floor floor_obj = (Floor) parent.getSelectedItem();
                floor_id_spn = floor_obj.getFloor_id();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InsertToilet();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {
            FilePathUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);
                imageView.setImageBitmap(bitmap);
                btn_chooser.setText("รูปที่เลือก");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public String GetFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

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
                CustomAdapterLocationSpinner customAdapterLocationSpinner = new CustomAdapterLocationSpinner(NewToilet.this, location);
                spn_location_toilet.setAdapter(customAdapterLocationSpinner);
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
                CustomAdapterFloorSpinner customAdapterFloorSpinner = new CustomAdapterFloorSpinner(NewToilet.this, floors);
                spn_floor_toilet.setAdapter(customAdapterFloorSpinner);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void InsertToilet(){
        if (FilePathUri != null) {
            final ProgressDialog ps = new ProgressDialog(this);
            ps.setTitle("Uploading......");
            ps.show();
            FirebaseStorage storage;
            StorageReference storageReference;
            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();
            final StorageReference ref = storageReference.child("Toilet_image/" + System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));
            ref.putFile(FilePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ps.dismiss();
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Toast.makeText(NewToilet.this, "Uploaded", Toast.LENGTH_SHORT).show();
                                    @SuppressWarnings("VisibleForTests")
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Toilet");
                                    String toilet_id = databaseReference.push().getKey();
                                    Toilet toilet = new Toilet(toilet_id,floor_id_spn,edit_tpilet_name.getText().toString(),
                                            uri.toString());
                                    databaseReference.child(toilet_id).setValue(toilet);
                                    startActivity(new Intent(getApplicationContext(), ManageLocation.class));
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    ps.dismiss();
                    Toast.makeText(NewToilet.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progess  = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    ps.setMessage("Uploaded "+(int)progess+"%");
                }
            });
        }
        else {
            Toast.makeText(getApplicationContext(), "Please Select Image or Add Image Name", Toast.LENGTH_LONG).show();
        }
    }
}
