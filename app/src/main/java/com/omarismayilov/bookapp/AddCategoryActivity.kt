package com.omarismayilov.bookapp

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_add_category.*
import java.util.*
import kotlin.collections.HashMap

class AddCategoryActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_category)
        auth = Firebase.auth

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        submitbutton.setOnClickListener {
            validateData()
        }

    }

    private var category = ""

    fun validateData(){
        category = categoryEt.text.toString().trim()
        if(TextUtils.isEmpty(category)){
            categoryEt.error = "Fill here"
        }else{
            addCategoryFb()
        }

    }



    private fun addCategoryFb() {
        progressDialog.show()
        progressDialog.setMessage("Saving...")

        val timestamp = System.currentTimeMillis()
        val hashMap: HashMap<String,Any?> = HashMap()
        hashMap["id"] = "$timestamp"
        hashMap["category"] = category.capitalize()
        hashMap["uid"] = auth.uid
        hashMap["timestamp"] = timestamp

        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.child(timestamp.toString()).setValue(hashMap).addOnSuccessListener {
            progressDialog.dismiss()
            Toast.makeText(applicationContext,"Succesfully added..",Toast.LENGTH_SHORT).show()
            categoryEt.setText("")
        }.addOnFailureListener{
            progressDialog.dismiss()
            Toast.makeText(applicationContext,it.localizedMessage,Toast.LENGTH_SHORT).show()
        }

    }
}