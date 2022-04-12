package com.example.testapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;

public class DbActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);

        InputStream is1 = this.getResources().openRawResource(R.raw.screen);
        Bitmap bm1 = BitmapFactory.decodeStream(is1);
        ((ImageView) findViewById(R.id.imageView)).setImageBitmap(bm1);

        InputStream is2 = this.getResources().openRawResource(R.raw.image2);
        Bitmap bm2 = BitmapFactory.decodeStream(is2);
        ((ImageView) findViewById(R.id.imageView2)).setImageBitmap(bm2);

        InputStream is3 = this.getResources().openRawResource(R.raw.image3);
        Bitmap bm3 = BitmapFactory.decodeStream(is3);
        ((ImageView) findViewById(R.id.imageView3)).setImageBitmap(bm3);

        InputStream is4 = this.getResources().openRawResource(R.raw.image4);
        Bitmap bm4 = BitmapFactory.decodeStream(is4);
        ((ImageView) findViewById(R.id.imageView4)).setImageBitmap(bm4);

        InputStream is5 = this.getResources().openRawResource(R.raw.fake1);
        Bitmap bm5 = BitmapFactory.decodeStream(is5);
        ((ImageView) findViewById(R.id.imageView5)).setImageBitmap(bm5);
    }
}