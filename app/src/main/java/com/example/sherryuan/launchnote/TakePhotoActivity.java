package com.example.sherryuan.launchnote;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TakePhotoActivity extends AppCompatActivity {

    static final int TAKE_PHOTO_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
    }

}
