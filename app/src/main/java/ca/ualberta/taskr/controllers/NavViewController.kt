package ca.ualberta.taskr.controllers

import android.content.Context
import android.content.Intent
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.widget.ImageView
import android.widget.TextView
import ca.ualberta.taskr.EditUserActivity
import ca.ualberta.taskr.ListTasksActivity
import ca.ualberta.taskr.MyTasksActivity
import ca.ualberta.taskr.R


class NavViewController(var navView: NavigationView,
                        var drawerLayout: DrawerLayout,
                        var context: Context){



    init {
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

                    true
                })

        var userController : UserController = UserController(context)
        val headerView =  navView.getHeaderView(0)
        var usernameTextView = headerView.findViewById<TextView>(R.id.navHeaderUsername)
        usernameTextView.text = userController.getLocalUserName()
        var userPhoto = headerView.findViewById<ImageView>(R.id.profileImage)
        userPhoto.setOnClickListener({
            val editUserIntent = Intent(context,
                    EditUserActivity::class.java)
            context.startActivity(editUserIntent)
        })

    }
}