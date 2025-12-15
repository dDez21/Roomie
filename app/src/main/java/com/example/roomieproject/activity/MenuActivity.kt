package com.example.roomieproject.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.roomieproject.R
import com.google.android.material.navigation.NavigationView
import androidx.activity.addCallback


class MenuActivity : AppCompatActivity(){

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerMenu: ImageButton
    private lateinit var arrowBack: ImageButton
    private lateinit var navView: NavigationView
    private lateinit var userIcon: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)

        //inizializzo Firebase
        //auth = FirebaseAuth.getInstance()
        //db = FirebaseFirestore.getInstance()

        //per menu laterale
        drawerLayout = findViewById(R.id.drawerLayout)
        drawerMenu = findViewById(R.id.navigation_drawer)
        arrowBack = findViewById(R.id.backButton)
        userIcon = findViewById(R.id.userAvatar)


        //imposto modo di navigazione
        navView = findViewById(R.id.navView)
        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        val navController = navHost.navController


        //attivo drawer
        drawerMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }


        //attivo freccia
        arrowBack.setOnClickListener {
            navController.popBackStack()
        }


        //attivo apertura menu utente
        userIcon.setOnClickListener {
            if (navController.currentDestination?.id != R.id.userFragment) {
                navController.navigate(R.id.userFragment)
            }
        }


        //a cambio fragment disattivo drawer
        navView.setNavigationItemSelectedListener { item ->
            val handled = NavigationUI.onNavDestinationSelected(item, navController)
            if (handled) drawerLayout.closeDrawer(GravityCompat.START)
            handled
        }


        //prendo bottone aggiunta gruppo
        val header = navView.getHeaderView(0)
        val newGroup = header.findViewById<View>(R.id.addGroup)
        newGroup.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            navController.navigate(R.id.groupSectionFragment)
        }


        //imposto drawer solo su menuFragment
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.menuFragment -> showDrawerMode()
                else -> showBackMode()
            }
        }


        //chiude drawer se faccio indietro mentre è aperto
        onBackPressedDispatcher.addCallback(this) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                navController.popBackStack()
            }
        }
    }



    //mostro drawer
    private fun showDrawerMode() {
        drawerMenu.visibility = View.VISIBLE
        arrowBack.visibility = View.GONE
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }


    //mostro freccia
    private fun showBackMode() {
        drawerMenu.visibility = View.GONE
        arrowBack.visibility = View.VISIBLE
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }
}