package com.overdrive.sedekahsampah.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment

import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

import com.overdrive.sedekahsampah.R
import com.overdrive.sedekahsampah.ui.createPost.CreateActivity
import de.hdodenhof.circleimageview.CircleImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.overdrive.sedekahsampah.models.Post
import com.overdrive.sedekahsampah.ui.home.post.Interaction
import com.overdrive.sedekahsampah.ui.home.post.PostAdapter


class HomeFragment : Fragment(), Interaction {
    override fun onItemSelected(position: Int, item: Post) {

    }

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var ci_pitchure : CircleImageView
    private lateinit var  displayName : TextView
    private lateinit var  rcvView : RecyclerView
    private lateinit var mAdaptaer : PostAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeUi(view)
        initFirebaseFireStore()
        val user  = FirebaseAuth.getInstance().currentUser
        updateUIUser(user)
        view.findViewById<FrameLayout>(R.id.edit_profile).setOnClickListener {

        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_to_createActivity->{
                startActivity(Intent(context!!,CreateActivity::class.java))
                true
            }else-> true
        }

    }
    private  fun initFirebaseFireStore(){
        val db = FirebaseFirestore.getInstance()
        val ref = db.collection("post").addSnapshotListener { querySnapshot, exception ->
            if(exception !=null){
                return@addSnapshotListener
            }
            val mlistPost = querySnapshot?.toObjects(Post::class.java)
            if (mlistPost != null) {
                initRecyleview(mlistPost)
            }
        }
    }

    private fun initRecyleview(mlistPost: MutableList<Post>) {
        mAdaptaer.submitList(mlistPost)
    }


    private fun initializeUi(view: View) {
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar_home)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        setHasOptionsMenu(true)
        ci_pitchure = view.findViewById(R.id.profile_image)
        displayName = view.findViewById(R.id.tv_displayName)
        rcvView = view.findViewById(R.id.rcv_post)
        mAdaptaer = PostAdapter(this@HomeFragment)
        rcvView.adapter = mAdaptaer
    }

    private fun updateUIUser(user: FirebaseUser?) {
        user?.let {
            try {
                if(it.isAnonymous){
                    val number = (1..100).random()
                    displayName.text = "Guest $number"
                }else{
                    displayName.text = it.displayName
                }

                Glide.with(context!!).load(it.photoUrl).placeholder(R.drawable.ic_launcher_foreground).into(ci_pitchure)
            }catch (e : Exception){

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}