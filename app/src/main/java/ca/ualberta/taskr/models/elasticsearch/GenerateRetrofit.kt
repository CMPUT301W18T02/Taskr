package ca.ualberta.taskr.models.elasticsearch

import ca.ualberta.taskr.models.Task
import ca.ualberta.taskr.models.User
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor



/**
 *  3/5/2018
 *
 *  Copyright (c) 2018 Brendan Samek. All Rights Reserved.
 *
 *  Generates the retrofit profiling that is required
 */
class GenerateRetrofit {
    companion object {

        var CMPUT301URL = "http://cmput301.softwareprocess.es:8080";

        /**
         * Generate JSON using googles GSON library
         */
        @JvmStatic
        fun generateGson(): Gson {

            val taskListType: Type = object : TypeToken<List<Task>>() {}.type
            val userListType: Type = object : TypeToken<List<User>>() {}.type

            return GsonBuilder()
                    .registerTypeAdapterFactory(UserTypeAdapterFactory())
                    .registerTypeAdapter(taskListType, TaskTypeAdapter())
                    .registerTypeAdapter(userListType, UserTypeAdapter())
                    .serializeNulls()
                    .create()
        }

        /**
         * Generate a Retrofit compatible profile from the elasticsearch information
         */
        @JvmStatic
        fun generateRetrofit(): ElasticSearch {
            val baseUrl = CMPUT301URL;

            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder()
                    .addInterceptor(interceptor).build()

            //TODO https://stackoverflow.com/questions/43455825/retrofit-2-gson-and-custom-deserializer
            val retrofit = Retrofit
                    .Builder()
                    .client(client)
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(generateGson()))
                    .build()

            return retrofit.create(ElasticSearch::class.java)
        }

    }
}