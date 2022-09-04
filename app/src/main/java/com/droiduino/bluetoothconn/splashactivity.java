package com.droiduino.bluetoothconn;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("CustomSplashScreen")
public class splashactivity extends AppCompatActivity {
    //variables
    Animation bottomAnim , topAnim;
    ImageView image;
    TextView logo, slogan;  //used to apply animation on text

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splashactivity);
        //Objects.requireNonNull(getSupportActionBar()).hide();
        //animations
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_animation);
        //hooks
        image = findViewById(R.id.imageView);
        logo = findViewById(R.id.textView);
        slogan = findViewById(R.id.textView2);
        //applying animation on text and image
        image.setAnimation(topAnim);
        slogan.setAnimation(bottomAnim);
        logo.setAnimation(bottomAnim);


        Thread td = new Thread() {

            public void run() {

                try {
//                    Hold the splashscreen
                    sleep(800);

                } catch (Exception ex) {

                    ex.printStackTrace();

                } finally {
//                        go to splash screen to main activity
                    Intent intent = new Intent(splashactivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                }

            }


        };td.start();


    }
}