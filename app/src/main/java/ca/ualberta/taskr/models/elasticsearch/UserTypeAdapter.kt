package ca.ualberta.taskr.models.elasticsearch

import ca.ualberta.taskr.models.User
import com.google.gson.JsonElement
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException

/**
 *  3/6/2018
 *
 *  Copyright (c) 2018 Brendan Samek. All Rights Reserved.
 */
class UserTaskAdapter: TypeAdapter<List<User>>() {
    val delegate = gson.getDelegateAdapter(this, object : TypeToken<List<User>>() {}.type)
    val elementAdapter = gson.getAdapter(JsonElement::class.java)
    val userAdapter = gson.getAdapter(User::class.java)

    override fun write(out: JsonWriter?, value: List<User>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun read(`in`: JsonReader?): List<User> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
}
