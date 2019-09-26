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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.testfirebase.GetterSetterClass.Location;
import com.example.testfirebase.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.IOException;

public class NewLocation extends AppCompatActivity {
    private ImageView imageView;
    private EditText edit_name,edit_des,edit_lat,edit_long;
    private Button btn_add,btn_choose,btn_map_chooser;
    private Spinner sp_location;
    private ProgressDialog progressDialog ;
    private Uri FilePathUri;
    private String Database_Path = "Building";
    private  DatabaseReference databaseReference;
    private int Image_Request_Code = 7;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_location);
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
        imageView = findViewById(R.id.image_building);
        edit_name = findViewById(R.id.edit_name);
        edit_des = findViewById(R.id.edit_des);
        edit_lat = findViewById(R.id.edit_lat);
        edit_long = findViewById(R.id.edit_long);
        btn_add = findViewById(R.id.btn_submit);
        btn_choose = findViewById(R.id.btnChooseImage);
        btn_map_chooser = findViewById(R.id.btn_show_map);
        sp_location = findViewById(R.id.sp_type);
        progressDialog = new ProgressDialog(getApplicationContext());
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.sp_type,android.R.layout.simple_spinner_item);
        sp_location.setAdapter(adapter);
        btn_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "โปรดเลือกรุปภาพ"), Image_Request_Code);
            }
        });
        btn_map_chooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(NewLocation.this, PlaceChooserMap.class);
                startActivityForResult(intent,1234);
            }
        });
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadImageFileToFirebaseStorage();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if (requestCode == Image_Request_Code && data != null && data.getData() != null) {
                FilePathUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);
                    imageView.setImageBitmap(bitmap);
                    btn_choose.setText("รูปที่เลือก");
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
    public String GetFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;
    }
    public void UploadImageFileToFirebaseStorage() {

        if (FilePathUri != null) {
            final ProgressDialog ps = new ProgressDialog(this);
            ps.setTitle("Uploading......");
            ps.show();
            FirebaseStorage storage;
            StorageReference storageReference;
            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();
            final StorageReference ref = storageReference.child("Building_image/" + System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));
            ref.putFile(FilePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ps.dismiss();
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Toast.makeText(NewLocation.this, "Uploaded", Toast.LENGTH_SHORT).show();
                                    @SuppressWarnings("VisibleForTests")
                                    String location_id = databaseReference.push().getKey();
                                    Location location = new Location(edit_name.getText().toString(),edit_des.getText().toString(),uri.toString(),
                                            sp_location.getSelectedItem().toString(),location_id,edit_lat.getText().toString(),edit_long.getText().toString());
                                    databaseReference.child(location_id).setValue(location);
                                    startActivity(new Intent(getApplicationContext(), ManageLocation.class));
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    ps.dismiss();
                    Toast.makeText(NewLocation.this, "Failed", Toast.LENGTH_SHORT).show();
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
