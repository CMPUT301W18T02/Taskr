package ca.ualberta.taskr.models.elasticsearch

import ca.ualberta.taskr.models.Task
import ca.ualberta.taskr.models.User
import retrofit2.Call
import retrofit2.http.GET

/**
 *  ${FILE_NAME}
 *
 *  3/1/2018
 *
 *  Copyright (c) 2018 Brendan Samek. All Rights Reserved.
 */

/**
 * ElasticSearch Interface
 */
interface ElasticSearch {
    /**
     * Produce server information for the application. E.g whether the server is connected
     */
    @GET("_cluster/health")
    fun getServerInfo(): Call<ServerInfo>

    /**
     * Return a list of all users connected to the server
     */
    @GET("cmput301w18t02/user/_search?q=*:*&filter_path=hits.hits.*,aggregations.*&size=99999")
    fun getUsers(): Call<List<User>>

    /**
     * Return a list of all tasks located on the server
     */
    @GET("cmput301w18t02/task/_search?q=*:*&filter_path=hits.hits.*,aggregations.*&size=99999")
    fun getTasks(): Call<List<Task>>

    /**
     * Returns a User from the server based on a given username
     */
    @GET()
    fun getUserFromUsername()


}