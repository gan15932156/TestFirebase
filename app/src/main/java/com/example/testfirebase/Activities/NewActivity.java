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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.testfirebase.GetterSetterClass.Activities;
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

public class NewActivity extends AppCompatActivity {
    /* private EditText edit_date1,edit_date2,edit_time1,edit_time2;
    private Button btn_show_date1,btn_show_date2,btn_show_time1,btn_show_time2;
    private int mYear1,mMonth1,mDay1,mHour1,mMinute1;
    private int mYear2,mMonth2,mDay2,mHour2,mMinute2;
    static final int DATE_DIALOG_ID = 0;*/
    private EditText edit_name,edit_place_name,edit_des,edit_long,edit_lat;
    private ImageView chooseimage;
    private Button btn_choose_image,btn_send_data,btn_choose_place;
    private int Image_Request_Code = 7;
    private Uri FilePathUri;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);
        databaseReference = FirebaseDatabase.getInstance().getReference("Activity");
        edit_name = findViewById(R.id.edit_ac_name);
        edit_place_name = findViewById(R.id.edit_place_name);
        edit_des = findViewById(R.id.edit_ac_dea);
        edit_long = findViewById(R.id.edit_place_long);
        edit_lat = findViewById(R.id.edit_place_lat);
        chooseimage = findViewById(R.id.image_activity);
        btn_choose_image = findViewById(R.id.btnChooseImage3);
        btn_send_data = findViewById(R.id.btn_submit3);
        btn_choose_place = findViewById(R.id.btn_chose_place);
        btn_choose_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "โปรดเลือกรุปภาพ"), Image_Request_Code);
            }
        });
        btn_send_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadImageFileToFirebaseStorage();
            }
        });
        btn_choose_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(NewActivity.this, PlaceChooserMap.class);
                startActivityForResult(intent,1234);
            }
        });
        /*  edit_date1 = findViewById(R.id.edit_date1);
       edit_time1 = findViewById(R.id.edit_time1);
       btn_show_date1 = findViewById(R.id.show_date1);
       btn_show_time1 = findViewById(R.id.show_time1);
       btn_show_date1.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               final Calendar c = Calendar.getInstance();
               mYear1 = c.get(Calendar.YEAR);
               mMonth1 = c.get(Calendar.MONTH);
               mDay1 = c.get(Calendar.DAY_OF_MONTH);

               DatePickerDialog datePickerDialog = new DatePickerDialog(NewActivity.this,
                       new DatePickerDialog.OnDateSetListener() {

                           @Override
                           public void onDateSet(DatePicker view, int year,
                                                 int monthOfYear, int dayOfMonth) {

                               edit_date1.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                           }
                       }, mYear1, mMonth1, mDay1);
               datePickerDialog.show();
           }
       });
       btn_show_time1.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               final Calendar c= Calendar.getInstance();
               mHour1 = c.get(Calendar.HOUR_OF_DAY);
               mMonth1 = c.get(Calendar.MINUTE);

               TimePickerDialog timePickerDialog = new TimePickerDialog(NewActivity.this, new TimePickerDialog.OnTimeSetListener() {
                   @Override
                   public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                       edit_time1.setText(hourOfDay + ":" + minute);
                   }
               },mHour1,mMinute1,false);
               timePickerDialog.show();
           }
       });
       */
    }
    public String GetFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if(requestCode == Image_Request_Code){
                FilePathUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);
                    chooseimage.setImageBitmap(bitmap);
                    btn_choose_image.setText("รูปที่เลือก");
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(requestCode == 1234){
                edit_lat.setText(data.getStringExtra("lat"));
                edit_long.setText(data.getStringExtra("mlong"));
                //Toast.makeText(this, data.getStringExtra("lat")+data.getStringExtra("mlong"), Toast.LENGTH_SHORT).show();
                //Toast.makeText(this, "Yews", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void UploadImageFileToFirebaseStorage() {

        if (FilePathUri != null) {
            final ProgressDialog ps = new ProgressDialog(this);
            ps.setTitle("กำลังอัพโหลด......");
            ps.show();
            FirebaseStorage storage;
            StorageReference storageReference;
            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();
            final StorageReference ref = storageReference.child("Activity_image/" + System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));
            ref.putFile(FilePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ps.dismiss();
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Toast.makeText(NewActivity.this, "อัพโหลด", Toast.LENGTH_SHORT).show();
                                    @SuppressWarnings("VisibleForTests")
                                    String ac_id = databaseReference.push().getKey();
                                    //Location location = new Location(edit_name.getText().toString(),edit_des.getText().toString(),uri.toString(), sp_location.getSelectedItem().toString(),location_id,edit_lat.getText().toString(),edit_long.getText().toString());
                                    Activities activities = new Activities(
                                            ac_id,
                                            edit_name.getText().toString(),
                                            edit_place_name.getText().toString(),
                                            edit_des.getText().toString(),
                                            uri.toString(),
                                            edit_lat.getText().toString(),
                                            edit_long.getText().toString()
                                    );
                                    databaseReference.child(ac_id).setValue(activities);
                                    startActivity(new Intent(getApplicationContext(), ManageActivity.class));
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    ps.dismiss();
                    Toast.makeText(NewActivity.this, "ล้มเหลว", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progess  = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    ps.setMessage("อัพโหลดแล้ว "+(int)progess+"%");
                }
            });
        }
        else {
            Toast.makeText(getApplicationContext(), "กรุณาเลือกณุปภาพ", Toast.LENGTH_LONG).show();
        }
    }
}
