package com.overdrive.sedekahsampah.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.overdrive.sedekahsampah.R
import com.redmadrobot.inputmask.MaskedTextChangedListener
import com.redmadrobot.inputmask.helper.AffinityCalculationStrategy
import androidx.annotation.NonNull




class PhoneValidation : AppCompatActivity() {
    private lateinit var phoneNumber : EditText;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_validation)



        initMask()
    }

    private fun initMask() {
         phoneNumber = findViewById<EditText>(R.id.phoneNumber)

        val affineFormats = mutableListOf<String>()
        affineFormats.add("+62 ([000]) [000]-[00]-[00]")
        val listener : MaskedTextChangedListener = MaskedTextChangedListener.Companion.installOn(
            phoneNumber,
            "+62 ([000]) [000]-[00]-[00]",
            affineFormats,
            AffinityCalculationStrategy.PREFIX,
            object : MaskedTextChangedListener.ValueListener {
                override fun onTextChanged(
                    maskFilled: Boolean,
                    extractedValue: String,
                    formattedValue: String
                ) {
//                    logValueListener(maskFilled, extractedValue, formattedText)
//                    checkBox.setChecked(maskFilled)
                }
            }
        )
        phoneNumber.setHint(listener.placeholder())
    }
}
