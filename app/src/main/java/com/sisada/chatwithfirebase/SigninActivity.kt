package com.sisada.chatwithfirebase

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.sisada.chatwithfirebase.databinding.ActivitySigninBinding

class SigninActivity : AppCompatActivity() {

    companion object{
        private const val RC_SIGN_IN = 1
    }
    private  var googleSigninClient: GoogleSignInClient? = null
    private  var fireBaseAuth : FirebaseAuth? = null
    private  lateinit var binding: ActivitySigninBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fireBaseAuth = FirebaseAuth.getInstance()
        val gso :GoogleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSigninClient = GoogleSignIn.getClient(this@SigninActivity,gso)
        binding.signInButton.setOnClickListener {
            signIn()
        }


    }

    private fun signIn(){
        val signInIntent : Intent = googleSigninClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN){
                if(resultCode == Activity.RESULT_OK)
                {
                    val task : Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
                    val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
                    fireBaseAuthWithGoogle(account!!)
                }
            }
    }

    private fun fireBaseAuthWithGoogle(account: GoogleSignInAccount){
        val credential : AuthCredential = GoogleAuthProvider.getCredential(account.idToken,null)
        fireBaseAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this@SigninActivity){
                task->
                if (task.isSuccessful){
                    startActivity(Intent(this@SigninActivity,MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@SigninActivity,"Auth Failed",Toast.LENGTH_SHORT).show()
                }
            }
    }
}