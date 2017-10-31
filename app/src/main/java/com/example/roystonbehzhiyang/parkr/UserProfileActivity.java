package com.example.roystonbehzhiyang.parkr;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.roystonbehzhiyang.parkr.pojo.Users;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class UserProfileActivity extends AppCompatActivity {

    SharedPreferences pref;
    private ImageView profileImage;
    private EditText txtDisplayName;
    private EditText txtEmail;
    private String currentUUID;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        currentUUID = pref.getString("uuid","");

        profileImage = (ImageView)findViewById(R.id.ivProfileImage);
        txtDisplayName = (EditText)findViewById(R.id.txtDisplayName);
        txtEmail = (EditText)findViewById(R.id.txtEmail);
    }

    @Override
    public void onStart(){
        super.onStart();

        mDatabase.orderByChild("uid").equalTo(currentUUID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Users currentUser = dataSnapshot.getValue(Users.class);
                    Picasso.with(getApplicationContext()).load(currentUser.photoURI).into(profileImage);
                    txtDisplayName.setText(currentUser.displayName);
                    txtEmail.setText(currentUser.email);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
