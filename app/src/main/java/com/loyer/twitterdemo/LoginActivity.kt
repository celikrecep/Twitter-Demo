package com.loyer.twitterdemo


import android.app.AlertDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth


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
