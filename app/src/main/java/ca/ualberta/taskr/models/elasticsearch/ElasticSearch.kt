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

interface ElasticSearch {
    @GET("_cluster/health")
    fun getServerInfo(): Call<ServerInfo>

    @GET("cmput301w18t02/user/_search?q=*:*&filter_path=hits.hits.*,aggregations.*&size=99999")
    fun getUsers(): Call<List<User>>

    @GET("cmput301w18t02/task/_search?q=*:*&filter_path=hits.hits.*,aggregations.*&size=99999")
    fun getTasks(): Call<List<Task>>

    @GET()
    fun getUserFromUsername()

}