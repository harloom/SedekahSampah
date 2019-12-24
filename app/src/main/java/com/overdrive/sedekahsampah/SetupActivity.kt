package com.overdrive.sedekahsampah

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.overdrive.sedekahsampah.ui.PhoneValidation
import com.overdrive.sedekahsampah.utils.KEY_SETUP
import kotlinx.android.synthetic.main.activity_setup.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File

class SetupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)


        initializeUI()



        profile_image.setOnClickListener {
            updatePitcure()
        }

        action_save.setOnClickListener {
            save()
        }

        action_save_verifikas.setOnClickListener {
            saveWithNoHp()
        }



    }


    private fun saveWithNoHp(){
        val user = FirebaseAuth.getInstance().currentUser
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(txt_nama.text.toString())
            .build()
        user?.updateProfile(profileUpdates)?.addOnSuccessListener {
            val ref = FirebaseFirestore.getInstance().collection("users")
                .document(user.uid)
            ref.update("displayName",txt_nama.text.toString()).addOnSuccessListener {
                val shared= PreferenceManager.getDefaultSharedPreferences(this@SetupActivity)
                shared.edit {
                    putBoolean(KEY_SETUP,true)
                    commit()
                }

                Toast.makeText(this@SetupActivity,"Upadate Berhasil",Toast.LENGTH_LONG).show()
                startActivity(
                    Intent(this@SetupActivity,PhoneValidation::class.java).
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK))

            }.addOnFailureListener {
                shomethingError()
            }
        }?.addOnFailureListener {
            shomethingError()
        }
    }
    private fun save(){
        val user = FirebaseAuth.getInstance().currentUser
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(txt_nama.text.toString())
            .build()
        user?.updateProfile(profileUpdates)?.addOnSuccessListener {
            val ref = FirebaseFirestore.getInstance().collection("users")
                .document(user.uid)
            ref.update("displayName",txt_nama.text.toString()).addOnSuccessListener {
                val shared= PreferenceManager.getDefaultSharedPreferences(this@SetupActivity)
                shared.edit {
                    putBoolean(KEY_SETUP,true)
                    commit()
                }

                Toast.makeText(this@SetupActivity,"Upadate Berhasil",Toast.LENGTH_LONG).show()
                startActivity(
                    Intent(this@SetupActivity,MainActivity::class.java).
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK));
                finish()

            }.addOnFailureListener {
                shomethingError()
            }
        }?.addOnFailureListener {
            shomethingError()
        }
    }

    private fun initializeUI() {
        val user = FirebaseAuth.getInstance().currentUser
        try {
            txt_nama.setText(user?.displayName)
            Glide.with(this@SetupActivity).load(user?.photoUrl).into(profile_image)
        }catch (e : Exception){
            shomethingError()
        }
    }

    private fun updatePitcure() {
        ImagePicker.with(this@SetupActivity).cropSquare().galleryOnly().start {
                resultCode, data ->

            if (resultCode == Activity.RESULT_OK) {
                val filePath: String? = ImagePicker.getFilePath(data)
                if (filePath != null) {

                    CoroutineScope(Dispatchers.Main).launch {
                        renderAndUpload(filePath)
                    }

                }
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(
                    this@SetupActivity,
                    ImagePicker.getError(data),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this@SetupActivity,
                    "Task Cancelled",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

        }
    }


    private suspend fun renderAndUpload(filePath: String) {
        try {
            val user = FirebaseAuth.getInstance().currentUser;
            val storageRef = FirebaseStorage.getInstance().getReference("user/${user?.uid!!}")
            val file = Uri.fromFile(File(filePath))
            val uploadTask =  storageRef.putFile(file)
            uploadTask.addOnProgressListener {
            }
            val urlDownload = uploadTask.await().storage.downloadUrl.await()
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(urlDownload.toString())).build()
            user.updateProfile(profileUpdates).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val ref = FirebaseFirestore.getInstance().collection("users")
                        .document(user.uid)
                    ref.update("photoUrl",urlDownload.toString()).addOnSuccessListener {
                        try {
                            Glide.with(this@SetupActivity).load(urlDownload).into(profile_image)
                        }catch (e : Exception){

                        }


                        Toast.makeText(this@SetupActivity,"Upadate Berhasil",Toast.LENGTH_LONG).show()
                    }
                }
            }



        } catch (e: Exception) {
            shomethingError()
        }
    }



    private fun shomethingError(){
        Toast.makeText(this@SetupActivity,"Something is Error Please Try Again", Toast.LENGTH_LONG).show()
    }


}
