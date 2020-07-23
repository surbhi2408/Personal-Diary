package com.example.diary;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class RateApp extends AppCompatActivity {

    TextView txtRating,txtSelect,txtDescription,txtThank;
    RatingBar ratingBar;
    Button button;
    float myRating = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_app);

        getSupportActionBar().setTitle("RateApp");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtRating = findViewById(R.id.txtRate);
        txtSelect = findViewById(R.id.selectText);
        txtDescription = findViewById(R.id.descriptionText);
        txtThank = findViewById(R.id.thankYouText);
        ratingBar = findViewById(R.id.ratingBar);
        button = findViewById(R.id.RateSubmit);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                int rating = (int)v;
                String message = null;
                myRating = ratingBar.getRating();
                switch (rating)
                {
                    case 1:
                        message = "Sorry to hear that! :(";
                        break;
                    case 2:
                        message = "You always accept solutions!";
                        break;
                    case 3:
                        message = "Good Enough!";
                        break;
                    case 4:
                        message = "Great! Thank You!";
                        break;
                    case 5:
                        message = "Awesome! You are the best!";
                        break;
                }
                Toast.makeText(RateApp.this, message, Toast.LENGTH_SHORT).show();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RateApp.this, "Your rating is: " + myRating, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
