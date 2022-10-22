package com.omarismayilov.bookapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.internal.model.CrashlyticsReport.Session.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class LoginActivity : AppCompatActivity() {
    private lateinit var auth:FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = Firebase.auth


        progressDialog=ProgressDialog(this)
        progressDialog.setTitle("Please wait..")
        progressDialog.setCanceledOnTouchOutside(false)

        noaccounttv.setOnClickListener {
            startActivity(Intent(this@LoginActivity,RegisterActivity::class.java))
        }
        loginbutton.setOnClickListener {
            validate()
        }
    }
    private var email = ""
    private var password = ""

    private fun validate() {
        email = emailet2.text.toString().trim()
        password = passwordet2.text.toString().trim()

        if (TextUtils.isEmpty(email)) {
            emailet2.error = "Fill here"
        }
        else if (TextUtils.isEmpty(password)) {
            passwordet2.error = "Fill here"
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(applicationContext,"Invalid email pattern", Toast.LENGTH_SHORT).show()
        }
        else {
            logIn()
        }
    }




    private fun logIn() {
        progressDialog.setMessage("Logging in...")
        progressDialog.show()

        auth.signInWithEmailAndPassword(email,password).addOnSuccessListener {
            checkUser()
        }.addOnFailureListener {
            progressDialog.dismiss()
            Toast.makeText(applicationContext,it.localizedMessage, Toast.LENGTH_SHORT).show()
        }



    }

    private fun checkUser() {
        progressDialog.setMessage("Checking user...")

        val firebaseUser = auth.currentUser

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        if (firebaseUser != null) {
            ref.child(firebaseUser.uid).addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    progressDialog.dismiss()
                    val userType = snapshot.child("userType").value
                    if (userType=="admin"){
                        startActivity(Intent(this@LoginActivity,AdminActivity::class.java))
                        finish()
                    }
                    else if(userType=="user"){
                        startActivity(Intent(this@LoginActivity,UserActivity::class.java))
                        finish()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }


            })
        }

    }


}