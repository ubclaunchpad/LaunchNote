package com.example.ubclaunchpad.launchnote.gallery;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.ubclaunchpad.launchnote.R;

/**
 * Activity for selecting an image from photo gallery
 */
public class GalleryActivity extends Activity {

    private static int RESULT_LOAD_IMG = 1;
    ImageView photoView;
    Bitmap photoBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        photoView = findViewById(R.id.imgView);

    }

    public void loadImageFromGallery(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // when an Image is picked
        if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {
            // get Image from data
            Uri selectedImage = data.getData();
            // load Bitmap using Glide
            Glide.with(this)
                    .asBitmap()
                    .load(selectedImage)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition transition) {
                            // have photoView display the loaded Bitmap
                            photoView.setImageBitmap(resource);
                            // set photoBitmap to the loaded Bitmap
                            photoBitmap = resource;
                        }
                    });

        } else {
            Toast.makeText(this, "You haven't picked an image", Toast.LENGTH_LONG).show();

        }
    }


}
