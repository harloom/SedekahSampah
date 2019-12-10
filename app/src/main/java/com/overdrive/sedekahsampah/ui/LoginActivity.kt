package com.overdrive.sedekahsampah.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.overdrive.sedekahsampah.MainActivity
import com.overdrive.sedekahsampah.R
import com.overdrive.sedekahsampah.models.User
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    companion object{
        const val  RC_SIGN_IN = 1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initConfigure()
    }


    private fun initConfigure(){
        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this@LoginActivity,gso)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            task.addOnSuccessListener {
                firebaseAuthWithGoogle(it)
            }.addOnFailureListener {
                Toast.makeText(this, "Google sign in failed:(", Toast.LENGTH_LONG).show()
                sign_in_button.isEnabled =true
            }
        }
    }
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    saveToFirebase(user)
                } else {
                    // If sign in fails, display a message to the user.
                    sign_in_button.isEnabled =true
                    saveToFirebase(null)
                }


            }
    }

    private fun saveToFirebase(user: FirebaseUser?) {
        val ref = FirebaseFirestore.getInstance().collection("user")
        val userDoc = User(user!!.uid,
            user.displayName,user.photoUrl.toString(),user.metadata!!.lastSignInTimestamp,user.phoneNumber)
        ref.document(user.uid).set(userDoc).addOnSuccessListener {
            enableButton()
            goToMain()
        }.addOnFailureListener {
            enableButton()
        }
    }

    private fun enableButton(){
        sign_in_button.isEnabled =true
        sign_in_guest.isEnabled =true
    }

    fun actionGuest(view: View) {
        sign_in_guest.isEnabled =false
        auth.signInAnonymously()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information )
                    val user = auth.currentUser
                    saveToFirebase(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()

                }


            }

    }
    fun actionGoogle(view: View) {
        sign_in_button.isEnabled =false
        signIn()

    }

    private fun goToMain(){
        startActivity(Intent(this@LoginActivity,MainActivity::class.java).
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}