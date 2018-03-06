//package ca.ualberta.taskr.models.elasticsearch
//
//import ca.ualberta.taskr.models.Task
//import ca.ualberta.taskr.models.User
//import com.google.gson.*
//import com.google.gson.reflect.TypeToken
//import com.google.gson.stream.JsonReader
//import com.google.gson.stream.JsonWriter
//import java.io.IOException
//import java.lang.reflect.Type
//import com.google.gson.JsonElement
//import java.io.IOError
//
//
///**
// *  3/6/2018
// *
// *  Copyright (c) 2018 Brendan Samek. All Rights Reserved.
// */
//class UserTypeAdapterFactory : TypeAdapterFactory {
//    override fun <T : Any?> create(gson: Gson?, type: TypeToken<T>?): TypeAdapter<T> {
//        if (gson == null) {
//            throw IOException()
//        }
//        val delegate = gson.getDelegateAdapter(this, type)
//        val elementAdapter = gson.getAdapter(JsonElement::class.java)
//        val userAdapter = gson.getAdapter(User::class.java)
//        val taskAdapter = gson.getAdapter(Task::class.java)
//        val typeT: Type = object : TypeToken<T>() {}.type
//        val userListType: Type = object : TypeToken<List<User>>() {}.type
//        val taskListType: Type = object : TypeToken<List<Task>>() {}.type
//
//
//        return object : TypeAdapter<T>() {
//            @Throws(IOException::class)
//            override fun write(out: JsonWriter, value: T) {
//                delegate?.write(out, value)
//            }
//
//            @Throws(IOException::class)
//            override fun read(`in`: JsonReader): T {
//                val jsonElement = elementAdapter.read(`in`)
//                val jsonObject = jsonElement.asJsonObject
//
//
//
//                return when (typeT) {
//                    userListType -> {
//
//                        val users:Object =
//                        val hits = jsonObject.getAsJsonObject("hits").getAsJsonArray("hits")
//
//                        for (hit in hits) {
//                            users.add(userAdapter.fromJson(hit.asJsonObject.getAsJsonObject("_source").asString))
//                        }
//                        delegate.fromJsonTree(jsonElement)
//                    }
//                    taskListType -> {
//                        delegate.fromJsonTree(jsonElement)
//                    }
//                    else -> {
//                        delegate.fromJsonTree(jsonElement)
//                    }
//                }
//
//            }
//            }.nullSafe()
//        }
//    }