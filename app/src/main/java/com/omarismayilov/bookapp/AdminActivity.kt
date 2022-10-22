package com.omarismayilov.bookapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_admin.*

class AdminActivity : AppCompatActivity() {

    private lateinit var auth:FirebaseAuth

    private lateinit var categoryArrayList : ArrayList<ModelCategory>

    private lateinit var  adapterCategory: AdabterCategory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        auth = Firebase.auth
        checkUser()
        loadCategories()

        searchEt.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {


            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                try {
                    adapterCategory.filter.filter(s)
                }catch (_:Exception){

                }

            }

            override fun afterTextChanged(p0: Editable?) {
                TODO("Not yet implemented")
            }


        })


        logoutbutton.setOnClickListener{
            auth.signOut()
            checkUser()
        }

        addbutton.setOnClickListener {
            startActivity(Intent(this@AdminActivity,AddCategoryActivity::class.java))
        }

        addPdfFab.setOnClickListener{
            startActivity(Intent(this@AdminActivity,PdfAddActivity::class.java))
        }

    }

    private fun loadCategories() {
        categoryArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    categoryArrayList.clear()

                    for (ds in snapshot.children){
                        val model = ds.getValue(ModelCategory::class.java)
                        categoryArrayList.add(model!!)
                    }
                    adapterCategory = AdabterCategory(this@AdminActivity,categoryArrayList)
                    categoriesRv.adapter = adapterCategory

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

    }


    fun checkUser(){
        val firebaseUser = auth.currentUser
        if(firebaseUser==null){
            startActivity(Intent(this@AdminActivity,MainActivity::class.java))
            finish()
        } else{
            val email = firebaseUser.email
            adminemailtv.text = email
        }


    }







}








