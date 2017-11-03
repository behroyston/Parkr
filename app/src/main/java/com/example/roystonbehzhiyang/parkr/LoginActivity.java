package com.example.roystonbehzhiyang.parkr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.example.roystonbehzhiyang.parkr.pojo.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseStorage fStorage;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText txtUsername;
    private EditText txtPassword;
    private ImageView hiddenFbPhoto;
    private static final String TAG = "Login Activity";
    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    SharedPreferences pref;
    private Editor editor;

    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLocationPermission();
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        fStorage = FirebaseStorage.getInstance();

        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = pref.edit();

        findViewById(R.id.btnLogin).setOnClickListener(this);
        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);

        //initialize Facebook Login Button
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton fbLoginButton = findViewById(R.id.btnFacebookLogin);
        fbLoginButton.setReadPermissions("email","public_profile");
        fbLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:Cancel");

            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:Error",error);
            }
        });


        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }else{
                    Log.d(TAG,user.getToken(true).toString());
                }
            }
        };
    }

    private void createUser(String uid, String displayName, String email, String photoURI){
        Users user = new Users(uid,displayName,email,"active",photoURI);
        mDatabase.child("Users").child(uid).setValue(user);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultsCode, Intent data){
        super.onActivityResult(requestCode, resultsCode, data);
        mCallbackManager.onActivityResult(requestCode, resultsCode, data);
    }

    /*private void saveFacebookImage(){
        final FirebaseUser user = mAuth.getCurrentUser();
        Uri fbPicUri = user.getPhotoUrl();

        Drawable drawFbImage = LoadImageFromWebOperations(fbPicUri.toString());
        hiddenFbPhoto.setImageDrawable(drawFbImage);

        StorageReference storageRef = fStorage.getReference();
        StorageReference fbImageReference = storageRef.child("profile_pic_" + user.getUid() +".jpg");

        UploadTask task =  fbImageReference.putFile(fbPicUri);
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests")Uri imageUri = taskSnapshot.getDownloadUrl();
                createUser(user.getUid(),user.getDisplayName(),user.getEmail(),imageUri.toString());
            }
        });
    }*/

    private void handleFacebookAccessToken(AccessToken accessToken) {
        Log.d(TAG, "handleFacebookAccessToken:"+ accessToken);
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "singInWithCredentials:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    String facebookPhotoURL;

                    for(UserInfo profile : user.getProviderData()){
                        if(FacebookAuthProvider.PROVIDER_ID.equals(profile.getProviderId())){
                            //Toast.makeText(LoginActivity.this,profile.getUid().toString(),Toast.LENGTH_SHORT).show();
                            facebookPhotoURL = "https://graph.facebook.com/"+ profile.getUid().toString() + "/picture?height=250";
                            createUser(user.getUid(),user.getDisplayName(),user.getEmail(),facebookPhotoURL);
                            saveUUID(user.getUid());
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }

                }else{
                    Log.w(TAG,"signInWithCredentials:failure", task.getException());
                    Toast.makeText(LoginActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loginAccount(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                if(!task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, R.string.auth_failed, Toast.LENGTH_SHORT);
                }else{
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    saveUUID(currentUser.getUid());

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    public void onSignUpClicked(View v){
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    public void onClick(View v){
        int i = v.getId();

        if(i == R.id.btnLogin){
            loginAccount(txtUsername.getText().toString(), txtPassword.getText().toString());
        }
    }

    public void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            saveUUID(currentUser.getUid());
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
    //saving the current logged in user UUID
    public void saveUUID(String uuid){
        editor.putString("uuid",uuid);
        editor.commit();
    }

    public void onTokenRefresh(){
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
}
