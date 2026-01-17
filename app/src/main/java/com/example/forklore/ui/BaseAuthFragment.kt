package com.example.forklore.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.forklore.R
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
}
