package com.example.ubclaunchpad.launchnote;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.example.ubclaunchpad.launchnote.database.FolderDao;
import com.example.ubclaunchpad.launchnote.database.PicNoteDao;
import com.example.ubclaunchpad.launchnote.database.LaunchNoteDatabase;
import com.example.ubclaunchpad.launchnote.models.PicNote;
import com.example.ubclaunchpad.launchnote.models.Folder;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.graphics.Bitmap;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import java.io.IOException;
import java.util.List;

/**
 * Unit test for PicNoteDao
 */
@RunWith(AndroidJUnit4.class)
public class LaunchNoteTableTest {
    private PicNoteDao picNoteDao;
    private FolderDao folderDao;
    private LaunchNoteDatabase testDatabase;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        testDatabase = Room.inMemoryDatabaseBuilder(context, LaunchNoteDatabase.class).build();
        picNoteDao = testDatabase.picNoteDao();
        folderDao = testDatabase.folderDao();
    }

    @After
    public void closeDb() throws IOException {
        testDatabase.close();
    }

    @Test
    public void testPicNoteDaoQueries() throws Exception {
        // create first picNote with dummy values
        PicNote picNote1 = new PicNote(1, "../test/uri1.png", "../test/uri1.png", "test picNote1", "", Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888));
        picNoteDao.insert(picNote1);

        // create second picNote with dummy values
        PicNote picNote2 = new PicNote(2, "../test/uri2.png", "../test/uri1.png", "test picNote2","", Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888));
        picNoteDao.insert(picNote2);

        // create second picNote with dummy values
        PicNote picNote3 = new PicNote(3, "../test/uri3.png","../test/uri1.png", "test picNote3","", Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888));
        picNoteDao.insert(picNote3);

        // test that we can find the first PicNote
        List<PicNote> picNoteById1 = picNoteDao.findById(1).blockingFirst();
        Assert.assertEquals(picNoteById1.get(0).getImageUri(), picNote1.getImageUri());
        Assert.assertEquals(picNoteById1.get(0).getDescription(), picNote1.getDescription());

        Assert.assertEquals(picNoteById1.size(), 1);

        // test that we can find the third PicNote
        List<PicNote> picNoteById3 = picNoteDao.findById(3).blockingFirst();
        Assert.assertEquals(picNoteById3.get(0).getImageUri(), picNote3.getImageUri());
        Assert.assertEquals(picNoteById3.get(0).getDescription(), picNote3.getDescription());

        Assert.assertEquals(picNoteById3.size(), 1);

        // test that we can get all the PicNotes
        List<PicNote> allPicNotes = picNoteDao.loadAll().blockingFirst();
        Assert.assertEquals(allPicNotes.get(0).getDescription(), picNote1.getDescription());
        Assert.assertEquals(allPicNotes.get(1).getDescription(), picNote2.getDescription());
        Assert.assertEquals(allPicNotes.get(2).getDescription(), picNote3.getDescription());

        Assert.assertEquals(allPicNotes.size(), 3);
    }

    @Test
    public void testFolderDaoQueries() {
        // create first class with dummy values
        Folder folder1 = new Folder("test folder1");
        Folder folder2 = new Folder("test folder2");
        Folder folder3 = new Folder("test folder3");
        folderDao.insertAll(folder1, folder2, folder3);

        // test that we can find the first LaunchNoteClass
        List<Folder> folderById1 = folderDao.findById("1").blockingFirst();
        Assert.assertEquals(folderById1.get(0).getDescription(), folder1.getDescription());
        Assert.assertEquals(folderById1.size(), 1);

        // test that we can get all the PicNotes
        List<Folder> allFolders = folderDao.loadAll().blockingFirst();
        Assert.assertEquals(allFolders.get(0).getDescription(), folder1.getDescription());
        Assert.assertEquals(allFolders.get(1).getDescription(), folder2.getDescription());
        Assert.assertEquals(allFolders.get(2).getDescription(), folder3.getDescription());

        Assert.assertEquals(allFolders.size(), 3);

        // test delete
        folder1.setId(1);
        folderDao.delete(folder1);
        allFolders = folderDao.loadAll().blockingFirst();
        Assert.assertFalse(allFolders.contains(folder1));
        Assert.assertEquals(allFolders.size(), 2);
    }
}
