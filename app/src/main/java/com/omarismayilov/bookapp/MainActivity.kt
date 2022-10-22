package com.omarismayilov.bookapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loginbutton.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))

        }

        skipbutton.setOnClickListener {
            startActivity(Intent(this,UserActivity::class.java))

        }



    }





}