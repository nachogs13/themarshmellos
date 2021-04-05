package com.muei.apm.fasterwho

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat

class NavigationActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawerLayout:DrawerLayout = findViewById(R.id.drawer_layout)

        val toggle = ActionBarDrawerToggle(
                this, drawerLayout,toolbar,R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        val navController = findNavController(R.id.nav_host_fragment)
        val navView: NavigationView = findViewById(R.id.nav_view)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        //appBarConfiguration = AppBarConfiguration(
        //    setOf(
        //        R.id.nav_home, R.id.nav_profile, R.id.nav_rankings,R.id.nav_amigos
        //    ), drawerLayout
        //)
        //setupActionBarWithNavController(navController, appBarConfiguration)
        //navView.setupWithNavController(navController)

        navView.setNavigationItemSelectedListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.navigation, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val intentInicio = Intent(this, InicioActivity::class.java)
        val intentProfile = Intent(this, ProfileActivity::class.java)
        val intentRankings = Intent(this, RankingActivity::class.java)
        val intentRutas = Intent(this, MisRutas::class.java)
        val intentAmigos = Intent(this, MisAmigos::class.java)

        when (item.itemId) {
            R.id.nav_home -> startActivity(intentInicio)//Toast.makeText(this, "Clicked item one", Toast.LENGTH_SHORT).show()
            R.id.nav_profile -> startActivity(intentProfile)//Toast.makeText(this, "Clicked item two", Toast.LENGTH_SHORT).show()
            R.id.nav_rankings -> startActivity(intentRankings)//Toast.makeText(this, "Clicked item three", Toast.LENGTH_SHORT).show()
            R.id.nav_routes -> startActivity(intentRutas)//Toast.makeText(this, "Clicked item four", Toast.LENGTH_SHORT).show()
            R.id.nav_amigos -> startActivity(intentAmigos)//Toast.makeText(this, "Clicked item five", Toast.LENGTH_SHORT).show()
        }
        return true
    }
}