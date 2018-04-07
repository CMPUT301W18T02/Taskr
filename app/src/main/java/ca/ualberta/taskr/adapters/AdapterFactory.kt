package ca.ualberta.taskr.adapters

import ca.ualberta.taskr.exceptions.ResourceDoesNotExistException
import ca.ualberta.taskr.models.Task
import ca.ualberta.taskr.models.User
import ca.ualberta.taskr.models.elasticsearch.ElasticsearchID
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException
import com.google.gson.JsonElement


/**
 *  3/6/2018
 *
 *  Copyright (c) 2018 Brendan Samek. All Rights Reserved.
 *
 *  UserType AdapterFactory Class. Assembles Usertypes into a useable datatype for elasticsearch
 *  and retrofit
 *
 *  @constructor creates the framework for the AdapterFactory
 */
class AdapterFactory : TypeAdapterFactory {

    /**
     * Create the TypeAdapter the given Gson
     * @param T the type of object to produce an adapter for
     * @property gson the [Gson] instance of the object
     * @property type the type of the information contained within the gson object
     * @constructor non-existant
     * @see [TypeToken]
     * @see [Gson]
     * @see [TypeAdapter]
     * @throws [IOException]
     */
    override fun <T : Any?> create(gson: Gson?, type: TypeToken<T>?): TypeAdapter<T> {
        if (gson == null) {
            throw IOException()
        }
        val delegate = gson.getDelegateAdapter(this, type)
        val elementAdapter = gson.getAdapter(JsonElement::class.java)
        val finalConverter = Gson()
        val userListType = object : TypeToken<List<@JvmSuppressWildcards User>>() {}
        val taskListType = object : TypeToken<List<@JvmSuppressWildcards Task>>() {}
        val elasticsearchIDType = object : TypeToken<ElasticsearchID>() {}


        return object : TypeAdapter<T>() {
            @Throws(IOException::class)
            override fun write(out: JsonWriter, value: T) {
                delegate?.write(out, value)
            }

            @Throws(IOException::class)
            override fun read(`in`: JsonReader): T {
                val jsonElement = elementAdapter.read(`in`)
                val jsonObject = jsonElement.asJsonObject


                if (type == userListType || type == taskListType) {
                    val hits = jsonObject.getAsJsonObject("hits").getAsJsonArray("hits")
                    val source = JsonArray()
                    for (hit in hits) {
                        source.add(hit.asJsonObject.getAsJsonObject("_source"))
                    }
                    return finalConverter.fromJson(source,type.type)
                }
                else if (type == elasticsearchIDType) {
                    if (jsonObject.size() == 0) {
                        throw ResourceDoesNotExistException()
                    }
                    val hits = jsonObject.getAsJsonObject("hits").getAsJsonArray("hits")
                    return finalConverter.fromJson(hits[0].asJsonObject, type.type)
                }
                else {
                    return finalConverter.fromJson(jsonElement,type?.type)
                }

            }
        }.nullSafe()
    }
}
