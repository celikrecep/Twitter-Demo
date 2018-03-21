package com.loyer.twitterdemo

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.text.TextUtils

import android.util.Log

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_login.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class LoginActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var mEmail: EditText? = null
    private var mPassword: EditText? = null
    private var TAG = "Login"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()
        mEmail = findViewById(R.id.edtEmail)
        mPassword = findViewById(R.id.edtPassword)

    }

    fun loginInExistingUser(view: View){

        attemptLogin()
    }


    private fun attemptLogin(){
        val email: String = mEmail!!.text!!.toString()
        val pass: String = mPassword!!.text!!.toString()


        if(email == "" || pass == "" ) return
        Toast.makeText(this,"Login in progress...",Toast.LENGTH_SHORT).show()

        mAuth!!.signInWithEmailAndPassword(email,pass).addOnCompleteListener { task ->
            Log.d(TAG,"Login is succesfull " + task.isSuccessful)

            if(!task.isSuccessful){
                Log.d(TAG,"Same problem :"+ task.exception)
                showErrorDialog("wrong email or password")
            }else{
                val intent = Intent(this@LoginActivity,MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
    fun txtRegister(view: View){
       val intent = Intent(this@LoginActivity,RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }


    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this)
                .setTitle("Oops!")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
    }
}
