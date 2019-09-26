package com.example.testfirebase.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.testfirebase.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginAdmin extends AppCompatActivity {
    private EditText edit_email,edit_password;
    private Button byn_submit;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_admin);
        firebaseAuth = FirebaseAuth.getInstance();
        edit_email = findViewById(R.id.edit_email);
        edit_password = findViewById(R.id.edit_password);
        byn_submit = findViewById(R.id.btn_submit);
        if(firebaseAuth.getCurrentUser() != null){ }
        byn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserLogin();
            }
        });
    }
    private void UserLogin(){
        String email = edit_email.getText().toString().trim();
        String passwotd = edit_password.getText().toString().trim();
        final ProgressDialog ps = new ProgressDialog(this);
        ps.setTitle("Please wait......");
        ps.show();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            ps.dismiss();
            return;
        }
        if(TextUtils.isEmpty(passwotd)){
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            ps.dismiss();
            return;
        }
        firebaseAuth.signInWithEmailAndPassword(email,passwotd).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                     @Override
                     public void onComplete(@NonNull Task<AuthResult> task) {
                         if(task.isSuccessful()){
                            ps.dismiss();
                            startActivity(new Intent(getApplicationContext(), ManageAdmin.class));
                            finish();
                         }
                         else{
                             ps.dismiss();
                             Toast.makeText(LoginAdmin.this, "ล้มเหลว", Toast.LENGTH_SHORT).show();
                             finish();
                         }
                     }
                 });
    }
}
