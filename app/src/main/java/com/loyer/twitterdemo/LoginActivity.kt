package com.loyer.twitterdemo

import android.opengl.Visibility
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.util.Log

import android.view.View
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_register.*

class LoginActivity : AppCompatActivity() {
    private var manager: FragmentManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        manager = supportFragmentManager
    }


    fun txtRegister(view: View){
            var registerFragment = RegisterFragment()
        var transaction: FragmentTransaction = manager!!.beginTransaction()
        transaction.add(R.id.login_activity,registerFragment,"register")
        transaction.commit()
        btnLogin.visibility = View.GONE
    }
    fun register(view: View){
            var remove : RegisterFragment = manager!!.findFragmentByTag("register") as RegisterFragment
       var transaction: FragmentTransaction = manager!!.beginTransaction()
        transaction.remove(remove)
        transaction.commit()
        btnLogin.visibility = View.VISIBLE

        var string: String = edtName.text.toString()
        Log.d("Register",string)
    }
}
