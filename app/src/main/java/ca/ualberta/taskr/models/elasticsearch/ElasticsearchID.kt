package ca.ualberta.taskr.models.elasticsearch

/**
 * ElasticSearch ID container class. This class essentially an immutable [String] with a different name
 * @property _id the ID of the elasticsearch object represented as a [String]
 */
data class ElasticsearchID(val _id: String)