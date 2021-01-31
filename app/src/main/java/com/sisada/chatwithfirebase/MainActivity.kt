package com.sisada.chatwithfirebase

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import com.sisada.chatwithfirebase.databinding.ActivityMainBinding
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : AppCompatActivity(),GoogleApiClient.OnConnectionFailedListener {

    private  var userName: String? = null
    private  var userPhotoUrl: String? = null
    private  var fireBaseAuth: FirebaseAuth? = null
    private var fireBaseUser: FirebaseUser? = null
    private  var googleApiClient: GoogleApiClient? = null
    private  lateinit var binding : ActivityMainBinding

    private  var firebaseDatabaseReferance: DatabaseReference? = null
    private var firebaseAdapter : FirebaseRecyclerAdapter<Message,MessageViewHolder>? = null

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

    class MessageViewHolder(v: View) : RecyclerView.ViewHolder(v){
        lateinit var  message:Message
        var messageTextView : TextView
        var nameTextView: TextView
        var userImage : CircleImageView
        var messageImageView : ImageView
        init {

            messageTextView = itemView.findViewById(R.id.message_text_view)
            nameTextView = itemView.findViewById(R.id.name_text_view)
            userImage = itemView.findViewById(R.id.messenger_image_view)
            messageImageView = itemView.findViewById(R.id.message_image_view)
        }

        fun bind(message : Message)
        {
            this.message = message
            if (message.text != null){
                messageTextView.text = message.text
                messageTextView.visibility = View.VISIBLE
                messageImageView.visibility = View.GONE


            } else if (message.imageUrl != null) {
                messageTextView.visibility = View.GONE
                messageImageView.visibility = View.VISIBLE
                val imageUrl: String? = message.imageUrl
                if (imageUrl!!.startsWith("gs://")){
                    val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
                    storageReference.downloadUrl.addOnCompleteListener{
                        task ->
                        if (task.isSuccessful){
                            val downloadUrl:String = task.result!!.toString()
                            Glide.with(messageImageView.context)
                                .load(downloadUrl)
                                .into(messageImageView)
                        }else{
                            Log.e(TAG, "download not success ${task.exception}")
                        }
                    }
                } else {
                    Glide.with(messageImageView.context)
                        .load(Uri.parse(message.imageUrl))
                        .into(messageImageView)
                }
            }

            nameTextView.text = message.name
            if(message.photoUrl == null){
                userImage.setImageDrawable(ContextCompat.getDrawable(userImage.context,R.drawable.ic_account_circle_black_36dp))
            } else {
                Glide.with(userImage.context)
                        .load(Uri.parse(message.photoUrl))
                        .into(userImage)
            }
        }
    }
}