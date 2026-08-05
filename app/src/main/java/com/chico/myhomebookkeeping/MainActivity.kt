package com.chico.myhomebookkeeping

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import com.google.android.material.navigation.NavigationView
import com.google.android.material.appbar.AppBarLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.chico.myhomebookkeeping.checks.CheckNightMode
import com.chico.myhomebookkeeping.backup.DatabaseRestoreManager
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.helpers.UiHelper
import com.chico.myhomebookkeeping.icons.IconResourceSynchronizer
import com.chico.myhomebookkeeping.obj.AppLanguage
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.obj.Colors
import com.chico.myhomebookkeeping.obj.DayNightMode
import com.chico.myhomebookkeeping.sp.EraseSP
import com.chico.myhomebookkeeping.obj.QuickAccessPanel
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
    private lateinit var navHostView: View
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var navView: NavigationView
    private var navHostInitialPadding = ViewPadding()
    private lateinit var sharedPreferences: SharedPreferences
    private var isQuickAccessDestinationListenerAdded = false
    private var latestSystemBarInsets = InsetsState()
    private val quickAccessSettingsListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == Constants.QUICK_ACCESS_PANEL_ITEMS && ::bottomNavigationView.isInitialized) {
                setupQuickAccessPanel()
            }
        }
    private val uiHelper = UiHelper()
    lateinit var mainActivityViewModel: MainActivityViewModel
    private var hasCheckedWhatsNewThisSession = false

    override fun attachBaseContext(newBase: Context) {
        // Apply the saved AppCompat locale before fragments are restored.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            AppLanguage.applySelectedLanguage(newBase)
        }
        super.attachBaseContext(newBase)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            AppLanguage.applySelectedLanguage(this)
        }
        DatabaseRestoreManager.applyPendingRestore(applicationContext)
        IconResourceSynchronizer.synchronize(applicationContext)
        setContentView(R.layout.activity_main)

        mainActivityViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        sharedPreferences = getSharedPreferences(Constants.SP_NAME, MODE_PRIVATE)
        sharedPreferences.registerOnSharedPreferenceChangeListener(quickAccessSettingsListener)

        uiMode()
        setupSystemBarIconAppearance()
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        appBarLayout = findViewById(R.id.app_bar_layout)
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.isSaveEnabled = false

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        navHostView = findViewById(R.id.nav_host_fragment)
        setupWindowInsets(drawerLayout)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_money_moving_query,
                R.id.nav_money_moving,
                R.id.nav_reports,
                R.id.nav_fast_payments_fragment,
                R.id.nav_new_money_moving,
                R.id.nav_new_transfer,
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
        navView.setNavigationItemSelectedListener { item ->
            navigateToTopLevelDestination(item.itemId)
            drawerLayout.closeDrawers()
            true
        }

        setupQuickAccessPanel()

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
                    navigateToTopLevelDestination(startDestinationId)
                }
            }
        }
//        if (mainActivityViewModel.checkIsFirstLaunch()) navController.navigate(R.id.nav_first_launch_fragment)
    }

    override fun onResume() {
        super.onResume()
        maybeShowWhatsNewAfterUpdate()
    }

    fun showWhatsNewAfterFirstLaunchIfNeeded() {
        maybeShowWhatsNewAfterUpdate()
    }

    private fun maybeShowWhatsNewAfterUpdate() {
        if (hasCheckedWhatsNewThisSession) return
        if (isFirstLaunchFlowActive()) return

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

    private fun isFirstLaunchFlowActive(): Boolean {
        if (mainActivityViewModel.checkIsFirstLaunch()) return true

        return when (navController.currentDestination?.id) {
            R.id.nav_first_launch_setup_fragment,
            R.id.nav_first_launch_select_currencies_fragment,
            R.id.nav_first_launch_fragment -> true
            else -> false
        }
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
                applyNavHostInsets()
            }
        }
    }

    private fun setupSystemBarIconAppearance() {
        val isNightMode =
            resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
                    Configuration.UI_MODE_NIGHT_YES
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = !isNightMode
        }
    }

    private fun setupWindowInsets(drawerLayout: DrawerLayout) {
        val appBarInitialPadding = appBarLayout.recordInitialPadding()
        val navViewInitialPadding = navView.recordInitialPadding()
        navHostInitialPadding = navHostView.recordInitialPadding()
        val bottomNavigationInitialPadding = bottomNavigationView.recordInitialPadding()
        val bottomNavigationInitialHeight = bottomNavigationView.layoutParams.height

        // Edge-to-edge mode draws behind system bars, so each chrome view reapplies its own safe padding.
        ViewCompat.setOnApplyWindowInsetsListener(drawerLayout) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            latestSystemBarInsets = InsetsState(
                top = systemBars.top,
                bottom = systemBars.bottom,
                imeBottom = ime.bottom
            )

            appBarLayout.updatePadding(top = appBarInitialPadding.top + systemBars.top)
            navView.updatePadding(
                top = navViewInitialPadding.top + systemBars.top,
                bottom = navViewInitialPadding.bottom + systemBars.bottom
            )
            bottomNavigationView.updatePadding(
                bottom = bottomNavigationInitialPadding.bottom + systemBars.bottom
            )
            bottomNavigationView.updateLayoutHeight(
                baseHeight = bottomNavigationInitialHeight,
                extraHeight = systemBars.bottom
            )
            applyNavHostInsets()
            insets
        }
    }

    private fun applyNavHostInsets() {
        // IME and navigation bar are mutually exclusive in practice; keep the larger safe area.
        navHostView.updatePadding(
            bottom = navHostInitialPadding.bottom +
                    maxOf(latestSystemBarInsets.bottom, latestSystemBarInsets.imeBottom)
        )
    }

    private fun View.recordInitialPadding() = ViewPadding(
        left = paddingLeft,
        top = paddingTop,
        right = paddingRight,
        bottom = paddingBottom
    )

    private fun View.updateLayoutHeight(baseHeight: Int, extraHeight: Int) {
        if (baseHeight <= 0) return
        val newHeight = baseHeight + extraHeight
        if (layoutParams.height == newHeight) return
        layoutParams = (layoutParams as ViewGroup.LayoutParams).apply {
            height = newHeight
        }
    }

    private data class ViewPadding(
        val left: Int = 0,
        val top: Int = 0,
        val right: Int = 0,
        val bottom: Int = 0
    )

    private data class InsetsState(
        val top: Int = 0,
        val bottom: Int = 0,
        val imeBottom: Int = 0
    )

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        searchMenuItem = menu.findItem(R.id.search_button)
        categoryOrderMenuItem = menu.findItem(R.id.category_order_button)
        quickPaymentSettingsMenuItem = menu.findItem(R.id.quick_payment_settings_button)
        val isCategoriesDestination = navController.currentDestination?.id == R.id.nav_categories
        val isNewMoneyMovingDestination =
            navController.currentDestination?.id == R.id.nav_new_money_moving ||
                    navController.currentDestination?.id == R.id.nav_new_transfer
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
//            Help will be created later; keep the action disabled with the hidden menu item.
//            R.id.help_button -> {
//                navController.navigate(R.id.nav_help_fragment)
//                true
//            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        when (navController.currentDestination?.id) {
            // First launch must be completed through the setup steps.
            R.id.nav_first_launch_setup_fragment,
            R.id.nav_first_launch_select_currencies_fragment,
            R.id.nav_first_launch_fragment -> return
            R.id.nav_money_moving -> finish()
            else -> super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun setupSearchMenuVisibility() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val isCategoriesDestination = destination.id == R.id.nav_categories
            val isNewMoneyMovingDestination =
                destination.id == R.id.nav_new_money_moving || destination.id == R.id.nav_new_transfer
            searchMenuItem?.isVisible = isCategoriesDestination
            categoryOrderMenuItem?.isVisible = isCategoriesDestination
            quickPaymentSettingsMenuItem?.isVisible = isNewMoneyMovingDestination
        }
    }

    private fun setupQuickAccessPanel() {
        val menu = bottomNavigationView.menu
        val selectedDestinationId = navController.currentDestination?.id
        while (menu.size() > 0) {
            menu.removeItem(menu.getItem(0).itemId)
        }
        QuickAccessPanel.getItems(sharedPreferences).forEachIndexed { index, item ->
            menu.add(Menu.NONE, item.destinationId, index, item.titleRes)
                .setIcon(item.iconRes)
        }
        selectedDestinationId?.let {
            if (menu.findItem(it) != null) bottomNavigationView.selectedItemId = it
        }
        bottomNavigationView.setOnItemSelectedListener { item ->
            if (navController.currentDestination?.id != item.itemId) {
                navigateToTopLevelDestination(item.itemId)
            }
            true
        }
        if (!isQuickAccessDestinationListenerAdded) {
            isQuickAccessDestinationListenerAdded = true
            navController.addOnDestinationChangedListener { _, destination, _ ->
                if (bottomNavigationView.menu.findItem(destination.id) != null) {
                    bottomNavigationView.selectedItemId = destination.id
                }
            }
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

    private fun navigateToTopLevelDestination(destinationId: Int) {
        val navOptions = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setPopUpTo(navController.graph.findStartDestination().id, false)
            .build()
        if (destinationId == R.id.nav_categories) {
            navController.navigate(
                R.id.nav_categories,
                CategoriesFragment.openModeArgs(CategoriesFragment.OPEN_MODE_STANDALONE),
                navOptions
            )
        } else {
            navController.navigate(destinationId, null, navOptions)
        }
    }

    private inline fun <reified T> getCurrentFragment(): T? {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
        return navHostFragment
            ?.childFragmentManager
            ?.primaryNavigationFragment as? T
    }

    override fun onDestroy() {
        if (::sharedPreferences.isInitialized) {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(quickAccessSettingsListener)
        }
        super.onDestroy()
    }
}
