package com.overdrive.sedekahsampah.ui.lainnya

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.overdrive.sedekahsampah.R
import com.overdrive.sedekahsampah.ui.LoginActivity

class MoreFragment : Fragment() {

    private lateinit var moreViewModel: MoreViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        moreViewModel =
            ViewModelProviders.of(this).get(MoreViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_more, container, false)
        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<LinearLayout>(R.id.action_logOut).setOnClickListener {
            it.isEnabled = false
            logOut(view)
        }
    }


    private fun logOut(view: View) {
        try {
            FirebaseAuth.getInstance().signOut()
            logOutGoogle()
            startActivity(Intent(context!! , LoginActivity::class.java).addFlags(
              Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            ))
        }catch (e : Exception){
            Toast.makeText(context!!,"Something is Error Please Try Again",Toast.LENGTH_LONG).show()
            view.findViewById<LinearLayout>(R.id.action_logOut).isEnabled = true
        }



    }

    private fun logOutGoogle(){
        val acc = GoogleSignIn.getLastSignedInAccount(context!!) ?: return
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val mGoogleSignInClient = GoogleSignIn.getClient(context!!,gso)
        mGoogleSignInClient.signOut()

    }



}