package com.overdrive.sedekahsampah.utils

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText

fun TextInputEditText.change(afterTextChanged: (String) -> Unit) {
 this.addTextChangedListener(object : TextWatcher {
  override fun afterTextChanged(editable: Editable?) {
   afterTextChanged.invoke(editable.toString())
  }

  override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

  override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
 })
}

const val KEY_SETUP = "setup"
