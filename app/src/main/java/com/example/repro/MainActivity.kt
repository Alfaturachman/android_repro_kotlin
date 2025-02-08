package com.example.repro

import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.repro.databinding.ActivityMainBinding
import com.example.repro.databinding.NavHeaderMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Menggunakan binding untuk activity_main.xml
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        mainBinding.navView.setCheckedItem(R.id.nav_home)
        mainBinding.navView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            true
        }

        // Set toolbar
        setSupportActionBar(mainBinding.appBarMain.toolbar)

        // Floating Action Button (FAB) action
        mainBinding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show()
        }

        // Check SharedPreferences
        val sharedPreferences: SharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
        val userLevel = sharedPreferences.getString("level", null)
        val userNama = sharedPreferences.getString("nama", null)

        // Drawer dan NavigationView setup
        val drawerLayout: DrawerLayout = mainBinding.drawerLayout
        val navView: NavigationView = mainBinding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        // Konfigurasi AppBar
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_pemasok, R.id.nav_pengelola, R.id.nav_harga_ban
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Mengakses elemen dalam nav_header_main.xml
        val headerView = navView.getHeaderView(0)
        val navHeaderBinding = NavHeaderMainBinding.bind(headerView)

        // Set properti di header (contoh)
        navHeaderBinding.imageView.setImageResource(R.mipmap.ic_launcher_round)
        navHeaderBinding.tvNavNama.text = userNama
        navHeaderBinding.tvNavEmail.text = userLevel
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
