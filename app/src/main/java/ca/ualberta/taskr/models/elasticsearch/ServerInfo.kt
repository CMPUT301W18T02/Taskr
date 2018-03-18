package ca.ualberta.taskr.models.elasticsearch

/**
 *  Serverinfo.kt
 *
 *  3/1/2018
 *
 *  Copyright (c) 2018 Brendan Samek. All Rights Reserved.
 */

/**
 * ServerInfo subclass. Contains various information relating to connection status
 */
data class ServerInfo(val cluster_name: String, val status: String, val timed_out: Boolean)