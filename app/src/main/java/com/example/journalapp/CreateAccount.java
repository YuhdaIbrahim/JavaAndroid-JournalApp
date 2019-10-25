package com.example.journalapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import util.JournalApi;

public class CreateAccount extends AppCompatActivity {
  private Button createAcc;
  private FirebaseAuth firebaseAuth;
  private FirebaseAuth.AuthStateListener authStateListener;
  private FirebaseUser currentUser;

  //Firestore Coonnetction
  private FirebaseFirestore db = FirebaseFirestore.getInstance();
  private CollectionReference collectionReference = db.collection("Users");

  private EditText emailEditText, passEditText;
  private ProgressBar progressBar;
  private EditText usernameEdittxt;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_account);

    firebaseAuth = FirebaseAuth.getInstance();


    progressBar = findViewById(R.id.Create_progress);
    emailEditText = findViewById(R.id.email_account);
    passEditText = findViewById(R.id.password_account);
    createAcc = findViewById(R.id.Register2);
    usernameEdittxt = findViewById(R.id.username_acct);

    authStateListener = new FirebaseAuth.AuthStateListener() {
      @Override
      public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null){

        }else {

        }
      }
    };


    createAcc.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (!TextUtils.isEmpty(emailEditText.getText().toString())
            && !TextUtils.isEmpty(passEditText.getText().toString())
            &&!TextUtils.isEmpty(usernameEdittxt.getText().toString())){

          String email = emailEditText.getText().toString().trim();
          String password = passEditText.getText().toString().trim();
          String username = usernameEdittxt.getText().toString().trim();
          createUserEmailAccount(email, password, username);
        }else{
          Toast.makeText(CreateAccount.this,"Fill all Text!",Toast.LENGTH_LONG).show();
        }

      }
    });

  }
  private void createUserEmailAccount(String email, String password, final String username){
    if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(email)){

      progressBar.setVisibility(View.VISIBLE);

      firebaseAuth.createUserWithEmailAndPassword(email, password)
          .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
          if (task.isSuccessful()){
            currentUser = firebaseAuth.getCurrentUser();
            assert currentUser != null;
            final String currentUserId = currentUser.getUid();

            Map<String, String> userObj = new HashMap<>();
            userObj.put("userId",currentUserId);
            userObj.put("username",username);

            collectionReference.add(userObj).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
              @Override
              public void onSuccess(DocumentReference documentReference) {
                documentReference.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                  @Override
                  public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(Objects.requireNonNull(task.getResult()).exists()){
                      progressBar.setVisibility(View.INVISIBLE);
                      String name = task.getResult().getString("username");
                      JournalApi journalApi = JournalApi.getInstance();
                      journalApi.setUserId(currentUserId);
                      journalApi.setUsername(name);

                      Intent intent = new Intent(CreateAccount.this,PostJurnalActivity.class);
                      intent.putExtra("username", name);
                      intent.putExtra("userId", currentUserId);
                      startActivity(intent);

                    }else {
                      progressBar.setVisibility(View.INVISIBLE);
                      Toast.makeText(CreateAccount.this,"Intent failed",Toast.LENGTH_LONG).show();
                    }
                  }
                });
              }
            }).addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                Log.d("failure", "onFailure: " + e);
                Toast.makeText(CreateAccount.this, "onFailuer",
                    Toast.LENGTH_SHORT).show();
              }
            });

          }else {
            progressBar.setVisibility(View.INVISIBLE);
            Log.d("TaskFail", "createUserWithEmail:failure", task.getException());
            Toast.makeText(CreateAccount.this, "Authentication failed.",
                Toast.LENGTH_SHORT).show();
          }
        }
      })
          .addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
          progressBar.setVisibility(View.INVISIBLE);
          Toast.makeText(CreateAccount.this,"Connection Failed",Toast.LENGTH_LONG).show();
        }
      });
    }else{

    }
  }

  @Override
  protected void onStart() {
    super.onStart();

    currentUser = firebaseAuth.getCurrentUser();
    firebaseAuth.addAuthStateListener(authStateListener);
  }
}
