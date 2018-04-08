package ca.ualberta.taskr.controllers

import android.content.Context
import android.content.Intent
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.widget.ImageView
import android.widget.TextView
import ca.ualberta.taskr.*
import ca.ualberta.taskr.util.PhotoConversion

/**
 * NavViewController class. This Controller is responsible
 * for powering the Hamburger menu that appears on the left
 * hand side of the screen
 *
 * @constructor the constructor sets the controller to be minimized off to the
 * side by default
 */
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
                    else if(menuItem.itemId == R.id.nav_MyBids){
                        val myBidsIntent = Intent(context,
                                MyBidsActivity::class.java)
                        context.startActivity(myBidsIntent)
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
        if(userController.getLocalUserObject()?.profilePicture != null ) {
            var user = userController.getLocalUserObject()
            var imageStr = user?.profilePicture
            if (imageStr!!.isNotEmpty()) {
                userPhoto.setImageBitmap(PhotoConversion.getBitmapFromString(userController.getLocalUserObject()!!.profilePicture!!))
            }
        }
        userPhoto.setOnClickListener({
            val editUserIntent = Intent(context,
                    EditUserActivity::class.java)
            context.startActivity(editUserIntent)
        })
    }

    /**
     * Function callback for when the user chooses to logout
     * @param userController the [UserController] instance
     */
    fun logout(userController: UserController){
        userController.setLocalUsername("")
        userController.setLocalUserObject(null)
        val loginScreenIntent = Intent(context,
                LoginActivity::class.java)
        context.startActivity(loginScreenIntent)

    }
}