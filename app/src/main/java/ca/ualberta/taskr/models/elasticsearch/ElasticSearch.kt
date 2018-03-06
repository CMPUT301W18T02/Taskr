package ca.ualberta.taskr.models

import retrofit2.Call
import retrofit2.http.GET

/**
 *  ${FILE_NAME}
 *
 *  3/1/2018
 *
 *  Copyright (c) 2018 Brendan Samek. All Rights Reserved.
 */

interface ElasticSearch {
    @GET(".")
    fun getServerInfo(): Call<ServerInfo>

    @GET("cmput301w18t02/user")
    fun getUsers(): Call<List<User>>

    @GET("cmput301w18t02/tasks")
    fun getTasks(): Call<List<Task>>
}