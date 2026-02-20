package com.example.forklore

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.forklore.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController
    lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)

        // Define root fragments (top-level destinations where back button should be hidden)
        val topLevelDestinations = setOf(
            R.id.splashFragment,
            R.id.loginFragment,
            R.id.registerFragment,
            R.id.feedFragment,
            R.id.discoverFragment,
            R.id.myRecipesFragment,
            R.id.profileFragment
        )

        appBarConfiguration = AppBarConfiguration(topLevelDestinations)

        val bottomNavVisibleDestinations = setOf(
            R.id.feedFragment,
            R.id.discoverFragment,
            R.id.myRecipesFragment,
            R.id.myPostsFragment, 
            R.id.profileFragment
        )


        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in bottomNavVisibleDestinations) {
                binding.bottomNavigation.visibility = View.VISIBLE
            } else {
                binding.bottomNavigation.visibility = View.GONE
            }
        }
    }
}
