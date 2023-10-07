package com.example.chatbox;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


public class login extends AppCompatActivity {

      Button reg;
    Button login;
    EditText email;
    EditText pass;
    FirebaseAuth auth;

    String emailPattern;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login=findViewById(R.id.button);
        email=findViewById(R.id.editTextTextEmailAddress);
        pass=findViewById(R.id.editTextTextPassword);
        emailPattern="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        reg=findViewById(R.id.button3);

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(login.this, registration.class);
                startActivity(i);
            }
        });

        auth=FirebaseAuth.getInstance();
        login.setOnClickListener(v -> {

           String e=email.getText().toString();
            String p=pass.getText().toString();

            if((TextUtils.isEmpty(e))){
                Toast.makeText(login.this,"Enter the Email",Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(p)) {

               Toast.makeText(login.this,"Enter the password",Toast.LENGTH_SHORT).show();

            }
           else if(!e.matches(emailPattern)){
                Toast.makeText(login.this,"Enter Correct Email",Toast.LENGTH_SHORT).show();
            }
            else if(p.length()<6){
                Toast.makeText(login.this,"Enter password of length 6 or more",Toast.LENGTH_SHORT).show();
            }
            else{
                auth.signInWithEmailAndPassword(e,p).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        try{
                           Intent i=new Intent(login.this, MainActivity2.class);
                            startActivity(i);
                            finish();
                        }catch(Exception e1){

                            Toast.makeText(login.this, e1.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(login.this, Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });

            }

        });

    }
}