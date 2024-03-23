package com.example.phoneregister

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.StateSet
import androidx.core.widget.addTextChangedListener
import com.example.phoneregister.databinding.ActivityMain2Binding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class MainActivity2 : AppCompatActivity() {
    private val binding by lazy { ActivityMain2Binding.inflate(layoutInflater) }
    lateinit var auth : FirebaseAuth
    lateinit var resendToken : PhoneAuthProvider.ForceResendingToken
    lateinit var storedVerificationId:String
    private var countDownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 59000 // 59 секунд в миллисекундах

    private val countDownInterval: Long = 1000 // интервал обновления таймера в миллисекундах
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        countDown()


        auth = FirebaseAuth.getInstance()


        binding.btnAgain.setOnClickListener{
            if (it.toString().length == 6) {
                verifiyCode()
            }
        }
        val intent = intent
        val phone = intent.getStringExtra("phone")
        binding.e.text = ("Bir martalik kod  $phone \n" +
                "raqamiga yuborildi")
        sendVerificationCode(phone.toString())

        binding.apply {
              binding.btnKoddd.setOnClickListener {
                  verifiyCode()
              }

            u.addTextChangedListener {
                if (it.toString().length == 6){
                    verifiyCode()
                }
            }
        }
    }

    fun countDown() {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateCountdownText()
            }

            override fun onFinish() {
                binding.btnAgain.visibility = View.GONE
                binding.btnAgain.visibility = View.VISIBLE
            }
        }

        (countDownTimer as CountDownTimer).start()

    }
    private fun updateCountdownText() {
        val seconds = (timeLeftInMillis / 1000).toInt()
        val timeLeftFormatted = String.format("%02d", seconds)
        binding.sanoq.text = timeLeftFormatted
    }
    private fun verifiyCode() {
        val code = binding.u.text.toString()

        if (code.length == 6) {
            val credential = PhoneAuthProvider.getCredential(storedVerificationId, code)
            signInWithPhoneAuthCredential(credential)

            val intent = Intent(this, MainActivity3::class.java)
            startActivity(intent)
        }
    }


    fun sendVerificationCode(phoneNumber:String){
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:`
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.d(StateSet.TAG, "onVerificationCompleted:$credential")
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w(StateSet.TAG, "onVerificationFailed", e)

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            }

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d(StateSet.TAG, "onCodeSent:$verificationId")?:""

            // Save verification ID and resending token so we can use them later
            storedVerificationId = verificationId
            resendToken = token
        }
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(StateSet.TAG, "signInWithCredential:success")

                    Toast.makeText(this, "Muvaffaqiyatli", Toast.LENGTH_SHORT).show()

                    val user = task.result?.user
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(StateSet.TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Muvaffaqiyatsiz!!!", Toast.LENGTH_SHORT).show()
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(this, "Kod xato kiritildi", Toast.LENGTH_SHORT).show()
                    }
                    // Update UI
                }
            }
    }
}