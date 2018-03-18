package ca.ualberta.taskr.models.elasticsearch

import okhttp3.MediaType
import okhttp3.RequestBody

/**
 *  3/8/2018
 *
 *  Copyright (c) 2018 Brendan Samek. All Rights Reserved.
 */
class Query {
    companion object {
        @JvmStatic
        fun taskQuery(owner: String, title: String, description: String): RequestBody {
            return RequestBody.create(MediaType.parse("text/plain"),"{\"_source\": false,\"query\": {\"bool\": {\"should\": [{\"match\" : {\"owner\": \"$owner\"}},{\"match\" : {\"title\": \"$title\"}},{\"match\" : {\"description\": \"$description\"}}]}}}")
        }

        @JvmStatic
        fun userQuery(username: String): RequestBody {
            return RequestBody.create(MediaType.parse("text/plain"),"{\"_source\": false,\"query\": {\"bool\": {\"should\": [{\"match\" : {\"username\": \"$username\"}}]}}}")
        }

        @JvmStatic
        fun userOwnedTasksQuery(username: String): RequestBody {
            return RequestBody.create(MediaType.parse("text/plain"),"{\"query\": {\"match\" : {\"owner\": \"$username\"}}}")
        }

        @JvmStatic
        fun userBiddedTasksQuery(username: String): RequestBody {
            return RequestBody.create(MediaType.parse("text/plain"),"{\"query\": {\"match\" : {\"bids.owner\": \"$username\"}}}")
        }


    }
}