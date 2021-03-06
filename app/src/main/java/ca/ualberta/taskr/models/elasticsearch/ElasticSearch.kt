package ca.ualberta.taskr.models.elasticsearch

import ca.ualberta.taskr.models.Task
import ca.ualberta.taskr.models.User
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

/**
 *  3/1/2018
 *
 *  Copyright (c) 2018 Brendan Samek. All Rights Reserved.
 */

/**
 * ElasticSearch Interface. This interface defines the specific search queries to be performed
 */
interface ElasticSearch {

    /**
     * Produce server information for the application. E.g whether the server is connected
     * @return a [ServerInfo] instance
     */
    @GET("_cluster/health")
    fun getServerInfo(): Call<ServerInfo>

    /**
     * Return a list of all users connected to the server
     * @return a [List] of [User] object instances
     */
    @GET("cmput301w18t02/user/_search?q=*:*&filter_path=hits.hits.*,aggregations.*&size=99999")
    fun getUsers(): Call<List<User>>


    /**
     * Return a list of all tasks located on the server
     * @return a [List] of [Task] object instances
     */
    @GET("cmput301w18t02/task/_search?q=*:*&filter_path=hits.hits.*,aggregations.*&size=99999")
    fun getTasks(): Call<List<Task>>

    /**
     * Returns user's elasticsearch id from the server based on a user query body
     * @return the [ElasticsearchID] of a specific [User] instance
     */
    @POST("cmput301w18t02/user/_search?filter_path=hits.hits._id,aggregations.*")
    fun getUserID(@Body userQueryBody: RequestBody): Call<ElasticsearchID>


    /**
     * Returns task's elasticsearch id from the server based on a task query body
     * @return the [ElasticsearchID] of a specific [Task] instance
     */
    @POST("cmput301w18t02/task/_search?filter_path=hits.hits._id,aggregations.*")
    fun getTaskID(@Body taskQueryBody: RequestBody): Call<ElasticsearchID>


    /**
     * Update a task using its id and a new task
     */
    @PUT("cmput301w18t02/task/{id}")
    fun updateTask(@Path("id") id: String, @Body task: Task) : Call<Void>

    /**
     * Update a user using its id and a new user
     */
    @PUT("cmput301w18t02/user/{id}")
    fun updateUser(@Path("id") id: String, @Body user: User) : Call<Void>

    /**
     * Add a new task
     */
    @POST("cmput301w18t02/task")
    fun createTask(@Body task: Task) : Call<Void>

    /**
     * Add a new user
     */
    @POST("cmput301w18t02/user")
    fun createUser(@Body user: User) : Call<Void>

    /**
     * Returns user's owned task
     * @return a [List] of [Task] object instances belonging to a specific [User]
     */
    @POST("cmput301w18t02/task/_search?filter_path=hits.hits.*,aggregations.*&size=99999")
    fun getUserTasks(@Body userTasksQueryBody: RequestBody): Call<List<Task>>


    /**
     * Returns task's elasticsearch id from the server based on a task query body
     * @return a [List] of [Task] object instances that a [User] has bid on
     */
    @POST("cmput301w18t02/task/_search?filter_path=hits.hits.*,aggregations.*&size=99999")
    fun getUserBids(@Body userBidsQuery: RequestBody): Call<ElasticsearchID>

    /**
     * Deletes a task from elasticsearch, DOES NOT CHECK DELETION CRITERIA
     */
    @DELETE("cmput301w18t02/task/{id}")
    fun deleteTask(@Path("id") taskID: String) : Call<Void>

    /**
     * Deletes a user from elasticsearch,
     */
    @DELETE("cmput301w18t02/user/{id}")
    fun deleteUser(@Path("id") userID: String) : Call<Void>

    /**
     * Returns user's owned task
     * @return a [List] of [Task] object instances that a [User] has won
     */
    @POST("cmput301w18t02/task/_search?filter_path=hits.hits.*,aggregations.*&size=99999")
    fun getWonTasks(@Body userTasksQueryBody: RequestBody): Call<List<Task>>

}