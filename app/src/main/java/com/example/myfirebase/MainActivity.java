package com.example.myfirebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final int RC_sign_in = 1000;
    Button btn;
    EditText editText;
    FirebaseUser firebaseUser;
    ArrayList<String> notes;
    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = findViewById(R.id.btn);
        editText = findViewById(R.id.et);
        listView = findViewById(R.id.lv);
        notes = new ArrayList<>();
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter(this, R.layout.each_row, R.id.tvlist, notes);
        listView.setAdapter(arrayAdapter);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //important points abt authentication    //value of user can be null i.e user is not logged in
//        user can either 1. login scsfly
//        2. network prob
//        3.user cancelled login
//                4.login failed - incorrect password
//        using the value of frbsuser we can get to know what ahs happened on the login screen among the four possibiltires.
//        after the login screen closes we get a callback to the mainactivity with some data , the daat tells wht happened in th login screen
        if (firebaseUser != null) {
            //logged in
            // get refernce of our databse
            final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String s = editText.getText().toString();
                    myRef.child("Note").push().setValue(s);
                    //to create child node in the database
                    //                myRef.child(" Node1").push().setValue(s);
//                myRef.child("Node 2").push().setValue(s);
                }
            });
            //child listener for redaig th edata frm the databse
            myRef.child("Note").addChildEventListener(new ChildEventListener(){  @Override
                                                          public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                                            String data=dataSnapshot.getValue(String.class);
                                                            notes.add(data);
                                                            arrayAdapter.notifyDataSetChanged();
                                                          }

                                                          @Override
                                                          public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                                          }

                                                          @Override
                                                          public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                                                          }

                                                          @Override
                                                          public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                                          }

                                                          @Override
                                                          public void onCancelled(@NonNull DatabaseError databaseError) {

                                                          }} );
            //read from the database using addvalue eventlistener
            //    //        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//
//                String value = dataSnapshot.getValue(String.class);
//                Log.d("TAG", "Value is:" + value);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                //when failed to reda value
//                Log.w("TAG", " Failed to read value." + databaseError.toException());
//            }
//        });
            //read the data using child listener
            //better in comparison to the addvalue listener

        } else {
            // user not logged in
            //trying to log the user in
            startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setIsSmartLockEnabled(false)
                    .setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build(),
                    new AuthUI.IdpConfig.GoogleBuilder().build())).build(), RC_sign_in);
        }
    }

    //   if the user sign in succesfly then we get a callback to the onActivityResult  function
    //this function will tell what happened in the loggin scren
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_sign_in) {
            IdpResponse idpResponse = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {

                //succesfly signed in
                final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String s = editText.getText().toString();
                        myRef.child("Note").push().setValue(s);
                    }
                });
                //child listener for reding th edata frm the databse
                myRef.child("Note").addChildEventListener(new ChildEventListener() {    @Override
                                                              public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                                                 String data = dataSnapshot.getValue(String.class);
                                                                 notes.add(data);
                                                                 arrayAdapter.notifyDataSetChanged();
                                                              }

                                                              @Override
                                                              public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                                              }

                                                              @Override
                                                              public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                                                              }

                                                              @Override
                                                              public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                                              }

                                                              @Override
                                                              public void onCancelled(@NonNull DatabaseError databaseError) {

                                                              }}  );
                firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
                Log.e("TAG","inside onActivityResult: "+ firebaseUser.getDisplayName());
                Log.e("TAG","inside OnActivityResult:"+ firebaseUser.getUid());
            } else {
                //sign in failed
                if(idpResponse==null){
                    //user pressed back button

                }
                if (idpResponse.getError().getErrorCode()== ErrorCodes.NO_NETWORK){
                    return;
                }
            }
        }
    }

}
