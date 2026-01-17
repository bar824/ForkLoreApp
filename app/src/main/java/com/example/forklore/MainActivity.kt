
package com.example.forklore

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.forklore.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )

        firebaseAuth = FirebaseAuth.getInstance()

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            // NOTE: The destination IDs (e.g., R.id.loginFragment) are assumed. 
            // Please replace them with the actual IDs from your navigation graph.
            when (destination.id) {
                //Destinations where the bottom navigation should be hidden
                R.id.splashFragment,
                R.id.loginFragment,
                R.id.registerFragment,
                R.id.postDetailsFragment -> {
                    binding.bottomNavigation.visibility = View.GONE
                }
                //Destinations where the bottom navigation should be visible
                R.id.myRecipesFragment,
                R.id.feedFragment,
                R.id.discoverFragment,
                R.id.profileFragment -> {
                    if (firebaseAuth.currentUser == null) {
                        // If user is not logged in, redirect to login and hide bottom navigation
                        navController.navigate(R.id.loginFragment)
                        binding.bottomNavigation.visibility = View.GONE
                    } else {
                        binding.bottomNavigation.visibility = View.VISIBLE
                    }
                }
                else -> {
                    // Default to hiding the navigation for any other fragments
                    binding.bottomNavigation.visibility = View.GONE
                }
            }
        }
    }
}
