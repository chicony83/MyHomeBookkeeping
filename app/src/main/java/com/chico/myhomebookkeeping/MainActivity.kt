package com.chico.myhomebookkeeping

import android.os.Bundle
import android.view.Menu
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
import com.chico.myhomebookkeeping.helpers.UiHelper

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private val uiHelper = UiHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
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
                R.id.nav_new_money_moving,
                R.id.nav_setting
            ),
            drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        fabSetOnClickListener(fab, navController)

        hideFab(navController, fab)
    }

    private fun hideFab(
        navController: NavController,
        fab: FloatingActionButton
    ) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.nav_new_money_moving -> uiHelper.hideUiElement(fab)
                R.id.nav_currencies -> uiHelper.hideUiElement(fab)
//                R.id.nav_categories -> uiHelper.hideUiElement(fab)
                R.id.nav_cash_account -> uiHelper.hideUiElement(fab)
                R.id.nav_money_moving_query -> uiHelper.hideUiElement(fab)
                R.id.nav_setting -> uiHelper.hideUiElement(fab)
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
//        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}