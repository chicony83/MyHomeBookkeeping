package com.chico.myhomebookkeeping

import android.content.SharedPreferences
import android.graphics.Point
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.chico.myhomebookkeeping.checks.CheckNightMode
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.helpers.UiHelper
import com.chico.myhomebookkeeping.sp.GetSP
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.sp.SetSP
import com.chico.myhomebookkeeping.obj.Colors
import com.chico.myhomebookkeeping.obj.DayNightMode
import com.chico.myhomebookkeeping.sp.EraseSP
import com.chico.myhomebookkeeping.utils.launchUi
import kotlinx.coroutines.runBlocking
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private val checkNightMode = CheckNightMode()
    private lateinit var eraseSP: EraseSP

    private lateinit var spEditor: SharedPreferences.Editor

    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView
    private val uiHelper = UiHelper()
    lateinit var mainActivityViewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainActivityViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        uiMode()
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        bottomNavigationView = findViewById(R.id.bottom_navigation)

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

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.journal_money_moving -> {
                    navController.navigate(R.id.nav_money_moving)
                }
                R.id.add_money_moving -> {
                    navController.navigate(R.id.nav_new_money_moving)
                }
                R.id.reports -> {
                    navController.navigate(R.id.nav_reports)
                }
                R.id.nav_fast_payments_fragment -> {
                    navController.navigate(R.id.nav_fast_payments_fragment)
                }
            }
            true
        }

        hideToolbarAndBottomNavigation(toolbar)
//        eraseSP.eraseTempSP()

        if (mainActivityViewModel.checkIsFirstLaunch()) navController.navigate(R.id.nav_first_launch_select_currencies_fragment)
//        if (mainActivityViewModel.checkIsFirstLaunch()) navController.navigate(R.id.showAddCategoriesFragment)
    }

    private fun uiMode() {
        runBlocking {
            checkIsNightModeOn()
            uiColors()
        }
    }

    private fun checkIsNightModeOn() {
        DayNightMode.setIsNightMode(checkNightMode.isNightMode(context = applicationContext))
    }

    private fun uiColors() {
        Colors.setColors(resources)
    }

    private fun hideToolbarAndBottomNavigation(toolbar: Toolbar) {
        launchUi {
            navController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
//                    R.id.nav_fast_payments_fragment->{
//                        bottomNavigationView.selectedItemId = R.id.nav_fast_payments_fragment
//                    }
                    R.id.nav_first_launch_select_currencies_fragment->{
                        uiHelper.hideUiElement(toolbar)
                        uiHelper.hideUiElement(bottomNavigationView)
                    }
                    R.id.nav_help_fragment -> {
                        uiHelper.hideUiElement(toolbar)
                        uiHelper.hideUiElement(bottomNavigationView)
                    }
//                    R.id.showAddCategoriesFragment -> {
//                        uiHelper.hideUiElement(toolbar)
//                        uiHelper.hideUiElement(bottomNavigationView)
//                    }
//                    R.id.nav_new_money_moving->{
//                        uiHelper.hideUiElement(bottomNavigation)
//                    }
                    R.id.nav_time_period -> {
                        uiHelper.hideUiElement(bottomNavigationView)
                    }
                    else -> {
                        uiHelper.showUiElement(toolbar)
                        uiHelper.showUiElement(bottomNavigationView)
                    }
                }
            }
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