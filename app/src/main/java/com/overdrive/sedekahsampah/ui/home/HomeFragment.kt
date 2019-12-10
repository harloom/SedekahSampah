package com.overdrive.sedekahsampah.ui.home

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.overdrive.sedekahsampah.MainActivity

import com.overdrive.sedekahsampah.R
import com.overdrive.sedekahsampah.ui.createPost.CreateActivity
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_home.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar


class HomeFragment : Fragment() {
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var ci_pitchure : CircleImageView
    private lateinit var  displayName : TextView

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

        initializeUi(view);
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


    private fun initializeUi(view: View) {
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar_home)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        setHasOptionsMenu(true)
        ci_pitchure = view.findViewById(R.id.profile_image)
        displayName = view.findViewById(R.id.tv_displayName)
    }

    private fun updateUIUser(user: FirebaseUser?) {
        user?.let {
            try {
                Glide.with(context!!).load(it.photoUrl).placeholder(ColorDrawable(Color.LTGRAY)).into(ci_pitchure)
                displayName.text = it.displayName
            }catch (e : Exception){

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}