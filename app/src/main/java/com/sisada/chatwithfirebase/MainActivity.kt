package com.sisada.chatwithfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.sisada.chatwithfirebase.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(),GoogleApiClient.OnConnectionFailedListener {

    private  var userName: String? = null
    private  var userPhotoUrl: String? = null
    private  var fireBaseAuth: FirebaseAuth? = null
    private var fireBaseUser: FirebaseUser? = null
    private  var googleApiClient: GoogleApiClient? = null
    private  lateinit var binding : ActivityMainBinding

    companion object{
        const val TAG = "MainActicity"
        const val ANONYMOUS = "anonymous"
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        googleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this,this)
            .addApi(Auth.GOOGLE_SIGN_IN_API)
            .build()

        userName = ANONYMOUS
        fireBaseAuth = FirebaseAuth.getInstance()
        fireBaseUser = fireBaseAuth!!.currentUser
        if (fireBaseUser == null){
            val intent = Intent(this@MainActivity, SigninActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            userName = fireBaseUser!!.displayName
            if(fireBaseUser!!.photoUrl != null){
                userPhotoUrl = fireBaseUser!!.photoUrl!!.toString()
            }
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.d("MainActivity","onConnectionFailed + $p0")
    }

}