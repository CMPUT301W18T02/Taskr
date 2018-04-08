package ca.ualberta.taskr.models.elasticsearch

import okhttp3.MediaType
import okhttp3.RequestBody

/**
 *  3/8/2018
 *
 *  Copyright (c) 2018 Brendan Samek. All Rights Reserved.
 */

/**
 * Query class.
 * Allows for the querying of lists of objects, create RequestBodies for different elasticsearch queries.
 */
class Query {
    companion object {

        /**
         * Query for getting a [Task]
         * @param owner The owner of a [Task]
         * @param title The title of the [Task]
         * @param description The description of the [Task]
         * @return a [RequestBody] containing the information to request a [Task]
         */
        @JvmStatic
        fun taskQuery(owner: String, title: String, description: String): RequestBody {
            return RequestBody.create(MediaType.parse("text/plain"), "{\"_source\": false,\"query\": {\"bool\": {\"must\": [{\"match\" : {\"owner\": \"$owner\"}},{\"match\" : {\"title\": \"$title\"}},{\"match\" : {\"description\": \"$description\"}}]}}}")
        }

        /**
         * Query for getting a [User]
         * @param username the username
         * @return a [RequestBody] containing the information to request a [User]
         */
        @JvmStatic
        fun userQuery(username: String): RequestBody {
            return RequestBody.create(MediaType.parse("text/plain"), "{\"_source\": false,\"query\": {\"bool\": {\"must\": [{\"match\" : {\"username\": \"$username\"}}]}}}")
        }

        /**
         * Query for getting the list of tasks owned by a [User]
         * @param username the username
         * @return a [RequestBody] containing the information to request a [List] of [Task] objects
         * owned by a [User]
         */
        @JvmStatic
        fun userOwnedTasksQuery(username: String): RequestBody {
            return RequestBody.create(MediaType.parse("text/plain"), "{\"query\": {\"match\" : {\"owner\": \"$username\"}}}")
        }

        /**
         * Query for getting the list of tasks a user has bid on
         * @param username the username
         * @return a [RequestBody] containing the information to request the [List] of [Task]
         * objects a [User] has bid on
         */
        @JvmStatic
        fun userBiddedTasksQuery(username: String): RequestBody {
            return RequestBody.create(MediaType.parse("text/plain"), "{\"query\": {\"match\" : {\"bids.owner\": \"$username\"}}}")
        }

        /**
         * Query for getting the list of tasks a [User] has won
         * @param username the username
         * @return a [RequestBody] containing the information to request the [List] of [Task]
         * objects a [User] has won
         */
        @JvmStatic
        fun userWonTasksQuery(username: String): RequestBody {
            return RequestBody.create(MediaType.parse("text/plain"), "{\"query\": {\"match\" : {\"bids.chosenBidder\": \"$username\"}}}")
        }


    }
}