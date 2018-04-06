package ca.ualberta.taskr

import android.util.Log
import android.widget.Button
import android.widget.EditText
import ca.ualberta.taskr.controllers.UserController
import ca.ualberta.taskr.models.elasticsearch.ElasticsearchID
import ca.ualberta.taskr.models.elasticsearch.GenerateRetrofit
import ca.ualberta.taskr.models.elasticsearch.Query
import com.mapbox.mapboxsdk.geometry.LatLng
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Robolectric
import org.robolectric.annotation.Config
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.Intent
import android.os.Bundle
import ca.ualberta.taskr.models.Task
import ca.ualberta.taskr.models.TaskStatus
import org.junit.*
import org.robolectric.shadows.ShadowLog


/**
 * Created by mrnic on 2018-03-24.
 *
 * This test class deals with all of the tests that involve the task basic requirements.
 */
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(26))
class TaskBasicsTest {

    private val taskTitle = "Test Title for a Task"
    private val taskDescr = "This is a description for a task. I am describing a task."
    private val taskLatLng = LatLng(80.0, 80.0)
    private val taskLocStr: String = taskLatLng.toString()

    private val username = "TestUsername"
    private lateinit var editTaskActivity: EditTaskActivity

    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var postTaskBtn: Button

    /**
     * Sets up everything that is required for the TestClass to function properly.
     */

    @Before
    fun setup() {
        editTaskActivity = Robolectric.setupActivity(EditTaskActivity::class.java)

        //set username
        UserController(editTaskActivity).setLocalUsername(username)

        ShadowLog.stream = System.out

        //Setup views
        titleEditText = editTaskActivity.findViewById<EditText>(R.id.taskTitleEditText)
        descriptionEditText = editTaskActivity.findViewById<EditText>(R.id.detailsEditText)
        addressEditText = editTaskActivity.findViewById<EditText>(R.id.locationEditText)
        postTaskBtn = editTaskActivity.findViewById<Button>(R.id.postTaskButton)

        //delete test task in elastic search
        deleteTestTask()
    }

    /**
     * Test task deletion
     */
    private fun deleteTestTask(){
        //delete test task in elastic search
        GenerateRetrofit.generateRetrofit().getTaskID(Query.taskQuery(username, taskTitle, taskDescr))
                .enqueue(object : Callback<ElasticsearchID> {
            override fun onResponse(call: Call<ElasticsearchID>, response: Response<ElasticsearchID>) {
                Log.i("network", response.body().toString())
                val id = response.body() as ElasticsearchID
                GenerateRetrofit.generateRetrofit().deleteTask(id.toString())
            }

            override fun onFailure(call: Call<ElasticsearchID>, t: Throwable) {
                Log.e("network", "Network Failed!")
                t.printStackTrace()
                return
            }
        })
    }

    /**
     * Check edit task activity to make sure its not null
     */
    @Test
    fun checkActivityNotNull() {
        Assert.assertNotNull(editTaskActivity)
    }

    /**
     * Test adding a task
     */
    @Test
    fun addATask() {
        //populate text fields, push add task button, check server for posted task, compare to expected
        titleEditText.setText(taskTitle)
        descriptionEditText.setText(taskDescr)
        addressEditText.setText(taskLocStr)

        postTaskBtn.performClick()

        GenerateRetrofit.generateRetrofit().getTasks().enqueue(object : Callback<List<Task>> {
            override fun onResponse(call: Call<List<Task>>, response: Response<List<Task>>) {
                var masterTaskList: ArrayList<Task> = ArrayList()
                masterTaskList.addAll(response.body() as ArrayList<Task>)
                val taskList: ArrayList<Task> = masterTaskList.filter {
                    it -> ((it.title == taskTitle)
                        && (it.description == taskDescr)
                        && (it.location.toString() == taskLocStr))
                } as ArrayList<Task>
                if(taskList.size == 0){
                    Log.d("Add Task Test", taskList.toString())
                }
                else {
                    val task = taskList[0]
                    Assert.assertEquals(taskTitle, task.title)
                    Assert.assertEquals(taskDescr, task.description)
                    Assert.assertEquals(taskLocStr, task.location.toString())
                }
            }

            override fun onFailure(call: Call<List<Task>>, t: Throwable) {
                Log.e("network", "Network Failed!")
                t.printStackTrace()
                Assert.assertEquals(1, 2)
            }
        })
        deleteTestTask()
    }

    /**
     * Test to see if the title length is within bounds
     */

    @Test
    fun maxLengthOfTaskTitle(){
        //Add a task with large title, check if stored title is less than or equal to 30
        titleEditText.setText(taskTitle + "Extra characters so that the test will show restriction on title.")
        descriptionEditText.setText(taskDescr)
        addressEditText.setText(taskLocStr)

        postTaskBtn.performClick()

        GenerateRetrofit.generateRetrofit().getTasks().enqueue(object : Callback<List<Task>> {
            override fun onResponse(call: Call<List<Task>>, response: Response<List<Task>>) {
                var masterTaskList: ArrayList<Task> = ArrayList()
                masterTaskList.addAll(response.body() as ArrayList<Task>)
                val taskList: ArrayList<Task> = masterTaskList.filter {
                    it -> (it.description == taskDescr)
                        && (it.location.toString() == taskLocStr)
                } as ArrayList<Task>
                if(taskList.size == 0){
                    Log.d("Max Title Length Test", taskList.toString())
                }
                else {
                    val task = taskList[0]
                    Log.d("Max Title Length Test", "Task Title: " + task.title)
                    Assert.assertTrue(task.title.length <= 30)
                }
            }

            override fun onFailure(call: Call<List<Task>>, t: Throwable) {
                Log.e("network", "Network Failed!")
                t.printStackTrace()
                Assert.assertEquals(1, 2)
            }
        })
        deleteTestTask()
    }

    /**
     * Test task description bounds
     */
    @Test
    fun maxLengthOfTaskDesc(){
        //Add a task with large description, check if stored title is less than or equal to 30
        titleEditText.setText(taskTitle)
        descriptionEditText.setText(taskDescr + "Extra characters so that the test will show" +
                                                "restriction on description. This one has to be" +
                "particularly long, as I have to go over the 300 character limit, which, when your" +
                "writing a test, is pretty doggone long. Whelp. Just a few characters le")
        addressEditText.setText(taskLocStr)

        postTaskBtn.performClick()

        Thread.sleep(1000)

        GenerateRetrofit.generateRetrofit().getTasks().enqueue(object : Callback<List<Task>> {
            override fun onResponse(call: Call<List<Task>>, response: Response<List<Task>>) {
                var masterTaskList: ArrayList<Task> = ArrayList()
                masterTaskList.addAll(response.body() as ArrayList<Task>)
                val taskList: ArrayList<Task> = masterTaskList.filter {
                    it -> (it.title == taskTitle)
                        && (it.location.toString() == taskLocStr)
                } as ArrayList<Task>

                if(taskList.size == 0) {
                    Log.d("Max Description Length Test", taskList.toString())
                }
                else {
                    val task = taskList[0]
                    Log.d("Max Description Length Test", "Task Description: " + task.description)
                    Assert.assertTrue(task.description.length <= 300)
                }
            }

            override fun onFailure(call: Call<List<Task>>, t: Throwable) {
                Log.e("network", "Network Failed!")
                t.printStackTrace()
                Assert.assertEquals(1, 2)
            }
        })
        deleteTestTask()
    }

    /**
     * Test viewing a list of a users tasks
     */
    @Test
    fun viewListOfMyTasks(){
        //populate a task, post the task, check if its associated with this user
        titleEditText.setText(taskTitle)
        descriptionEditText.setText(taskDescr)
        addressEditText.setText(taskLocStr)

        postTaskBtn.performClick()

        GenerateRetrofit.generateRetrofit().getTasks().enqueue(object : Callback<List<Task>> {
            override fun onResponse(call: Call<List<Task>>, response: Response<List<Task>>) {
                var masterTaskList: ArrayList<Task> = ArrayList()
                masterTaskList.addAll(response.body() as ArrayList<Task>)
                val taskList: ArrayList<Task> = masterTaskList.filter {
                    it -> (it.title == taskTitle)
                        && (it.location.toString() == taskLocStr)
                } as ArrayList<Task>

                if(taskList.size == 0) {
                    Log.d("Max Description Length Test", taskList.toString())
                }
                else {
                    val task = taskList[0]
                    Assert.assertTrue(task.owner == username)
                }
            }

            override fun onFailure(call: Call<List<Task>>, t: Throwable) {
                Log.e("network", "Network Failed!")
                t.printStackTrace()
                Assert.assertEquals(1, 2)
            }


    })
    }

    /**
     * Test editing the description
     */
    @Test
    fun editDescription(){
        //Create a task, pass it to the EditTaskActivity, edit it, see if its been edited correctly

        //create task
        val task: Task = Task(owner = username, title = taskTitle,
                status = TaskStatus.REQUESTED, bids = ArrayList(),
                description = taskDescr, photos = ArrayList(),
                location = taskLatLng, chosenBidder = "")

        var editTaskIntent = Intent(editTaskActivity, EditTaskActivity::class.java)
        var editTaskBundle = Bundle()
        var strTask = GenerateRetrofit.generateGson().toJson(task)
        editTaskBundle.putString("Task", strTask)
        editTaskIntent.putExtras(editTaskBundle)

        val taskPassedInActivity = Robolectric.buildActivity(EditTaskActivity::class.java!!, editTaskIntent).create().get()

        //Setup views
        val newDescriptionEditText = taskPassedInActivity.findViewById<EditText>(R.id.detailsEditText)
        val newPostTaskBtn = editTaskActivity.findViewById<Button>(R.id.postTaskButton)

        // Change the description of the task and post it
        val newTaskDescr = "NewDescription.txt"
        newDescriptionEditText.setText(newTaskDescr)
        newPostTaskBtn.performClick()

        Thread.sleep(1000)

        // Check if task is in Elastic Search
        GenerateRetrofit.generateRetrofit().getTasks().enqueue(object : Callback<List<Task>> {
            override fun onResponse(call: Call<List<Task>>, response: Response<List<Task>>) {
                var masterTaskList: ArrayList<Task> = ArrayList()
                masterTaskList.addAll(response.body() as ArrayList<Task>)
                val taskList: ArrayList<Task> = masterTaskList.filter {
                    it -> ((it.description == newTaskDescr)
                        && (it.title == taskTitle)
                        && (it.location.toString() == taskLocStr))
                } as ArrayList<Task>

                if(taskList.size == 0) {
                    Log.d("Change Task Test", taskList.toString())
                } else {

                    val newTask = taskList[0]
                    Assert.assertEquals(newTaskDescr, newTask.description)
                }

                // Delete test task in elastic search
                GenerateRetrofit.generateRetrofit().getTaskID(Query.taskQuery(username, taskTitle, newTaskDescr)).enqueue(object : Callback<ElasticsearchID> {
                    override fun onResponse(call: Call<ElasticsearchID>, response: Response<ElasticsearchID>) {
                        Log.i("network", response.body().toString())
                        val id = response.body() as ElasticsearchID
                        GenerateRetrofit.generateRetrofit().deleteTask(id.toString())
                    }

                    override fun onFailure(call: Call<ElasticsearchID>, t: Throwable) {
                        Log.e("network", "Network Failed!")
                        t.printStackTrace()
                        return
                    }
                })
            }

            override fun onFailure(call: Call<List<Task>>, t: Throwable) {
                Log.e("network", "Network Failed!")
                t.printStackTrace()
                Assert.assertEquals(1, 2)
            }
        })
        deleteTestTask()
    }

    /**
     * Test task deletion
     */

    @Test
    fun delTask(){ //TODO: Fix Erroring out "ca.ualberta.taskr.exceptions.ResourceDoesNotExistException"
        //populate text fields, push add task button, delete task, check server for deletion
        titleEditText.setText(taskTitle)
        descriptionEditText.setText(taskDescr)
        addressEditText.setText(taskLocStr)

        postTaskBtn.performClick()

        Thread.sleep(1000)
        //make sure task is in server
        GenerateRetrofit.generateRetrofit().getTasks().enqueue(object : Callback<List<Task>> {
            override fun onResponse(call: Call<List<Task>>, response: Response<List<Task>>) {
                var masterTaskList: ArrayList<Task> = ArrayList()
                masterTaskList.addAll(response.body() as ArrayList<Task>)
                val taskList: ArrayList<Task> = masterTaskList.filter {
                    it -> ((it.title == taskTitle)
                        && (it.description == taskDescr)
                        && (it.location.toString() == taskLocStr))
                } as ArrayList<Task>
                if(taskList.size == 0){
                    Log.d("Add Task Test", taskList.toString())
                }
                else {
                    val task = taskList[0]
                    Assert.assertEquals(taskTitle, task.title)
                    Assert.assertEquals(taskDescr, task.description)
                    Assert.assertEquals(taskLocStr, task.location.toString())
                }
            }

            override fun onFailure(call: Call<List<Task>>, t: Throwable) {
                Log.e("network", "Network Failed!")
                t.printStackTrace()
                Assert.assertEquals(1, 2)
            }
        })

        //delete the task
        deleteTestTask()

        //check if task is still in server
        GenerateRetrofit.generateRetrofit().getTasks().enqueue(object : Callback<List<Task>> {
            override fun onResponse(call: Call<List<Task>>, response: Response<List<Task>>) {
                var masterTaskList: ArrayList<Task> = ArrayList()
                masterTaskList.addAll(response.body() as ArrayList<Task>)
                val taskList: ArrayList<Task> = masterTaskList.filter {
                    it -> ((it.title == taskTitle)
                        && (it.description == taskDescr)
                        && (it.location.toString() == taskLocStr))
                } as ArrayList<Task>

                Assert.assertTrue(taskList.size == 0)
            }

            override fun onFailure(call: Call<List<Task>>, t: Throwable) {
                Log.e("network", "Network Failed!")
                t.printStackTrace()
                Assert.assertEquals(1, 2)
            }
        })

    }
}