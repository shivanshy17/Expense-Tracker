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


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Registration_Activity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPass;
    private Button btnReg;
    private TextView mSignin;
    private EditText mCnfPass;

    private AlertDialog mDailog;
    //Firebase

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth=FirebaseAuth.getInstance();

        mDailog=new AlertDialog.Builder(this).create();

        registration();
    }

    private void registration(){

        mEmail=findViewById(R.id.email_signup);
        mPass=findViewById(R.id.password_signup);
        mSignin=findViewById(R.id.signin_reg);
        mCnfPass=findViewById(R.id.password_signup_confirm);
        btnReg=findViewById(R.id.btn_login);


        btnReg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                String email=mEmail.getText().toString().trim();
                String pass=mPass.getText().toString().trim();
                String cnfpass=mCnfPass.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email Required!!");
                    return;
                }

                if(TextUtils.isEmpty(pass)){
                    mPass.setError("Password Required!!");
                    return;
                }

                if(TextUtils.isEmpty(cnfpass)){
                    mCnfPass.setError("Confirm your Password!!");
                    return;
                }

                mDailog.setMessage("Registering User...");
                mDailog.show();

                mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            mDailog.dismiss();
                            Toast.makeText(getApplicationContext(),"Registration Complete", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), Home_Activity.class));

                        }else {
                            mDailog.dismiss();
                            Toast.makeText(getApplicationContext(),"Registration Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });



            }
        });

        mSignin.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View view){
               startActivity(new Intent(getApplicationContext(),MainActivity.class));
           }
        });

    }


}
