package ca.ualberta.taskr.models.elasticsearch

/**
 *  ${FILE_NAME}
 *
 *  3/1/2018
 *
 *  Copyright (c) 2018 Brendan Samek. All Rights Reserved.
 */

data class ServerInfo(val cluster_name: String, val status: String, val timed_out: Boolean)