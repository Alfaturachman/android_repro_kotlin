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

        // Check SharedPreferences
        val sharedPreferences: SharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
        val userLevel = sharedPreferences.getString("level", null)
        val userNama = sharedPreferences.getString("nama", null)

        // Drawer dan NavigationView setup
        val drawerLayout: DrawerLayout = mainBinding.drawerLayout
        val navView: NavigationView = mainBinding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        setupMenu(navView.menu, userLevel)

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

    private fun setupMenu(menu: Menu, userLevel: String?) {
        // Sembunyikan semua menu terlebih dahulu
        menu.findItem(R.id.nav_pengelola)?.isVisible = false
        menu.findItem(R.id.nav_pemasok)?.isVisible = false
        menu.findItem(R.id.nav_harga_ban)?.isVisible = false

        // Menu sesuai dengan role
        when (userLevel) {
            "pengelola" ->
                menu.findItem(R.id.nav_pengelola)?.isVisible = true
            "pemasok" ->
                menu.findItem(R.id.nav_pemasok)?.isVisible = true
        }
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
