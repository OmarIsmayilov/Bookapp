package com.omarismayilov.bookapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_admin.*
import kotlinx.android.synthetic.main.activity_admin.logoutbutton
import kotlinx.android.synthetic.main.activity_user.*

class UserActivity : AppCompatActivity() {
    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        auth = Firebase.auth
        checkUser()

        logoutbutton.setOnClickListener{
            auth.signOut()
            startActivity(Intent(this@UserActivity,MainActivity::class.java))
            finish()
        }

    }


    @SuppressLint("SetTextI18n")
    fun checkUser(){
        val firebaseUser = auth.currentUser
        if(firebaseUser==null){
            useremailtv.text = "Not logged in"
        } else{
            val email = firebaseUser.email
            useremailtv.text = email
        }


    }


}
