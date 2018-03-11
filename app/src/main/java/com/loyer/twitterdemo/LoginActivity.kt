package com.loyer.twitterdemo

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.RoundedBitmapDrawable
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.util.Log

import android.view.View
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_register.*

class LoginActivity : AppCompatActivity() {
    private var manager: FragmentManager? = null
    private val PERMISSIONS_REQUEST = 101
    private val IMAGE_PICK_CODE = 101


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

    fun loadImage(view: View){
        setupPermission()

        var intent : Intent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent,IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == IMAGE_PICK_CODE && data != null  ){
            val selectedImage = data.data
            val filePath = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = contentResolver.query(selectedImage,filePath,null,null,null)
            cursor.moveToFirst()
            val pathIndex = cursor.getColumnIndex(filePath[0])
            val imagePath = cursor.getString(pathIndex)
            cursor.close()

            imgPP.setImageBitmap(BitmapFactory.decodeFile(imagePath))

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSIONS_REQUEST ->{
                if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Log.i("Register", "Permission has been denied by user")
                }else {
                    Log.i("Register", "Permission has been granted by user")
                }
            }
        }
    }


   private fun makeRequest(){
       ActivityCompat.requestPermissions(this
               , arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
               ,PERMISSIONS_REQUEST)

    }

    private fun setupPermission(){
        val permission = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)
        if(permission != PackageManager.PERMISSION_GRANTED)
        {
            Log.i("Register","Permission Denied")
            makeRequest()
        }
    }

}
