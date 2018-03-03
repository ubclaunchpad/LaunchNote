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
        folderDao = testDatabase.projectDao();
    }

    @After
    public void closeDb() throws IOException {
        testDatabase.close();
    }

    @Test
    public void testPicNoteDaoQueries() throws Exception {
        // create first picNote with dummy values
        PicNote picNote1 = new PicNote("../test/uri1.png", "../test/uri1.png", "test picNote1", "", 0, Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888));
        picNoteDao.insert(picNote1);

        // create second picNote with dummy values
        PicNote picNote2 = new PicNote("../test/uri2.png", "../test/uri1.png", "test picNote2","", 0, Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888));
        picNoteDao.insert(picNote2);

        // create second picNote with dummy values
        PicNote picNote3 = new PicNote("../test/uri3.png","../test/uri1.png", "test picNote3","", 0, Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888));
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
    public void testClassDaoQueries() {
        // create first LaunchNoteClass with dummy values
        Folder class1 = new Folder("test folder1");
        Folder class2 = new Folder("test folder2");
        Folder class3 = new Folder("test folder3");
        folderDao.insertAll(class1, class2, class3);

        // test that we can find the first LaunchNoteClass
        List<Folder> classById1 = folderDao.findById("1").blockingFirst();
        Assert.assertEquals(classById1.get(0).getDescription(), class1.getDescription());
        Assert.assertEquals(classById1.size(), 1);

        // test that we can find the third LaunchNoteClass
        List<Folder> classById3 = folderDao.findById("3").blockingFirst();
        Assert.assertEquals(classById3.get(0).getDescription(), class3.getDescription());

        Assert.assertEquals(classById3.size(), 1);

        // test that we can get all the LaunchNoteClasses
        List<Folder> allClasses = folderDao.loadAll().blockingFirst();
        Assert.assertEquals(allClasses.get(0).getDescription(), class1.getDescription());
        Assert.assertEquals(allClasses.get(1).getDescription(), class2.getDescription());
        Assert.assertEquals(allClasses.get(2).getDescription(), class3.getDescription());

        Assert.assertEquals(allClasses.size(), 3);
    }

    @Test
    public void testProjectDaoQueries() {
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
