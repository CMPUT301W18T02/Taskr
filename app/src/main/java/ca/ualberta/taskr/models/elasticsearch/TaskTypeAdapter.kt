package ca.ualberta.taskr.models.elasticsearch

import ca.ualberta.taskr.models.Task
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
 *
 *  Task Type Adapter. Takes the Task type and produces a sub class compatible with the
 *  TaskType class
 */
class TaskTypeAdapter : TypeAdapter<List<Task>>() {
    private val gson = Gson()
    private val delegate = gson.getAdapter(object : TypeToken<List<Task>>(){})
    private val elementAdapter = gson.getAdapter(JsonElement::class.java)

    override fun write(out: JsonWriter?, value: List<Task>?) {
        delegate.write(out, value)

    }

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

