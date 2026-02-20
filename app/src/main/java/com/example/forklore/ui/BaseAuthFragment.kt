package com.example.forklore.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.forklore.MainActivity
import com.example.forklore.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth

abstract class BaseAuthFragment : Fragment() {

    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser == null) {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(findNavController().graph.id, true)
                .build()
            findNavController().navigate(R.id.loginFragment, null, navOptions)
        }
    }

    /**
     * Helper method to set up a MaterialToolbar with back navigation support.
     * Should be called in onViewCreated() of child fragments.
     */
    protected fun setupToolbarNavigation(toolbar: MaterialToolbar) {
        val activity = requireActivity() as? MainActivity
        if (activity != null) {
            toolbar.setupWithNavController(findNavController(), activity.appBarConfiguration)
        } else {
            // Fallback if activity is not MainActivity
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
    }
}
