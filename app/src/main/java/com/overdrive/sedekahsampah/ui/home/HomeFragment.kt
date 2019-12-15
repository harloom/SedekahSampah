package com.overdrive.sedekahsampah.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
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
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


import com.overdrive.sedekahsampah.models.ImageStorage
import com.overdrive.sedekahsampah.models.Post
import com.overdrive.sedekahsampah.ui.LoginActivity
import com.overdrive.sedekahsampah.ui.home.post.Interaction
import com.overdrive.sedekahsampah.ui.home.post.InteractionEditClick
import com.overdrive.sedekahsampah.ui.home.post.PostAdapter
import com.overdrive.sedekahsampah.ui.home.post.PostInfo
import com.overdrive.sedekahsampah.ui.home.post.komentar.KomentarBottomFragment
import com.overdrive.sedekahsampah.ui.home.post.komentar.KomentarBottomFragment.Companion.POST_KOMENTAR
import com.stfalcon.imageviewer.StfalconImageViewer


class HomeFragment : Fragment(), Interaction {
    override fun onKomentarSelected(position: Int, item: Post) {
        val bundleof = bundleOf(POST_KOMENTAR to item)
        val kb =KomentarBottomFragment()
        kb.arguments = bundleof
        kb.show(childFragmentManager,"KomentarSheet")
    }

    override fun onActionMoreSelected(position: Int, item: Post) {
        if(FirebaseAuth.getInstance().currentUser!!.isAnonymous){
            dialogAnymous(context!!)
            return
        }
        goToMorePost(item)
    }

    override fun onImageSelected(image: MutableList<ImageStorage>) {
        StfalconImageViewer.Builder<ImageStorage>(context!!, image) { v, im ->
            Glide.with(v).load(im.url).into(v)
        }.show()
    }

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

                if(FirebaseAuth.getInstance().currentUser!!.isAnonymous){
                    dialogAnymous(context!!)
                    return true
                }
                startActivity(Intent(context!!,CreateActivity::class.java))
                true
            }else-> true
        }

    }
    private  fun initFirebaseFireStore(){
        val db = FirebaseFirestore.getInstance()
        val ref = db.collection("post").orderBy("timeStamp", Query.Direction.DESCENDING).addSnapshotListener { querySnapshot, exception ->
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


    private fun goToMorePost(item : Post){
        val newFragment = PostInfo(item,onThreadCallback)

        // The device is using a large layout, so show the fragment as a dialog
        if (fragmentManager != null) {
            newFragment.show(childFragmentManager, "morePost")
        }


    }

    private val onThreadCallback: InteractionEditClick = object  : InteractionEditClick {
        override fun onDeleteListener(item: Post) {
            FirebaseFirestore.getInstance().collection("post").document(item.id!!).delete().addOnSuccessListener {
                alertDialog(context!!)
            }.addOnFailureListener {
                shomethingError()

            }
        }

        override fun onEditListerner(id: String, title: String?, content: String?) {
            val docRef = FirebaseFirestore.getInstance().collection("post").document(id)
             docRef.update(mapOf(
                 "title" to title,
                 "body" to content
             )).addOnSuccessListener {
                 Toast.makeText(context , "Post Berubah " ,Toast.LENGTH_LONG).show()
            }.addOnFailureListener {
                shomethingError()

            }
        }

        override fun onLaporListener(item: Post) {
            Toast.makeText(context , "onLaporListener " ,Toast.LENGTH_LONG).show()
        }

    }

    private fun dialogAnymous(contex : Context){
        MaterialDialog(contex).show {
            title(R.string.anymous)
            message(R.string.silhakanLogin)
            cornerRadius(16f)
            positiveButton(text = "Iya"){
                dismiss()
                goLogin()

            }
            negativeButton(text = "Tidak"){
                dismiss()
            }
        }
    }

    private fun alertDialog(contex: Context) {
        MaterialDialog(contex).show {
            title(R.string.pemberitahuan)
            message(R.string.terhapus)
            cornerRadius(16f)
            positiveButton(text = "Iya") {
                dismiss()

            }
        }
    }

    private fun goLogin() {
        try {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(context!! , LoginActivity::class.java).addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            ))
            activity?.finish()
        }catch (e : Exception){
            shomethingError()

        }

    }

    private fun shomethingError(){
        Toast.makeText(context!!,"Something is Error Please Try Again",Toast.LENGTH_LONG).show()
    }
}