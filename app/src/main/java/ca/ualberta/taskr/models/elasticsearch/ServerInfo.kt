package ca.ualberta.taskr.models.elasticsearch

/**
 *  Serverinfo.kt
 *
 *  3/1/2018
 *
 *  Copyright (c) 2018 Brendan Samek. All Rights Reserved.
 */

/**
 * ServerInfo Class. Contains various information relating to connection status
 * @property cluster_name the name of the server cluster
 * @property status the status of the server cluster
 * @property timed_out whether the server has timed out or not
 */
data class ServerInfo(val cluster_name: String, val status: String, val timed_out: Boolean)