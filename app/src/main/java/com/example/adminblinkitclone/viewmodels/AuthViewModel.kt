package com.example.adminblinkitclone.viewmodels

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.example.adminblinkitclone.model.Admins
import com.example.adminblinkitclone.Utils
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.TimeUnit

class AuthViewModel : ViewModel() {

    private val _verificationId = MutableStateFlow<String?>(null)
    private val _otpSent = MutableStateFlow(false)
    //exposing otpsent -- means ki hum iski value bahar files main karenge
    val otpSent: MutableStateFlow<Boolean> = _otpSent

    private val _isSignedInSuccessfully = MutableStateFlow(false)
    val isSignedInSuccessfully = _isSignedInSuccessfully

    private val _isACurrentUser = MutableStateFlow(false)
    val isACurrentUser = _isACurrentUser

    init {
        Utils.getAuthInstance().currentUser?.let {
            _isACurrentUser.value = true
        }
    }

    fun sendOTP(userNumber: String, activity: Activity)
    {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

            }

            override fun onVerificationFailed(e: FirebaseException) {

            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                _verificationId.value = verificationId
                _otpSent.value = true

            }
        }

        val options = PhoneAuthOptions.newBuilder(Utils.getAuthInstance())
            .setPhoneNumber("+91$userNumber") // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun signInWithPhoneAuthCredential(otp: String, userNumber: String, user: Admins) {

        val credential = PhoneAuthProvider.getCredential(_verificationId.value.toString(), otp)
        FirebaseMessaging.getInstance().token.addOnCompleteListener {task->
            val token = task.result
            user.adminToken = token

            Utils.getAuthInstance().signInWithCredential(credential)
                .addOnCompleteListener{ task ->
                    user.uid = Utils.getCurrentUserid()
                    if (task.isSuccessful) {
                        FirebaseDatabase.getInstance().getReference("Admins").child("AdminInfo").child(user.uid!!).setValue(user)
                        _isSignedInSuccessfully.value = true
                    } else {

                    }

                }
        }
    }
}