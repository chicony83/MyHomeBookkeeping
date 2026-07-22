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
import androidx.navigation.fragment.NavHostFragment
import com.chico.myhomebookkeeping.checks.CheckNightMode
import com.chico.myhomebookkeeping.backup.DatabaseRestoreManager
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.helpers.UiHelper
import com.chico.myhomebookkeeping.icons.IconResourceSynchronizer
import com.chico.myhomebookkeeping.sp.GetSP
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.sp.SetSP
import com.chico.myhomebookkeeping.obj.Colors
import com.chico.myhomebookkeeping.obj.DayNightMode
import com.chico.myhomebookkeeping.sp.EraseSP
import com.chico.myhomebookkeeping.ui.categories.CategoriesFragment
import com.chico.myhomebookkeeping.ui.dialogs.WhatNewInLastVersionDialog
import com.chico.myhomebookkeeping.ui.fastPaymentsPackage.fastPayments.UpdateViewModel
import com.chico.myhomebookkeeping.ui.settings.SettingsFragment
import com.chico.myhomebookkeeping.utils.launchUi
import kotlinx.coroutines.runBlocking
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private val checkNightMode = CheckNightMode()
    private lateinit var eraseSP: EraseSP
    private var searchMenuItem: MenuItem? = null
    private var categoryOrderMenuItem: MenuItem? = null
    private var quickPaymentSettingsMenuItem: MenuItem? = null

    private lateinit var spEditor: SharedPreferences.Editor

    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView
    private val uiHelper = UiHelper()
    lateinit var mainActivityViewModel: MainActivityViewModel
    private var hasCheckedWhatsNewThisSession = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DatabaseRestoreManager.applyPendingRestore(applicationContext)
        IconResourceSynchronizer.synchronize(applicationContext)
        setContentView(R.layout.activity_main)

        mainActivityViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        uiMode()
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController
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
        setupSearchMenuVisibility()
//        eraseSP.eraseTempSP()

        if (savedInstanceState == null) {
            val isFirstLaunch = mainActivityViewModel.checkIsFirstLaunch()
            if (isFirstLaunch) {
                navController.navigate(R.id.nav_first_launch_setup_fragment)
            } else {
                val startDestinationId = mainActivityViewModel.getStartDestinationId()
                if (startDestinationId != R.id.nav_fast_payments_fragment) {
                    navController.navigate(startDestinationId)
                }
            }
        }
//        if (mainActivityViewModel.checkIsFirstLaunch()) navController.navigate(R.id.nav_first_launch_fragment)
    }

    override fun onResume() {
        super.onResume()
        maybeShowWhatsNewAfterUpdate()
    }

    private fun maybeShowWhatsNewAfterUpdate() {
        if (hasCheckedWhatsNewThisSession) return

        hasCheckedWhatsNewThisSession = true
        window.decorView.post {
            checkVersionUpdateAndShowWhatsNew()
        }
    }

    private fun checkVersionUpdateAndShowWhatsNew() {
        if (mainActivityViewModel.isLastVersionOfProgramChecked()) return

        ViewModelProvider(this).get(UpdateViewModel::class.java).update()
        WhatNewInLastVersionDialog().show(
            supportFragmentManager,
            getString(R.string.tag_show_dialog)
        )
        mainActivityViewModel.setLastVersionChecked()
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
                    R.id.nav_first_launch_setup_fragment -> {
                        uiHelper.hideUiElement(toolbar)
                        uiHelper.hideUiElement(bottomNavigationView)
                    }
                    R.id.nav_first_launch_select_currencies_fragment->{
                        uiHelper.hideUiElement(toolbar)
                        uiHelper.hideUiElement(bottomNavigationView)
                    }
                    R.id.nav_help_fragment -> {
                        uiHelper.hideUiElement(toolbar)
                        uiHelper.hideUiElement(bottomNavigationView)
                    }
                    R.id.nav_first_launch_fragment -> {
                        uiHelper.hideUiElement(toolbar)
                        uiHelper.hideUiElement(bottomNavigationView)
                    }
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
        searchMenuItem = menu.findItem(R.id.search_button)
        categoryOrderMenuItem = menu.findItem(R.id.category_order_button)
        quickPaymentSettingsMenuItem = menu.findItem(R.id.quick_payment_settings_button)
        val isCategoriesDestination = navController.currentDestination?.id == R.id.nav_categories
        val isNewMoneyMovingDestination =
            navController.currentDestination?.id == R.id.nav_new_money_moving
        searchMenuItem?.isVisible = isCategoriesDestination
        categoryOrderMenuItem?.isVisible = isCategoriesDestination
        quickPaymentSettingsMenuItem?.isVisible = isNewMoneyMovingDestination
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search_button -> {
                getCurrentFragment<CategoriesFragment>()?.toggleSearch()
                true
            }
            R.id.category_order_button -> {
                getCurrentFragment<CategoriesFragment>()?.toggleCategoryOrderEditMode()
                true
            }
            R.id.quick_payment_settings_button -> {
                openSettingsSection(SettingsFragment.SECTION_QUICK_PAYMENT)
                true
            }
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

    private fun setupSearchMenuVisibility() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val isCategoriesDestination = destination.id == R.id.nav_categories
            val isNewMoneyMovingDestination = destination.id == R.id.nav_new_money_moving
            searchMenuItem?.isVisible = isCategoriesDestination
            categoryOrderMenuItem?.isVisible = isCategoriesDestination
            quickPaymentSettingsMenuItem?.isVisible = isNewMoneyMovingDestination
        }
    }

    private fun openSettingsSection(section: String) {
        navController.navigate(
            R.id.nav_setting,
            Bundle().apply {
                putString(SettingsFragment.ARG_SECTION, section)
            }
        )
    }

    private inline fun <reified T> getCurrentFragment(): T? {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
        return navHostFragment
            ?.childFragmentManager
            ?.primaryNavigationFragment as? T
    }
}
