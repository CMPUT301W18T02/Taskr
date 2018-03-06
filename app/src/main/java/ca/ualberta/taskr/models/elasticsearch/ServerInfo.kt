package ca.ualberta.taskr.models

/**
 *  ${FILE_NAME}
 *
 *  3/1/2018
 *
 *  Copyright (c) 2018 Brendan Samek. All Rights Reserved.
 */

data class ServerInfo(val clusterName: String, val status: String, val timedOut: Boolean)