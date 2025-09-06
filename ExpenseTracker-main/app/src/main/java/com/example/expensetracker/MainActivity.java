package com.example.expensetracker;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPass;
    private EditText btnLogin;
    private TextView mForgetPassword;
    private TextView mSignupHere;
    private EditText mCnfPass;

    private AlertDialog mDailog;

    //Firebase..

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        mAuth=FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),Home_Activity.class));
        }

        mDailog=new AlertDialog.Builder(this).create();

        LoginDetails();


    }

    private void LoginDetails(){
            mEmail=findViewById(R.id.email_login);
            mPass=findViewById(R.id.password_login);
            Button btnLogin=findViewById(R.id.btn_login);
            mForgetPassword=findViewById(R.id.forget_password);
            mSignupHere=findViewById(R.id.signup_reg);

            btnLogin.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){

                    String email=mEmail.getText().toString().trim();
                    String pass=mPass.getText().toString().trim();

                    if(TextUtils.isEmpty(email)){
                        mEmail.setError("Email Required!!!");
                        return;
                    }

                    if(TextUtils.isEmpty(pass)){
                        mPass.setError("Password Required!!!");
                        return;
                    }



                    mDailog.setMessage("Processing...");
                    mDailog.show();

                    mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                mDailog.dismiss();
                                startActivity(new Intent(getApplicationContext(), Home_Activity.class));

                                Toast.makeText(getApplicationContext(),"Login Successful",Toast.LENGTH_SHORT).show();
                            }else{
                                mDailog.dismiss();
                                Toast.makeText(getApplicationContext(),"Login Failed",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            });

            //Registration activity

            mSignupHere.setOnClickListener(new View.OnClickListener(){
               @Override
               public void onClick(View view){
                   startActivity(new Intent(getApplicationContext(),Registration_Activity.class));
                }
            });

            //Reset password activity

        mForgetPassword.setOnClickListener(new View.OnClickListener(){
            @Override
           public void onClick(View view){
               startActivity(new Intent(getApplicationContext(),Reset_Activity.class));
           }
        });

    }

}