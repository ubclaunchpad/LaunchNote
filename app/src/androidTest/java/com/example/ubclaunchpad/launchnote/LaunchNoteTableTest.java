package com.example.ubclaunchpad.launchnote;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.example.ubclaunchpad.launchnote.database.ClassDao;
import com.example.ubclaunchpad.launchnote.database.PicNoteDao;
import com.example.ubclaunchpad.launchnote.database.LaunchNoteDatabase;
import com.example.ubclaunchpad.launchnote.database.ProjectDao;
import com.example.ubclaunchpad.launchnote.models.LaunchNoteClass;
import com.example.ubclaunchpad.launchnote.models.PicNote;
import com.example.ubclaunchpad.launchnote.models.Project;

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
    private ClassDao classDao;
    private ProjectDao projectDao;
    private LaunchNoteDatabase testDatabase;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        testDatabase = Room.inMemoryDatabaseBuilder(context, LaunchNoteDatabase.class).build();
        picNoteDao = testDatabase.picNoteDao();
        classDao = testDatabase.classDao();
        projectDao = testDatabase.projectDao();
    }

    @After
    public void closeDb() throws IOException {
        testDatabase.close();
    }

    @Test
    public void testPicNoteDaoQueries() throws Exception {
        // create first picNote with dummy values
        PicNote picNote1 = new PicNote("../test/uri1.png", "test picNote1", Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888));
        picNoteDao.insert(picNote1);

        // create second picNote with dummy values
        PicNote picNote2 = new PicNote("../test/uri2.png", "test picNote2", Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888));
        picNoteDao.insert(picNote2);

        // create second picNote with dummy values
        PicNote picNote3 = new PicNote("../test/uri3.png", "test picNote3", Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888));
        picNoteDao.insert(picNote3);

        // test that we can find the first PicNote
        List<PicNote> picNoteById1 = picNoteDao.findById("1").blockingFirst();
        Assert.assertEquals(picNoteById1.get(0).getImageUri(), picNote1.getImageUri());
        Assert.assertEquals(picNoteById1.get(0).getDescription(), picNote1.getDescription());

        Assert.assertEquals(picNoteById1.size(), 1);

        // test that we can find the third PicNote
        List<PicNote> picNoteById3 = picNoteDao.findById("3").blockingFirst();
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
        LaunchNoteClass class1 = new LaunchNoteClass("test class1");
        LaunchNoteClass class2 = new LaunchNoteClass("test class2");
        LaunchNoteClass class3 = new LaunchNoteClass("test class3");
        classDao.insertAll(class1, class2, class3);

        // test that we can find the first LaunchNoteClass
        List<LaunchNoteClass> classById1 = classDao.findById("1").blockingFirst();
        Assert.assertEquals(classById1.get(0).getDescription(), class1.getDescription());
        Assert.assertEquals(classById1.size(), 1);

        // test that we can find the third LaunchNoteClass
        List<LaunchNoteClass> classById3 = classDao.findById("3").blockingFirst();
        Assert.assertEquals(classById3.get(0).getDescription(), class3.getDescription());

        Assert.assertEquals(classById3.size(), 1);

        // test that we can get all the LaunchNoteClasses
        List<LaunchNoteClass> allClasses = classDao.loadAll().blockingFirst();
        Assert.assertEquals(allClasses.get(0).getDescription(), class1.getDescription());
        Assert.assertEquals(allClasses.get(1).getDescription(), class2.getDescription());
        Assert.assertEquals(allClasses.get(2).getDescription(), class3.getDescription());

        Assert.assertEquals(allClasses.size(), 3);
    }

    @Test
    public void testProjectDaoQueries() {
        // create first class with dummy values
        Project project1 = new Project("test project1");
        Project project2 = new Project("test project2");
        Project project3 = new Project("test project3");
        projectDao.insertAll(project1, project2, project3);

        // test that we can find the first LaunchNoteClass
        List<Project> projectById1 = projectDao.findById("1").blockingFirst();
        Assert.assertEquals(projectById1.get(0).getDescription(), project1.getDescription());
        Assert.assertEquals(projectById1.size(), 1);

        // test that we can get all the PicNotes
        List<Project> allProjects = projectDao.loadAll().blockingFirst();
        Assert.assertEquals(allProjects.get(0).getDescription(), project1.getDescription());
        Assert.assertEquals(allProjects.get(1).getDescription(), project2.getDescription());
        Assert.assertEquals(allProjects.get(2).getDescription(), project3.getDescription());

        Assert.assertEquals(allProjects.size(), 3);

        // test delete
        project1.setId(1);
        projectDao.delete(project1);
        allProjects = projectDao.loadAll().blockingFirst();
        Assert.assertFalse(allProjects.contains(project1));
        Assert.assertEquals(allProjects.size(), 2);
    }
}
