package ca.ualberta.taskr.models.elasticsearch

import ca.ualberta.taskr.models.Task
import ca.ualberta.taskr.models.User
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
 */
class AdapterFactory : TypeAdapterFactory {

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

class ResourceDoesNotExistException : Exception()
