package ca.ualberta.taskr;

/**
 * Created by Jacob Bakker on 2/22/2018.
 */

import android.media.Image;

import com.mapbox.mapboxsdk.geometry.LatLng;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.File;
import java.util.ArrayList;

import ca.ualberta.taskr.models.Bid;
import ca.ualberta.taskr.models.Task;
import ca.ualberta.taskr.models.TaskStatus;

import static junit.framework.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class TaskUnitTest {
    public String owner = "Corkus";
    public String title = "I'm a wot";
    public TaskStatus status;
    public ArrayList<Bid> bids = new ArrayList<Bid>();
    public String description = "4 mana 7/7";
    public ArrayList<Image> photos = new ArrayList<Image>();
    public LatLng location;
    public String chosenBidder = "The Mask";

    public String newBidName1 = "Mr. MoneyBags McGee's Monetary Mmmm";
    public String newBidName2 = "A Bribe";
    public float newBidAmount1 = 0.01f;
    public float newBidAmount2 = 6.66f;

    @Test
    public void testGetOwner() {
        Task task = new Task(owner, title, status, bids, description, photos, location, chosenBidder);
        String testOwner = task.getOwner();
        assertEquals(testOwner, owner);
    }

    @Test
    public void testGetTitle() {
        Task task = new Task(owner, title, status, bids, description, photos, location, chosenBidder);
        String testTitle = task.getTitle();
        assertEquals(testTitle, title);
    }

    @Test
    public void testGetStatus() {
        Task task = new Task(owner, title, status, bids, description, photos, location, chosenBidder);
        TaskStatus testStatus = task.getStatus();
        assertEquals(testStatus, status);
    }

    @Test
    public void testGetBids() {
        Task task = new Task(owner, title, status, bids, description, photos, location, chosenBidder);
        ArrayList<Bid> testBids = task.getBids();
        assertEquals(testBids, bids);
    }

    @Test
    public void testAddAndGetBid() {
        Task task = new Task(owner, title, status, bids, description, photos, location, chosenBidder);
        Bid newBid1 = new Bid(newBidName1, newBidAmount1);
        Bid newBid2 = new Bid(newBidName2, newBidAmount2);
        task.addBid(newBid1);
        task.addBid(newBid2);
        Bid testBid = task.getBidAtIndex(0);
        assertEquals(testBid, newBid1);
        testBid = task.getBidAtIndex(1);
        assertEquals(testBid, newBid2);
    }

    @Test
    public void testSetBidAtIndex() {
        Task task = new Task(owner, title, status, bids, description, photos, location, chosenBidder);
        Bid newBid1 = new Bid(newBidName1, newBidAmount1);
        Bid newBid2 = new Bid(newBidName2, newBidAmount2);
        task.addBid(newBid1);
        task.setBidAtIndex(newBid2, 0);
        Bid testBid = task.getBidAtIndex(0);
        assertEquals(testBid, newBid2);
    }
    @Test
    public void testGetDescription() {
        Task task = new Task(owner, title, status, bids, description, photos, location, chosenBidder);
        String testDescription = task.getDescription();
        assertEquals(testDescription, description);
    }

    @Test
    public void testGetPhotos() {
        Task task = new Task(owner, title, status, bids, description, photos, location, chosenBidder);
        ArrayList<Image> testPhotos = task.getPhotos();
        assertEquals(testPhotos, photos);
    }

    @Test
    public void testAddAndGetPhoto() {
        Task task = new Task(owner, title, status, bids, description, photos, location, chosenBidder);
        task.addPhoto(newPhoto1);
        Image testPhoto = task.getPhotoAtIndex(0);
        assertEquals(testPhoto, newPhoto1);
    }

    @Test
    public void testGetLocation() {
        Task task = new Task(owner, title, status, bids, description, photos, location, chosenBidder);
        LatLng testLocation = task.getLocation();
        assertEquals(testLocation, location);
    }
    @Test
    public void testGetChosenBidder() {
        Task task = new Task(owner, title, status, bids, description, photos, location, chosenBidder);
        String testChosenBidder = task.getChosenBidder();
        assertEquals(testChosenBidder, chosenBidder);
    }
}
