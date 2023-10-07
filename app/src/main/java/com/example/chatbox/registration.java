package com.example.chatbox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class registration extends AppCompatActivity {

    EditText re_user,re_email,re_pass,cn_pass;
    Button login;
    Button singUp;
    CircleImageView re_image;

    FirebaseAuth auth;

    Uri imageUri;
    String imageuri;

    String emailPattern;
    FirebaseDatabase database;
    FirebaseStorage storage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        re_user=findViewById(R.id.editTextText);
        re_email=findViewById(R.id.editTextTextEmailAddress2);
        re_pass=findViewById(R.id.editTextTextPassword2);
        cn_pass=findViewById(R.id.editTextTextPassword3);
        login=findViewById(R.id.button2);
        re_image=findViewById(R.id.profilerg);
        singUp=findViewById(R.id.button);
        emailPattern="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();

        auth=FirebaseAuth.getInstance();


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(registration.this,login.class);
                startActivity(i);
                finish();
            }
        });
        singUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name= re_user.getText().toString();
                String email=re_email.getText().toString();
                String pass=re_pass.getText().toString();
                String cnpass=cn_pass.getText().toString();


                String status="Hey I am using this Application";

                if(TextUtils.isEmpty(name)||TextUtils.isEmpty(email)|| TextUtils.isEmpty(pass) || TextUtils.isEmpty(cnpass)){

                    Toast.makeText(registration.this,"Please Enter Valid Information",Toast.LENGTH_SHORT).show();
                } else if (!email.matches(emailPattern)) {


                    Toast.makeText(registration.this,"Please Enter Valid Email",Toast.LENGTH_SHORT).show();
                }else if(pass.length()<6){
                    Toast.makeText(registration.this,"Password must be 6 characters or more",Toast.LENGTH_SHORT).show();

                } else if (!pass.equals(cnpass)) {
                    Toast.makeText(registration.this,"Password Does not Match",Toast.LENGTH_SHORT).show();
                }else{
                    auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {

                                String id=task.getResult().getUser().getUid();
                                DatabaseReference reference=database.getReference().child("user").child(id);
                                StorageReference storageReference=storage.getReference().child("Upload").child(id);

                                if(imageUri!=null)
                                {

                                    storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if(task.isSuccessful()){

                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        imageuri=uri.toString();
                                                        Users user=new Users(id,name,email,pass,cnpass);
                                                        reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if(task.isSuccessful()){

                                                                    Intent intent=new Intent(registration.this,MainActivity.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                                else{

                                                                    Toast.makeText(registration.this,"Error in creating the user",Toast.LENGTH_SHORT).show();
                                                                }

                                                            }
                                                        });

                                                    }
                                                });
                                            }

                                        }
                                    });

                                }
                                else{
                                    imageuri="https://firebasestorage.googleapis.com/v0/b/chat-application-115f1.appspot.com/o/profile.png?alt=media&token=b2b9916f-de15-4bd8-90d5-248e1725ce24";
                                    Users user=new Users(id,name,email,pass,imageuri);
                                    reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){

                                                Intent intent=new Intent(registration.this,MainActivity2.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                            else{

                                                Toast.makeText(registration.this,"Error in creating the user",Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });

                                }





                            }else{
                                Toast.makeText(registration.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        re_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),10);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==10){
            if(data!=null){
                imageUri=data.getData();
                re_image.setImageURI(imageUri);
            }
        }
    }
}