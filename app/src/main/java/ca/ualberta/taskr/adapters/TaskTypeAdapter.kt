package ca.ualberta.taskr.adapters

import ca.ualberta.taskr.models.Task
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
 *
 *  Task Type Adapter. Takes the Task type and produces a sub class compatible with the
 *  TaskType class
 */

/**
 * TaskTypeAdapter
 *
 * Allows for the serializtion of Lists of tasks using GSON and JSON
 */
class TaskTypeAdapter : TypeAdapter<List<Task>>() {
    private val gson = Gson()
    private val delegate = gson.getAdapter(object : TypeToken<List<Task>>(){})
    private val elementAdapter = gson.getAdapter(JsonElement::class.java)

    /**
     * Write the [List] of [Task] objects in JSON format
     * @param out a [JsonWriter] instance
     * @param value a [List] of [Task] objects to write in JSON format
     *
     * @see [JsonWriter]
     */
    override fun write(out: JsonWriter?, value: List<Task>?) {
        delegate.write(out, value)

    }

    /**
     * Read the [List] of tasks in JSON format
     * @param in a [JsonReader] instance
     * @return a [List] of [Task] objects
     *
     * @see [JsonReader]
     */
    override fun read(`in`: JsonReader?): List<Task> {
        val jsonElement = elementAdapter.read(`in`)
        val jsonObject = jsonElement.asJsonObject
        val hits = jsonObject.getAsJsonObject("hits").getAsJsonArray("hits")
        val tasks = ArrayList<Task>()

        for (hit in hits) {
            tasks.add(gson.fromJson(hit.asJsonObject.getAsJsonObject("_source"), object : TypeToken<Task>(){}.type))
        }
        return tasks
    }
}

