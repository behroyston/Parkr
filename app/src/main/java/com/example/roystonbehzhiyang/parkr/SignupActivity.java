package com.example.roystonbehzhiyang.parkr;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.roystonbehzhiyang.parkr.pojo.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileDescriptor;
import java.io.IOException;


public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseStorage fStorage;
    private DatabaseReference mDatabase;
    private EditText txtEmail;
    private EditText txtPassword;
    private EditText txtDisplayName;
    public static final int RESULT_LOAD_IMAGE = 1;
    private Uri profilePicPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        fStorage = FirebaseStorage.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        txtEmail = findViewById(R.id.txtEmailAddress);
        txtPassword = findViewById(R.id.txtPassword);
        txtDisplayName = findViewById(R.id.txtDisplayName);
        findViewById(R.id.btnSignUp).setOnClickListener(this);
        findViewById(R.id.profileImageView).setOnClickListener(this);
    }

    private void createAccount(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>(){
            @Override
            public void onComplete(@NonNull Task<AuthResult> task){
                if(!task.isSuccessful()){
                    Toast.makeText(SignupActivity.this, R.string.auth_failed, Toast.LENGTH_SHORT).show();
                }else{
                    uploadImageToServer();
                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void createUser(String uid, String displayName, String email, String photoURI){
        Users user = new Users(uid,displayName,email,"active",photoURI);
        mDatabase.child("Users").child(uid).setValue(user);
    }

    public void onClick(View v){
        int i = v.getId();

        if(i == R.id.btnSignUp){
            createAccount(txtEmail.getText().toString(),txtPassword.getText().toString());
        }else if(i == R.id.profileImageView){
            selectImage();
        }
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null){
            //Uri selectedImage = data.getData();
            profilePicPath = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(profilePicPath,filePathColumn,null,null,null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            ImageView imageView = (ImageView)findViewById(R.id.profileImageView);
            try {
                imageView.setImageBitmap(getBitmapFromUri(profilePicPath));
            }catch(IOException ex){
                ex.printStackTrace();
            }
        }
    }

    private Bitmap getBitmapFromUri(Uri selectedImage) throws IOException{
        ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(selectedImage,"r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    private void uploadImageToServer(){
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        StorageReference storageRef = fStorage.getReference();
        StorageReference profileImgRef = storageRef.child("profile_pic_" + currentUser.getUid() +".jpg");
        //StorageReference profilePathReference = storageRef.child("profilePic/profile_pic_" + currentUser.getUid() + ".jpg");

        /*ImageView imageView = (ImageView)findViewById(R.id.profileImageView);
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = imageView.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();*/

        UploadTask uploadTask = profileImgRef.putFile(profilePicPath);
        uploadTask.addOnFailureListener(new OnFailureListener(){
            @Override
            public void onFailure(@NonNull Exception exception){
                //handle unsuccessful upload
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();

                createUser(currentUser.getUid(),txtDisplayName.getText().toString(),txtEmail.getText().toString(),downloadUrl.toString());
            }
        });

    }


}
