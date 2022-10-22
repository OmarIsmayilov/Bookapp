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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth:FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = Firebase.auth

        //progress dialog
        progressDialog=ProgressDialog(this)
        progressDialog.setTitle("Please wait..")
        progressDialog.setCanceledOnTouchOutside(false)


        registerbutton.setOnClickListener {
            validate()
        }

    }

    private var email = ""
    private var name = ""
    private var password = ""

    private fun validate(){
        //1)
        name = nameet.text.toString().trim()
        email = emailet.text.toString().trim()
        password = passwordet.text.toString().trim()
        val cpassword = passwordcfet.text.toString().trim()

        //2) info check

        if(TextUtils.isEmpty(name)){
            nameet.error = "Fill here"
        }
        else if(TextUtils.isEmpty(email)){
            emailet.error = "Fill here"
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(applicationContext,"Invalid email pattern",Toast.LENGTH_SHORT).show()
        }
        else if(TextUtils.isEmpty(password)){
            passwordet.error = "Fill here"
        }
        else if(TextUtils.isEmpty(cpassword)){
            passwordcfet.error = "Fill here"
        }
        else if(password!=cpassword){
            Toast.makeText(applicationContext,"Password doesn't match...",Toast.LENGTH_SHORT).show()
        }
        else{
            createUser()
        }


    }

    private fun createUser(){

        //3)

        progressDialog.setMessage("Creating account...")
        progressDialog.show()
        auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener {
            updateUserInfo()

        }.addOnFailureListener{
            progressDialog.dismiss()
            Toast.makeText(applicationContext,it.localizedMessage,Toast.LENGTH_SHORT).show()
        }

    }

    private fun updateUserInfo() {
        //4)
        progressDialog.setMessage("Saving user info...")

        val timestamp = System.currentTimeMillis()

        val uid = auth.uid

        val hashMap:HashMap<String,Any?> = HashMap()

        hashMap["uid"] = uid
        hashMap["name"] = name
        hashMap["email"] = email
        hashMap["profileImage"] = ""
        hashMap["userType"] = "user"
        hashMap["timestamp"] = timestamp

        //save in database
        val ref = FirebaseDatabase.getInstance().getReference("Users")

        ref.child(uid!!).setValue(hashMap).addOnSuccessListener {
            progressDialog.dismiss()
            Toast.makeText(applicationContext,"Account created...",Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@RegisterActivity,UserActivity::class.java))
            finish()

        }.addOnFailureListener {
            progressDialog.dismiss()
            Toast.makeText(applicationContext,it.localizedMessage,Toast.LENGTH_SHORT).show()

        }

    }


}