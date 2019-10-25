package com.example.journalapp;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import util.JournalApi;

public class PostJurnalActivity extends AppCompatActivity implements View.OnClickListener {

  private static final int GALLERY_CODE = 1;
  private Button save_button;
  private ProgressBar post_progress;
  private ImageView addPhotoButton;
  private EditText tittleEdit,descEdit;
  private TextView currentUsertext;
  private ImageView imageView;

  private String currentId;
  private String currenUsername;

  private FirebaseAuth firebaseAuth;
  private FirebaseAuth.AuthStateListener authStateListener;
  private FirebaseUser firebaseUser;

  private FirebaseFirestore db =  FirebaseFirestore.getInstance();
  private StorageReference storageReference;

  private CollectionReference collectionReference = db.collection("Journal");
  private Uri ImageUrl;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_post_jurnal);

    firebaseAuth = FirebaseAuth.getInstance();
    post_progress = findViewById(R.id.post_progress);
    tittleEdit = findViewById(R.id.post_title);
    descEdit = findViewById(R.id.post_desc);
    currentUsertext = findViewById(R.id.post_username_text);
    imageView = findViewById(R.id.imgview2);

    save_button = findViewById(R.id.post_button);
    save_button.setOnClickListener(this);
    addPhotoButton = findViewById(R.id.postCameraButton);
    addPhotoButton.setOnClickListener(this);

    if (JournalApi.getInstance() != null){
      currentId = JournalApi.getInstance().getUserId();
      currenUsername = JournalApi.getInstance().getUsername();

      currentUsertext.setText(currenUsername);
    }
    authStateListener = new FirebaseAuth.AuthStateListener() {
      @Override
      public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null){

        }else {

        }


      }
    };

  }

  @Override
  public void onClick(View view) {
    switch (view.getId()){
      case R.id.post_button:

        break;
      case R.id.postCameraButton:
        Intent intentGallery = new Intent(Intent.ACTION_GET_CONTENT);
        intentGallery.setType("Image/*");
        startActivityForResult(intentGallery, GALLERY_CODE);
        break;
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == GALLERY_CODE && resultCode == RESULT_OK){
      if (data != null){
        ImageUrl = data.getData();
        imageView.setImageURI(ImageUrl);
      }
    }

  }

  @Override
  protected void onStart() {
    super.onStart();
    firebaseUser = firebaseAuth.getCurrentUser();
    firebaseAuth.addAuthStateListener(authStateListener);
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (firebaseAuth != null){
      firebaseAuth.removeAuthStateListener(authStateListener);
    }
  }
}
