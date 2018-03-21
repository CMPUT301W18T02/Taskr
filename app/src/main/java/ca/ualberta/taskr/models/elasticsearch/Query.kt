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
         * Query for getting one task
         */
        @JvmStatic
        fun taskQuery(owner: String, title: String, description: String): RequestBody {
            return RequestBody.create(MediaType.parse("text/plain"), "{\"_source\": false,\"query\": {\"bool\": {\"must\": [{\"match\" : {\"owner\": \"$owner\"}},{\"match\" : {\"title\": \"$title\"}},{\"match\" : {\"description\": \"$description\"}}]}}}")
        }

        /**
         * Query for getting one user
         */
        @JvmStatic
        fun userQuery(username: String): RequestBody {
            return RequestBody.create(MediaType.parse("text/plain"), "{\"_source\": false,\"query\": {\"bool\": {\"must\": [{\"match\" : {\"username\": \"$username\"}}]}}}")
        }

        /**
         * Query for getting tasks owned by a user
         */
        @JvmStatic
        fun userOwnedTasksQuery(username: String): RequestBody {
            return RequestBody.create(MediaType.parse("text/plain"), "{\"query\": {\"match\" : {\"owner\": \"$username\"}}}")
        }

        /**
         * Query for getting tasks bidded on by a user
         */
        @JvmStatic
        fun userBiddedTasksQuery(username: String): RequestBody {
            return RequestBody.create(MediaType.parse("text/plain"), "{\"query\": {\"match\" : {\"bids.owner\": \"$username\"}}}")
        }

        /**
         * Query for getting tasks won on by a user
         */
        @JvmStatic
        fun userWonTasksQuery(username: String): RequestBody {
            return RequestBody.create(MediaType.parse("text/plain"), "{\"query\": {\"match\" : {\"bids.chosenBidder\": \"$username\"}}}")
        }


    }
}