package com.overdrive.sedekahsampah.ui.user

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.overdrive.sedekahsampah.R
import com.overdrive.sedekahsampah.models.Post

import kotlinx.android.synthetic.main.dialog_edit_post.*

class DialogEditData(private val keyField : String,private val value: String , private val callback: InteractionEdiData) : DialogFragment() {
    private lateinit var _value: EditText



    private var tempValue = ""


    private val TAG = "DialogProfile"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_edit_data, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCancelable(false)
        return dialog
    }

    override fun onStart() {
        super.onStart()


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _value = view.findViewById(R.id.txt_edit)
        _value.setText(value)


        tempValue = _value.text.toString()
        action_dismiss.setOnClickListener {

            if( tempValue != _value.text.toString()){
                val dialogSimpan =
                    MaterialAlertDialogBuilder(activity).setMessage("Apakah Anda Ingin Menyimpan!")
                        .setPositiveButton("OK") { dialogInterface, i ->
                            callback.onDataCallback(_value.text.toString(),keyField)
                            this.dismiss()
                        }.setCancelable(true).setNegativeButton("Batal") { dialogInterface, i ->
                            dialogInterface.dismiss()
                            this.dismiss()
                        }


                dialogSimpan.show()
            }else{
                dismiss()
            }

        }

    }

    private fun editPost() {

    }


    override fun onDestroyView() {
        super.onDestroyView()
    }

    private fun log(s: String) {

    }
}
