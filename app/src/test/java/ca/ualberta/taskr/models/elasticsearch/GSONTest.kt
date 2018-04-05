package ca.ualberta.taskr.models.elasticsearch

import ca.ualberta.taskr.models.Bid
import ca.ualberta.taskr.models.Task
import ca.ualberta.taskr.models.TaskStatus
import ca.ualberta.taskr.models.User
import com.google.gson.reflect.TypeToken
import com.mapbox.mapboxsdk.geometry.LatLng
import org.junit.Assert.assertEquals
import org.junit.Test
import java.lang.reflect.Type
import java.util.*

/**
 * 3/5/2018
 *
 *
 * Copyright (c) 2018 Brendan Samek. All Rights Reserved.
 */
class GSONTest {
    private val user = User("Jim", "7809991111", null, "jim@mail.com", "jim")

    private var owner = "Corkus"
    private var title = "I'm a wot"
    private var status: TaskStatus? = TaskStatus.ASSIGNED
    private var description = "4 mana 7/7"
    private var photos = ArrayList<String>()
    private var location: LatLng = LatLng(40.1231, 12.12321)
    private var chosenBidder = "The Mask"
    private var newBidName1 = "Mr. MoneyBags McGee's Monetary Mmmm"
    private var newBidName2 = "A Bribe"
    private var newBidAmount1 = 0.01f
    private var newBidAmount2 = 6.66f
    val newBid1 = Bid(newBidName1, newBidAmount1.toDouble(), false)
    val newBid2 = Bid(newBidName2, newBidAmount2.toDouble(), false)
    private var bids = ArrayList<Bid>()

    init {
        bids.add(newBid1)
        bids.add(newBid2)
    }


    private val task = Task(owner, title, status, bids, description, photos, location, chosenBidder)


    private val gson = GenerateRetrofit.generateGson()

    @Test
    fun getUserJson() {
        val json = "{\"name\":\"Jim\",\"phoneNumber\":\"7809991111\",\"profilePicture\":null,\"email\":\"jim@mail.com\",\"username\":\"jim\"}"

        assertEquals(json, gson.toJson(user))
    }

    @Test
    fun getTaskJson() {
        val json = "{\"owner\":\"Corkus\",\"title\":\"I\\u0027m a wot\",\"status\":\"ASSIGNED\",\"bids\":[{\"owner\":\"Mr. MoneyBags McGee\\u0027s Monetary Mmmm\",\"amount\":0.009999999776482582,\"isDismissed\":false},{\"owner\":\"A Bribe\",\"amount\":6.659999847412109,\"isDismissed\":false}],\"description\":\"4 mana 7/7\",\"photos\":[],\"location\":{\"latitude\":40.1231,\"longitude\":12.12321,\"altitude\":0.0},\"chosenBidder\":\"The Mask\"}"
        assertEquals(json,gson.toJson(task))
    }

    @Test
    fun serverToUsers() {
        val serverJson = "{\"hits\":{\"hits\":[{\"_source\":{\"name\": \"Lynn Stephens\", \"phoneNumber\": \"1-749-949-4881\", \"email\": \"zimmermanstacey@silva.info\", \"username\": \"lynn\", \"profilePicture\": null}},{\"_source\":{\"name\": \"Kim Wallace\", \"phoneNumber\": \"597-096-8855x26141\", \"email\": \"thomastonya@gmail.com\", \"username\": \"kim\", \"profilePicture\": null}},{\"_source\":{\"name\": \"John Kelly\", \"phoneNumber\": \"980-062-5671\", \"email\": \"sabrinachen@zimmerman-miller.com\", \"username\": \"john\", \"profilePicture\": null}},{\"_source\":{\"name\": \"William Berger\", \"phoneNumber\": \"040-284-7779\", \"email\": \"amber05@atkins.com\", \"username\": \"william\", \"profilePicture\": null}},{\"_source\":{\"name\": \"Thomas Camacho\", \"phoneNumber\": \"996-196-2187\", \"email\": \"cody68@sims-stephens.com\", \"username\": \"thomas\", \"profilePicture\": null}},{\"_source\":{\"name\": \"Richard Robbins\", \"phoneNumber\": \"+99(1)7046393461\", \"email\": \"pamelajohnston@hanson.com\", \"username\": \"richard\", \"profilePicture\": null}}]}}"
        val userListType: Type = object : TypeToken<List<User>>() {}.type
        val userList:List<User> = listOf(User(name="Lynn Stephens", phoneNumber="1-749-949-4881", profilePicture=null, email="zimmermanstacey@silva.info", username="lynn"), User(name="Kim Wallace", phoneNumber="597-096-8855x26141", profilePicture=null, email="thomastonya@gmail.com", username="kim"), User(name="John Kelly", phoneNumber="980-062-5671", profilePicture=null, email="sabrinachen@zimmerman-miller.com", username="john"), User(name="William Berger", phoneNumber="040-284-7779", profilePicture=null, email="amber05@atkins.com", username="william"), User(name="Thomas Camacho", phoneNumber="996-196-2187", profilePicture=null, email="cody68@sims-stephens.com", username="thomas"), User(name="Richard Robbins", phoneNumber="+99(1)7046393461", profilePicture=null, email="pamelajohnston@hanson.com", username="richard"))
        val usersFromJson = gson.fromJson<List<User>>(serverJson, userListType)
        assertEquals(userList, usersFromJson)
    }

    @Test
    fun serverToTasks() {
        val serverJson = "{\"hits\":{\"hits\":[{\"_index\":\"cmput301w18t02\",\"_type\":\"task\",\"_id\":\"AWH6QhnSXBxuGPxAgwG6\",\"_score\":1.0,\"_source\":{\"owner\": \"ryan\", \"title\": \"unleash user-centric metrics\", \"status\": \"DONE\", \"bids\": [{\"owner\": \"thomas\", \"amount\": 1096.74}, {\"owner\": \"mr.\", \"amount\": 3765.2}, {\"owner\": \"kim\", \"amount\": 2833.19}, {\"owner\": \"william\", \"amount\": 177.97}, {\"owner\": \"lynn\", \"amount\": 3213.34}, {\"owner\": \"mary\", \"amount\": 1464.36}, {\"owner\": \"john\", \"amount\": 6149.91}, {\"owner\": \"richard\", \"amount\": 3767.3}, {\"owner\": \"william\", \"amount\": 6380.1}], \"description\": \"Hospital particular benefit possible defense if. Government exist seek.\\nBase tonight recently world bar must. Financial power security tax. Notice although participant than health bit.\", \"photos\": [], \"location\": {\"latitude\": -61.78094842150479, \"longitude\": 18.443136034102086, \"altitude\": 0.0}, \"chosenBidder\": null}},{\"_index\":\"cmput301w18t02\",\"_type\":\"task\",\"_id\":\"AWH6Qhu4XBxuGPxAgwG7\",\"_score\":1.0,\"_source\":{\"owner\": \"mr.\", \"title\": \"orchestrate rich models\", \"status\": \"DONE\", \"bids\": [{\"owner\": \"ryan\", \"amount\": 2318.54}, {\"owner\": \"william\", \"amount\": 1131.33}, {\"owner\": \"kim\", \"amount\": 3347.58}, {\"owner\": \"john\", \"amount\": 306.38}, {\"owner\": \"richard\", \"amount\": 866.95}, {\"owner\": \"lynn\", \"amount\": 654.77}, {\"owner\": \"thomas\", \"amount\": 3302.94}, {\"owner\": \"mary\", \"amount\": 678.18}, {\"owner\": \"william\", \"amount\": 1708.06}], \"description\": \"Across radio career realize. Follow cultural anything race. Often add by long. Technology everything year concern sense foot white.\", \"photos\": [], \"location\": {\"latitude\": -5.322559707342336, \"longitude\": -127.69139614611947, \"altitude\": 0.0}, \"chosenBidder\": \"richard\"}}]}}"
        val taskListType: Type = object : TypeToken<List<Task>>() {}.type
        //TODO: Add tasks object
        val tasksFromJson = gson.fromJson<List<Task>>(serverJson, taskListType)
        println(tasksFromJson)
//        assertEquals(userList, usersFromJson)
    }

}