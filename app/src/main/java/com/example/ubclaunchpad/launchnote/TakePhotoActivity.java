package com.example.ubclaunchpad.launchnote;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TakePhotoActivity extends AppCompatActivity {

    static final int TAKE_PHOTO_REQUEST_CODE = 1;
    static final String DATE_FORMAT = "yyyyMMdd_HHmmss";
    String currentImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
    }

    public void takePhoto(View view) {
        // Intent to open up Android's camera
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // If it can be handled ...
        if (takePhotoIntent.resolveActivity(getPackageManager()) != null) {
            // File where the image goes
            File imageFile = null;
            try {
                imageFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // If the image file was created with no problems ...
            if (imageFile != null) {
                // Get URI from file and pass it as an extra to the intent, then start intent
                Uri imageURI = FileProvider.getUriForFile(this, "com.example.ubclaunchpad.launchnote.FileProvider", imageFile);
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
                startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST_CODE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // First, create file name
        String timestamp = new SimpleDateFormat(DATE_FORMAT).format(new Date());
        String fileName = "JPEG_" + timestamp;
        // Directory where the file will be stored (check file_paths.xml)
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        // Create file
        File image = File.createTempFile(fileName, ".jpg",  dir);
        currentImagePath = image.getAbsolutePath();
        return image;
    }

}
