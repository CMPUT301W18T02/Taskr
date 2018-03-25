package ca.ualberta.taskr.controllers

import android.content.Context
import android.content.Intent
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.widget.ImageView
import android.widget.TextView
import ca.ualberta.taskr.*


class NavViewController(var navView: NavigationView,
                        var drawerLayout: DrawerLayout,
                        var context: Context){



    init {
        val userController = UserController(context)
        navView.setNavigationItemSelectedListener(
                { menuItem ->
                    menuItem.isChecked = true
                    drawerLayout.closeDrawers()

                    if(menuItem.itemId == R.id.nav_tasks){
                        val listTasksIntent = Intent(context,
                                ListTasksActivity::class.java)
                        context.startActivity(listTasksIntent)
                    }
                    else if(menuItem.itemId == R.id.nav_myTasks){
                        val myTasksIntent = Intent(context,
                                MyTasksActivity::class.java)
                        context.startActivity(myTasksIntent)
                    }
                    else if(menuItem.itemId == R.id.nav_todo){
                        val toDoTaskListIntent = Intent(context,
                                ToDoTaskListActivity::class.java)
                        context.startActivity(toDoTaskListIntent)
                    }
                    else if(menuItem.itemId == R.id.nav_logout){
                        logout(userController)
                    }

                    true
                })

        val headerView =  navView.getHeaderView(0)
        val usernameTextView = headerView.findViewById<TextView>(R.id.navHeaderUsername)
        usernameTextView.text = userController.getLocalUserName()
        val userPhoto = headerView.findViewById<ImageView>(R.id.profileImage)
        userPhoto.setOnClickListener({
            val editUserIntent = Intent(context,
                    EditUserActivity::class.java)
            context.startActivity(editUserIntent)
        })
    }

    fun logout(userController: UserController){
        userController.setLocalUsername("")
        userController.setLocalUserObject(null)
        val loginScreenIntent = Intent(context,
                LoginActivity::class.java)
        context.startActivity(loginScreenIntent)

    }
}