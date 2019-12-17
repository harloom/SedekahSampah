package com.overdrive.sedekahsampah.ui.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.overdrive.sedekahsampah.R
import com.overdrive.sedekahsampah.models.ImageStorage
import com.overdrive.sedekahsampah.models.Post
import com.overdrive.sedekahsampah.ui.home.post.Interaction
import com.overdrive.sedekahsampah.ui.home.post.PostAdapter
import de.hdodenhof.circleimageview.CircleImageView

class UserActivity : AppCompatActivity(), Interaction {
    override fun onItemSelected(position: Int, item: Post) {

    }

    override fun onImageSelected(image: MutableList<ImageStorage>) {

    }

    override fun onActionMoreSelected(position: Int, item: Post) {

    }

    override fun onKomentarSelected(position: Int, item: Post) {

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
                Glide.with(this@UserActivity).load(it.photoUrl).into(ci_pitchure)

            }catch (e : Exception){

            }
        }
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
    }


    private fun initRecyleview(mlistPost: List<Post>) {
        mAdaptaer.submitList(mlistPost)
    }

}
