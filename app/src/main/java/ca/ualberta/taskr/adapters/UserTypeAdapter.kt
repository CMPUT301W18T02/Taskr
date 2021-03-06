package ca.ualberta.taskr.adapters

import ca.ualberta.taskr.models.User
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

/**
 *  3/6/2018
 *
 *  Copyright (c) 2018 Brendan Samek. All Rights Reserved.
 *  User Type Adapter. Takes the User type and produces a sub class compatible with the
 *  TaskType class
 */

/**
 * UserTypeAdapter
 *
 * Allows for the serializtion of Lists of users using GSON and JSON
 */
class UserTypeAdapter : TypeAdapter<List<User>>() {
    private val gson = Gson()
    private val delegate = gson.getAdapter(object : TypeToken<List<User>>(){})
    private val elementAdapter = gson.getAdapter(JsonElement::class.java)

    /**
     * Write the [List] of [User] objects in JSON format
     * @param out a [JsonWriter] instance
     * @param value a [List] of [User] objects to write in JSON format
     *
     * @see [JsonWriter]
     */
    override fun write(out: JsonWriter?, value: List<User>?) {
        delegate.write(out, value)

    }

    /**
     * Read the [List] of [User] objects in JSON format
     * @param in a [JsonReader] instance
     * @param value a [List] of [User] objects to write in JSON format
     *
     * @see [JsonReader]
     */
    override fun read(`in`: JsonReader?): List<User> {
        val jsonElement = elementAdapter.read(`in`)
        val jsonObject = jsonElement.asJsonObject
        val users = ArrayList<User>()

        val hits = jsonObject.getAsJsonObject("hits").getAsJsonArray("hits")

        for (hit in hits) {
            users.add(gson.fromJson(hit.asJsonObject.getAsJsonObject("_source"),object : TypeToken<User>(){}.type))
        }
        return users
    }
}

