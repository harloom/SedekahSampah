package com.overdrive.sedekahsampah.ui.user

import android.app.Activity
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.overdrive.sedekahsampah.R
import com.overdrive.sedekahsampah.models.ImageStorage
import com.overdrive.sedekahsampah.models.Post
import com.overdrive.sedekahsampah.ui.home.post.Interaction
import com.overdrive.sedekahsampah.ui.home.post.InteractionEditClick
import com.overdrive.sedekahsampah.ui.home.post.PostAdapter
import com.overdrive.sedekahsampah.ui.home.post.PostInfo
import com.overdrive.sedekahsampah.ui.home.post.komentar.KomentarBottomFragment
import com.stfalcon.imageviewer.StfalconImageViewer
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_user.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File

class UserActivity : AppCompatActivity(), Interaction, InteractionEdiData {
    override fun onDataCallback(value: String, keyField: String) {
        val user = FirebaseAuth.getInstance().currentUser
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(value)
            .build()
        user?.updateProfile(profileUpdates)?.addOnSuccessListener {
                val ref = FirebaseFirestore.getInstance().collection("users")
                    .document(user.uid)
                ref.update(keyField,value).addOnSuccessListener {
                    Toast.makeText(this@UserActivity,"Upadate Berhasil",Toast.LENGTH_LONG).show()
                }.addOnFailureListener {
                    shomethingError()
                }
        }?.addOnFailureListener {
          shomethingError()
        }

    }

    override fun onItemSelected(position: Int, item: Post) {

    }

    override fun onImageSelected(image: MutableList<ImageStorage>) {
        StfalconImageViewer.Builder<ImageStorage>(this@UserActivity, image) { v, im ->
            Glide.with(v).load(im.url).into(v)
        }.show()
    }

    override fun onActionMoreSelected(position: Int, item: Post) {
        if(FirebaseAuth.getInstance().currentUser!!.isAnonymous){
            dialogAnymous()
            return
        }
        goToMorePost(item)
    }

    private fun goToMorePost(item: Post) {
        val newFragment = PostInfo(item,onThreadCallback)

        // The device is using a large layout, so show the fragment as a dialog

            newFragment.show(supportFragmentManager, "morePost")


    }



    private val onThreadCallback: InteractionEditClick = object  : InteractionEditClick {
        override fun onEditListerner(id: String, title: String?, content: String?) {
            val docRef = FirebaseFirestore.getInstance().collection("post").document(id)
            docRef.update(mapOf(
                "title" to title,
                "body" to content
            )).addOnSuccessListener {
                Toast.makeText(this@UserActivity , "Post Berubah " ,Toast.LENGTH_LONG).show()
            }.addOnFailureListener {
                shomethingError()

            }
        }

        override fun onLaporListener(item: Post) {

        }

        override fun onDeleteListener(item: Post) {
            FirebaseFirestore.getInstance().collection("post").document(item.id!!).delete()
                .addOnSuccessListener {
                    alertDialog()
                }.addOnFailureListener {
                shomethingError()

            }
        }
    }

        private fun dialogAnymous() {
        MaterialDialog(this).show {
            title(R.string.anymous)
            message(R.string.silhakanLogin)
            cornerRadius(16f)
            positiveButton(text = "Iya"){
                dismiss()


            }
            negativeButton(text = "Tidak"){
                dismiss()
            }
        }
    }

    override fun onKomentarSelected(position: Int, item: Post) {
        val bundleof = bundleOf(KomentarBottomFragment.POST_KOMENTAR to item)
        val kb = KomentarBottomFragment()
        kb.arguments = bundleof
        kb.show(supportFragmentManager,"KomentarSheet")
    }

    private lateinit var  rcvView : RecyclerView
    private lateinit var mAdaptaer : PostAdapter
    private lateinit var ci_pitchure : CircleImageView
    private lateinit var  displayName : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        initializeUi()
        updateUIUser(FirebaseAuth.getInstance().currentUser)
        initFirebaseFireStore()
    }

    private fun updateUIUser(user: FirebaseUser?) {
        user?.let {
            try {
                displayName.text = it.displayName
                if(it.phoneNumber.toString().isNotBlank()){
                    tv_value_noHp.text = it.phoneNumber
                }
                Glide.with(this@UserActivity).load(it.photoUrl).into(ci_pitchure)

            }catch (e : Exception){

            }
        }
    }

    private fun  goFormEdit(value  : String , key : String){
        val newFragment = DialogEditData(key,value,this@UserActivity)

        // The device is using a large layout, so show the fragment as a dialog

        newFragment.show(supportFragmentManager, "editValue")

    }


    private  fun initFirebaseFireStore(){
        val db = FirebaseFirestore.getInstance()
        val ref = db.collection("post").whereEqualTo("uid",FirebaseAuth.getInstance().currentUser!!.uid).orderBy("timeStamp", Query.Direction.DESCENDING).addSnapshotListener { querySnapshot, exception ->
            if(exception !=null){
                return@addSnapshotListener
            }
            val mlistPost = querySnapshot?.toObjects(Post::class.java)
            if (mlistPost != null) {
                initRecyleview(mlistPost)
            }
        }
    }

    private fun initializeUi() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar_profile)
        setSupportActionBar(toolbar)
        ci_pitchure = findViewById(R.id.profile_image)
        displayName = findViewById(R.id.tv_displayName)
        rcvView = findViewById(R.id.rcv_post)
        mAdaptaer = PostAdapter(this@UserActivity)
        rcvView.adapter = mAdaptaer


        edit_profile.setOnClickListener {
            goFormEdit( FirebaseAuth.getInstance().currentUser!!.displayName.toString(),"displayName")
        }

       actionUpdateCamera.setOnClickListener {
           updatePitcure()
       }


        ci_pitchure.setOnClickListener {
                updatePitcure()
        }
    }

    private fun updatePitcure(){
        ImagePicker.with(this@UserActivity).cropSquare().galleryOnly().start {
                resultCode, data ->

            if (resultCode == Activity.RESULT_OK) {
                val filePath: String? = ImagePicker.getFilePath(data)
                if (filePath != null) {

                    CoroutineScope(Main).launch {
                        renderAndUpload(filePath)
                    }

                }
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(
                    this@UserActivity,
                    ImagePicker.getError(data),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this@UserActivity,
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
                            Glide.with(this@UserActivity).load(urlDownload).into(ci_pitchure)
                        }catch (e : Exception){

                        }


                        Toast.makeText(this@UserActivity,"Upadate Berhasil",Toast.LENGTH_LONG).show()
                    }
                }
            }



        } catch (e: Exception) {
            shomethingError()
        }
    }


    private fun initRecyleview(mlistPost: List<Post>) {
        mAdaptaer.submitList(mlistPost)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }


        private fun shomethingError(){
            Toast.makeText(this@UserActivity,"Something is Error Please Try Again", Toast.LENGTH_LONG).show()
        }


    private fun alertDialog() {
        MaterialDialog(this@UserActivity).show {
            title(R.string.pemberitahuan)
            message(R.string.terhapus)
            cornerRadius(16f)
            positiveButton(text = "Iya") {
                initFirebaseFireStore()
                dismiss()

            }
        }
    }

}
