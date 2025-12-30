package com.example.roomieproject.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.roomieproject.R
import com.google.android.material.navigation.NavigationView
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.roomieproject.adapter.DrawerGroupsAdapter
import com.example.roomieproject.util.UserState
import com.example.roomieproject.viewmodel.MenuViewModel
import kotlinx.coroutines.launch


class MenuActivity : AppCompatActivity(){

    private val vm: MenuViewModel by viewModels()
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerMenu: ImageButton
    private lateinit var arrowBack: ImageButton
    private lateinit var navView: NavigationView
    private lateinit var userName: TextView
    private lateinit var userIcon: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val topBar = findViewById<View>(R.id.top_bar)
        val baseHeight = topBar.layoutParams.height
        ViewCompat.setOnApplyWindowInsetsListener(topBar) { v, insets ->
            val topInset = insets.getInsets(
                WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.displayCutout()
            ).top
            v.updatePadding(top = topInset)
            v.updateLayoutParams {
                height = baseHeight + topInset
            }
            insets
        }
        ViewCompat.requestApplyInsets(topBar)

        //imposto modo di navigazione
        navView = findViewById(R.id.navView)
        val navHost = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        val navController = navHost.navController

        //attivo drawer
        drawerLayout = findViewById(R.id.drawerLayout)
        drawerMenu = findViewById(R.id.navigation_drawer)
        drawerMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        //attivo freccia
        arrowBack = findViewById(R.id.backButton)
        arrowBack.setOnClickListener {
            navController.popBackStack()
        }

        //attivo apertura menu utente
        userName = findViewById(R.id.userName)
        userIcon = findViewById(R.id.userAvatar)
        userIcon.setOnClickListener {
            if (navController.currentDestination?.id != R.id.userFragment) {
                navController.navigate(R.id.userFragment)
            }
        }
        loadUserData()

        //a cambio fragment disattivo drawer
        navView.setNavigationItemSelectedListener { item ->
            val handled = NavigationUI.onNavDestinationSelected(item, navController)
            if (handled) drawerLayout.closeDrawer(GravityCompat.START)
            handled
        }

        //elenco gruppi
        val header = navView.getHeaderView(0)
        val baseHeaderTopPadding = header.paddingTop
        ViewCompat.setOnApplyWindowInsetsListener(header) { v, insets ->
            val topInset = insets.getInsets(
                WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.displayCutout()
            ).top
            v.updatePadding(top = baseHeaderTopPadding + topInset)
            insets
        }
        ViewCompat.requestApplyInsets(header)
        val groups = header.findViewById<RecyclerView>(R.id.rv_groups)
        val groupsAdapter = DrawerGroupsAdapter { groupId->
            drawerLayout.closeDrawer(GravityCompat.START)
            val group = bundleOf("groupId" to groupId)
            navController.navigate(R.id.menuFragment, group, androidx.navigation.navOptions {
                popUpTo(R.id.menuFragment) { inclusive = true }
                launchSingleTop = true
            })
        }
        groups.layoutManager = LinearLayoutManager(this)
        groups.adapter = groupsAdapter

        //aggiungi gruppo
        val newGroup = header.findViewById<View>(R.id.addGroup)
        newGroup.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            navController.navigate(R.id.groupSectionFragment)
        }

        //imposto drawer solo su  + caso alla creazione dell'utente
        navController.addOnDestinationChangedListener { _, destination, args ->
            val forcedNoGroup = args?.getBoolean("forcedNoGroup", false) ?: false
            when (destination.id) {
                R.id.menuFragment -> showDrawerMode()
                R.id.groupSectionFragment -> {
                    if (forcedNoGroup) showNoBackMode() else showBackMode()
                }
                else -> showBackMode()
            }
            if (destination.id == R.id.menuFragment) {
                showDrawerMode()
                vm.refreshGroups()
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

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.userState.collect { state ->
                    when (state) {
                        UserState.Idle -> Unit
                        UserState.Loading -> Unit
                        is UserState.HasGroups -> {
                            groupsAdapter.submitList(state.groups)
                        }
                        UserState.NoGroup -> {
                            val current = navController.currentDestination?.id
                            if (current != R.id.groupSectionFragment) {
                                navController.navigate(R.id.groupSectionFragment, bundleOf("forcedNoGroup" to true))}
                        }
                        is UserState.Error -> {
                            Toast.makeText(
                                this@MenuActivity,
                                state.e.message ?: "Errore verifica gruppi",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
        vm.checkUser()
    }

    //carico dati utente per top bar
    private fun loadUserData(){
        lifecycleScope.launch {
            try {
                val (name, url) = vm.userData()
                userName.text = if (name.isBlank()) "Utente" else name
                if (!url.isNullOrBlank()) {
                    Glide.with(this@MenuActivity).load(url).into(userIcon)
                } else {
                    userIcon.setImageResource(R.drawable.user_logo)
                }
            } catch (e: Exception) {
                userName.text = "Utente"
                userIcon.setImageResource(R.drawable.user_logo)
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


    //caso nuovo utente
    private fun showNoBackMode() {
        drawerMenu.visibility = View.GONE
        arrowBack.visibility = View.GONE
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }
}