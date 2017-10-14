package com.example.ubclaunchpad.launchnote.gallery;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.example.ubclaunchpad.launchnote.database.PicNoteDatabase;
import com.example.ubclaunchpad.launchnote.models.PicNote;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Activity for selecting an image from photo gallery
 */
public class GalleryActivity extends Activity {

    private static int RESULT_LOAD_IMG = 1;
    ImageView photoView;
    Bitmap photoBitmap;
    Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        photoView = findViewById(R.id.imgView);
        ButterKnife.bind(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // when an Image is picked
        if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {
            // get Image from data
            photoUri = data.getData();
            // load Bitmap using Glide
            Glide.with(this)
                    .asBitmap()
                    .load(photoUri)
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

    @OnClick(R.id.buttonLoadPicture)
    public void loadImageFromGallery(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    @OnClick(R.id.buttonSavePicture)
    public void saveImageToDb(View view) {
        if (!(photoUri == null) && !(photoBitmap == null)) {
            // TODO: parse out the description
            PicNote picNoteToSave = new PicNote(photoUri.toString(), "", photoBitmap);

            // save the PicNote into the database
            PicNoteDatabase.Companion.getDatabase(this).picNoteDao().insert(picNoteToSave);
            finish();
        } else {
            Toast.makeText(this, "You haven't picked an image", Toast.LENGTH_LONG).show();
        }
    }
}
