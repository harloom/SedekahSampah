package com.overdrive.sedekahsampah.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.EditText
import android.widget.Toast
import com.overdrive.sedekahsampah.R
import com.redmadrobot.inputmask.MaskedTextChangedListener
import com.redmadrobot.inputmask.helper.AffinityCalculationStrategy
import androidx.annotation.NonNull
import com.google.android.material.button.MaterialButton
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.overdrive.sedekahsampah.MainActivity
import kotlinx.android.synthetic.main.activity_phone_validation.*
import kotlinx.android.synthetic.main.activity_setup.*
import java.util.concurrent.TimeUnit
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.View
import com.overdrive.sedekahsampah.utils.change


class PhoneValidation : AppCompatActivity() {

   private lateinit var action_buton : MaterialButton;
    var number :String=""
    var mVerificationId : String =  "";
    private lateinit var phoneNumber : EditText;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_validation)
        action_buton  = findViewById(R.id.action_verifikasi)
        initMask()

        action_buton.setOnClickListener {
            action_buton.isEnabled = false
            frame_code.visibility = View.VISIBLE
            timer.start()
            sendVerifikasi()
        }

        form_code.change {
            if(it.length ==6){
                verifyVerificationCode(it)
            }
        }

    }

    val timer = object: CountDownTimer(60000, 1000) {
        @SuppressLint("SetTextI18n")
        override fun onTick(millisUntilFinished: Long) {
            action_buton.setText("Tunggu ${TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)}")
        }

        override fun onFinish() {
            action_buton.setText("Kirim Kembali")
            frame_code.visibility = View.INVISIBLE
            action_buton.isEnabled= true
        }
    }

    private fun initMask() {
         phoneNumber = findViewById(R.id.phoneNumber)

        val affineFormats = mutableListOf<String>()
        affineFormats.add("+62 ([000]) [0000]-[00]-[000]")

        val listener : MaskedTextChangedListener = MaskedTextChangedListener.Companion.installOn(
            phoneNumber,
            "+62 ([000]) [0000]-[00]-[000]",
            affineFormats,
            AffinityCalculationStrategy.PREFIX,
            object : MaskedTextChangedListener.ValueListener {
                override fun onTextChanged(
                    maskFilled: Boolean,
                    extractedValue: String,
                    formattedValue: String
                ) {
                    number = extractedValue
                  println("$maskFilled :  $extractedValue  : $formattedValue");

                }
            }

        )
        phoneNumber.setHint(listener.placeholder())
    }

    private fun sendVerifikasi(){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            "+62$number", // Phone number to verify
            60, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            this, // Activity (for callback binding)
            callbacks) // OnVerificationStateChangedCallbacks
    }

    private fun resendVerificationCode(phoneNumber: String,
                                       token: PhoneAuthProvider.ForceResendingToken?) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            "+62$phoneNumber", // Phone number to verify
            60, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            this, // Activity (for callback binding)
            callbacks, // OnVerificationStateChangedCallbacks
            token)             // ForceResendingToken from callbacks
    }

   val  callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
       override fun onVerificationCompleted(p0: PhoneAuthCredential) {
           val code = p0.smsCode

           //sometime the code is not detected automatically
           //in this case the code will be null
           //so user has to manually enter the code
           if (code != null) {
               form_code.setText(code)
               //verifying the code
               verifyVerificationCode(code)
           }



       }

       override fun onVerificationFailed(p0: FirebaseException) {

       }

       override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
           super.onCodeSent(p0, p1)
           mVerificationId = p0
       }

   }

    private fun verifyVerificationCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(mVerificationId, code)
        val user = FirebaseAuth.getInstance().currentUser
        user?.updatePhoneNumber(credential)?.addOnSuccessListener {
            val ref = FirebaseFirestore.getInstance().collection("users")
                .document(user.uid)
            ref.update("numberPhone","+62$number").addOnSuccessListener {
                goToMain()
            }.addOnFailureListener {
                shomethingError()
            }
        }?.addOnFailureListener {
            shomethingError()
        }
    }

    private fun shomethingError(){
        Toast.makeText(this@PhoneValidation,"Something is Error Please Try Again", Toast.LENGTH_LONG).show()
    }


    private fun goToMain(){
        startActivity(
            Intent(this@PhoneValidation, MainActivity::class.java).
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK));
        finish()
    }
}
