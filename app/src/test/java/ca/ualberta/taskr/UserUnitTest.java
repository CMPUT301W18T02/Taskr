package ca.ualberta.taskr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.ImageReader;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.File;

import ca.ualberta.taskr.models.User;

import static junit.framework.Assert.assertEquals;

/**
 * Created by marissasnihur on 2018-02-22.
 */
@RunWith(RobolectricTestRunner.class)
public class UserUnitTest {

    public String name = "John";
    public String email = "jsmith@ualberta.ca";
    public String phoneNumber = "1234567890";
    public String username = "jsmith";

    public Image image;


    @Test
    public void testGetName() {
        //File path = new File("Taskr/docs/TestPic.png");
        Bitmap bitmap = BitmapFactory.decodeFile("Taskr/docs/TestPic.png");
        User user = new User(name, phoneNumber, image, email, username);
        String returnedValue = user.getName();
        assertEquals(returnedValue, name);
    }

    @Test
    public void testGetEmail() {
        User user = new User(name, phoneNumber, image, email, username);
        String returnedValue = user.getEmail();
        assertEquals(returnedValue, email);
    }

    @Test
    public void testGetPhoneNumber() {
        User user = new User(name, phoneNumber, image, email, username);
        String returnedValue = user.getPhoneNumber();
        assertEquals(returnedValue, phoneNumber);
    }

    @Test
    public void testGetUsername() {
        User user = new User(name, phoneNumber, image, email, username);
        String returnedValue = user.getUsername();
        assertEquals(returnedValue, username);
    }

    @Test
    public void testGetImage() {
        User user = new User(name, phoneNumber, image, email, username);
        Image returnedValue = user.getProfilePicture();
        assertEquals(returnedValue, image);
    }

    @Test
    public void testSetName(){
        User user = new User(name, phoneNumber, image, email, username);
        String returnedValue = user.setName("NSJDBA");
        assertEquals(returnedValue, name);
    }
}
