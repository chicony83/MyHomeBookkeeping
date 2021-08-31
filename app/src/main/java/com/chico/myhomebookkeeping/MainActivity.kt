package com.chico.myhomebookkeeping

import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import com.chico.myhomebookkeeping.checks.GetSP
import com.chico.myhomebookkeeping.constants.Constants
import com.chico.myhomebookkeeping.helpers.SetSP
import com.chico.myhomebookkeeping.helpers.UiHelper
import com.chico.myhomebookkeeping.utils.launchUi

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private val uiHelper = UiHelper()
    private val spName = Constants.SP_NAME
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var getSP: GetSP
    private lateinit var setSP: SetSP

    //        getSharedPreferences(spName, MODE_PRIVATE)
    private lateinit var spEditor: SharedPreferences.Editor

    //    private val argsIdMoneyMovingForChange = Constants.SP_ID_MONEY_MOVING_FOR_CHANGE
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences(spName, MODE_PRIVATE)
        getSP = GetSP(sharedPreferences)
        spEditor = sharedPreferences.edit()
        setSP = SetSP(spEditor)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_money_moving_query,
                R.id.nav_money_moving,
//                R.id.nav_reports,
                R.id.nav_categories,
                R.id.nav_currencies,
                R.id.nav_cash_account,
                R.id.nav_change_money_moving,
                R.id.nav_setting,
                R.id.nav_time_period
            ),
            drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        fabSetOnClickListener(fab, navController)

        hideFab(navController, fab)
        hideToolbar(toolbar)
    }

    private fun hideToolbar(toolbar: Toolbar) {
        launchUi {
            navController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.nav_help_fragment -> toolbar.visibility = View.GONE
                    else -> toolbar.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun hideFab(
        navController: NavController,
        fab: FloatingActionButton
    ) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.nav_change_money_moving -> uiHelper.hideUiElement(fab)
                R.id.nav_currencies -> uiHelper.hideUiElement(fab)
                R.id.nav_categories -> uiHelper.hideUiElement(fab)
                R.id.nav_cash_account -> uiHelper.hideUiElement(fab)
                R.id.nav_money_moving_query -> uiHelper.hideUiElement(fab)
                R.id.nav_setting -> uiHelper.hideUiElement(fab)
                R.id.nav_help_fragment -> uiHelper.hideUiElement(fab)
                R.id.nav_new_money_moving -> uiHelper.hideUiElement(fab)
                R.id.nav_first_launch_fragment -> uiHelper.hideUiElement(fab)
                R.id.nav_time_period -> uiHelper.hideUiElement(fab)
                else -> uiHelper.showUiElement(fab)
            }
        }
    }

    private fun fabSetOnClickListener(
        fab: FloatingActionButton,
        navController: NavController
    ) {
        fab.setOnClickListener {
            navController.navigate(R.id.nav_new_money_moving)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.help_button -> {
                navController.navigate(R.id.nav_help_fragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (navController.currentDestination?.id == R.id.nav_money_moving) {
            finish()
        } else {
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
