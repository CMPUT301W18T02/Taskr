package ca.ualberta.taskr.models

import android.media.Image
import ca.ualberta.taskr.models.elasticsearch.GenerateRetrofit
import ca.ualberta.taskr.models.elasticsearch.Query
import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.Ignore

/**
 * ${FILE_NAME}
 *
 *
 * 2/28/2018
 *
 *
 * Copyright (c) 2018 Brendan Samek. All Rights Reserved.
 */
class UserTaskIntegrationTest {
    private val controller = UserTaskController(HashMap())
    private val elasticSearch = GenerateRetrofit.generateRetrofit()
    private var name = "John"
    private var email = "jsmith@ualberta.ca"
    private var phoneNumber = "1234567890"
    private var username = "jsmith"
    private var image: Image? = null

    @Ignore
    @Test
    fun uploadChanges() {
        controller.addUser(User(name, phoneNumber, image, email, username))
        controller.uploadChanges()
    }

    @Test
    fun downloadChanges() {
        controller.downloadChanges()
    }

    @Test
    fun getUserID() {
        println(elasticSearch.getUserID(Query.userQuery("ryan")).execute().body())
    }
    @Test
    fun checkDataBaseConnectivity() {
        println("Is the database available: " + controller.checkDataBaseConnectivity())
    }

}