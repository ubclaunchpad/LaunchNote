package com.example.ubclaunchpad.launchnote;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.ubclaunchpad.launchnote.database.PicNoteDatabase;
import com.example.ubclaunchpad.launchnote.gallery.GalleryActivity;
import com.example.ubclaunchpad.launchnote.models.PicNote;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        List<PicNote> picNotes = PicNoteDatabase.Companion.getDatabase(this).picNoteDao().loadAll();

        for (PicNote next: picNotes) {
            Toast.makeText(this, next.getId() + next.getImageUri(), Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.buttonUploadFromGallery)
    public void launchGalleryActivity(View view) {
        Intent galleryActivityIntent = new Intent(MainActivity.this, GalleryActivity.class);
        startActivity(galleryActivityIntent);
    }
}
