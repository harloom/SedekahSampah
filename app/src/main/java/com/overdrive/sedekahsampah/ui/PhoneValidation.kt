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


class PhoneValidation : AppCompatActivity() {

   private lateinit var action_buton : MaterialButton;

    private lateinit var phoneNumber : EditText;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_validation)
        action_buton  = findViewById(R.id.action_verifikasi)
        initMask()

        action_buton.setOnClickListener {
            action_buton.isEnabled = false
            timer.start()
        }

    }

    val timer = object: CountDownTimer(60000, 1000) {
        @SuppressLint("SetTextI18n")
        override fun onTick(millisUntilFinished: Long) {
            action_buton.setText("Tunggu ${TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)}")
        }

        override fun onFinish() {
            action_buton.setText("Kirim Kembali")
            action_buton.isEnabled= true
        }
    }

    private fun initMask() {
         phoneNumber = findViewById(R.id.phoneNumber)

        val affineFormats = mutableListOf<String>()
        affineFormats.add("+62 ([000]) [0000]-[00]-[00]")

        val listener : MaskedTextChangedListener = MaskedTextChangedListener.Companion.installOn(
            phoneNumber,
            "+62 ([000]) [0000]-[00]-[00]",
            affineFormats,
            AffinityCalculationStrategy.PREFIX,
            object : MaskedTextChangedListener.ValueListener {
                override fun onTextChanged(
                    maskFilled: Boolean,
                    extractedValue: String,
                    formattedValue: String
                ) {
                  println("$maskFilled :  $extractedValue  : $formattedValue");

                }
            }

        )
        phoneNumber.setHint(listener.placeholder())
    }

    private fun sendVerifikasi(){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber.text.toString(), // Phone number to verify
            60, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            this, // Activity (for callback binding)
            callbacks) // OnVerificationStateChangedCallbacks
    }

   val  callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
       override fun onVerificationCompleted(p0: PhoneAuthCredential) {
           val user = FirebaseAuth.getInstance().currentUser
           user?.updatePhoneNumber(p0)?.addOnSuccessListener {
               val ref = FirebaseFirestore.getInstance().collection("users")
                   .document(user.uid)
               ref.update("numberPhone",txt_nama.text.toString()).addOnSuccessListener {
                   goToMain()
               }.addOnFailureListener {
                   shomethingError()
               }
           }?.addOnFailureListener {
               shomethingError()
           }
       }

       override fun onVerificationFailed(p0: FirebaseException) {

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
