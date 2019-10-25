package com.example.journalapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.nio.channels.AcceptPendingException;

public class MainActivity extends AppCompatActivity {

  private Button getStarted;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    getStarted = findViewById(R.id.getStarted);
    getStarted.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
      }
    });
  }
}
