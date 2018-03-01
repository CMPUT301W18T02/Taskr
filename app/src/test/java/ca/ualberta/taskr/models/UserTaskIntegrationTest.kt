package ca.ualberta.taskr.models

import com.ibm.icu.text.ArabicShaping
import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*

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
    private val controller = UserTaskController(HashMap<User, ArrayList<Task>>())

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }

    @Test
    fun uploadChanges() {
    }

    @Test
    fun downloadChanges() {
    }

    @Test
    fun checkDataBaseConnectivity() {

        print(controller.checkDataBaseConnectivity())
    }

}