package ca.ualberta.taskr.models.elasticsearch

import ca.ualberta.taskr.models.Task
import ca.ualberta.taskr.models.User
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

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
     * Returns user's elasticsearch id from the server based on a user query body
     */
    @POST("cmput301w18t02/user/_search?filter_path=hits.hits._id,aggregations.*")
    fun getUserID(@Body userQueryBody: RequestBody): Call<ElasticsearchID>


    /**
     * Returns task's elasticsearch id from the server based on a task query body
     */
    @POST("cmput301w18t02/task/_search?filter_path=hits.hits._id,aggregations.*")
    fun getTaskID(@Body taskQueryBody: RequestBody): Call<ElasticsearchID>


    /**
     * Update a task using its id and a new task
     */
    @PUT("cmput301w18t02/task/{id}")
    fun updateTask(@Path("id") id: String, @Body task: Task)

    /**
     * Update a user using its id and a new user
     */
    @PUT("cmput301w18t02/user/{id}")
    fun updateUser(@Path("id") id: String, @Body user: User)

    /**
     * Add a new task
     */
    @POST("cmput301w18t02/task")
    fun createTask(@Body task: Task)

    /**
     * Add a new user
     */
    @POST("cmput301w18t02/user")
    fun createUser(@Body user: User)


    // TODO: Implement deletion methods for various types
    // TODO: Implement searching implementations so searches do not occur locally

}