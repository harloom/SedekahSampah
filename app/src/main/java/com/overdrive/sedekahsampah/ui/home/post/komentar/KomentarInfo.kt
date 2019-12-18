package com.overdrive.sedekahsampah.ui.home.post.komentar

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.overdrive.sedekahsampah.R
import com.overdrive.sedekahsampah.models.Komentar
import com.overdrive.sedekahsampah.models.Post
import kotlinx.android.synthetic.main.dialog_post_more.*

class KomentarInfo (private val item  : Komentar, private val callback  : InteractionEditClick): BottomSheetDialogFragment() {

    private lateinit var btnHapus: LinearLayout
    private lateinit var btnEdit: LinearLayout
    private lateinit var btnLapor: LinearLayout


    private val TAG = "DialogProfile"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_post_more, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setCancelable(true)
        return dialog
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnHapus =txtHapus
        btnLapor = txtReport
        btnEdit = txtEdit
        btnHapus.setOnClickListener {
            val dialog =
                MaterialAlertDialogBuilder(activity).setMessage("Apakah Anda Ingin Menghapus!")
                    .setPositiveButton("OK") { dialogInterface, i ->
                        callback.onDeleteListener(item)
                        dismiss()
                    }.setCancelable(false).setNegativeButton("Batal") { dialogInterface, i ->
                        dialogInterface.dismiss()
                    }


            dialog.show()
        }



        if (item.uid != FirebaseAuth.getInstance().currentUser!!.uid) {
            btnEdit.visibility = View.GONE
            btnHapus.visibility = View.GONE
        }

        action_dismiss.setOnClickListener {
            dismiss()
        }

        btnEdit.setOnClickListener {
//            val newFragment = DialogEditPost(item,callback)
//            if (fragmentManager != null) {
//                newFragment.show(fragmentManager!!, "morePost")
//            }

        }
        btnLapor.setOnClickListener {
            val dialog =
                MaterialAlertDialogBuilder(activity).setMessage("Apakah Anda Ingin Melapor!")
                    .setPositiveButton("OK") { dialogInterface, i ->
                    }.setCancelable(true).setNegativeButton("Batal") { dialogInterface, i ->
                        callback.onLaporListener(item)
                        dialogInterface.dismiss()
                    }


            dialog.show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
    }

    private fun log(s: String) {

    }

    private fun deletePost(itemId: String) {



    }
}

interface InteractionEditClick {
    fun onDeleteListener(item : Komentar)
    fun onEditListerner(id : String,title : String? , content : String?)
    fun onLaporListener(item: Komentar)
}