package com.example.adminblinkitclone.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.adminblinkitclone.activity.AdminMainActivity
import com.example.adminblinkitclone.R
import com.example.adminblinkitclone.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    private val viewModel : AuthViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Handler(Looper.getMainLooper()).postDelayed(
            {

                lifecycleScope.launch {
                    viewModel.isACurrentUser.collect{
                        if(it){
                            // if currrent user exists show home fragment .. not login again and again
                            startActivity(Intent(requireActivity(), AdminMainActivity::class.java))
                            requireActivity().finish()
                        }
                        else{
                            findNavController().navigate(R.id.action_splashFragment_to_signInFragment)
                        }
                    }
                }
            }, 3000)
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }


}