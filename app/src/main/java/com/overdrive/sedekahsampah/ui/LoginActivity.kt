package com.overdrive.sedekahsampah.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.overdrive.sedekahsampah.MainActivity
import com.overdrive.sedekahsampah.R
import com.overdrive.sedekahsampah.SetupActivity
import com.overdrive.sedekahsampah.models.User
import com.overdrive.sedekahsampah.utils.KEY_SETUP
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleButton : SignInButton

    companion object{
        const val  RC_SIGN_IN = 1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initConfigure()
        if(auth.currentUser !=null){
            return goToMain()
        }
        googleButton = findViewById<SignInButton>(R.id.sign_in_button)
        googleButton.setOnClickListener {
            signIn()
        }
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
                enableButton()
            }
        }
    }
    private fun signIn() {
        println("SigIN")
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
        val ref = FirebaseFirestore.getInstance().collection("users")
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
        googleButton.isEnabled =true
        sign_in_guest.isEnabled =true
    }

    fun actionGuest(view: View) {
        view.isEnabled =false
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


    private fun cekSetup() : Boolean{
        val shared= PreferenceManager.getDefaultSharedPreferences(this@LoginActivity)
        return shared.getBoolean(KEY_SETUP,false)
    }



    private fun goToMain(){

        if(cekSetup()){
            startActivity(Intent(this@LoginActivity,MainActivity::class.java).
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK));
            finish()
        }else{
            startActivity(Intent(this@LoginActivity,SetupActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK))
            finish()
        }


    }
}
