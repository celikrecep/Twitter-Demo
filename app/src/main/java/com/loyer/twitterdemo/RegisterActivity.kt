package com.loyer.twitterdemo

import android.Manifest
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
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity() {
    private val TAG = "Register"
    private val PERMISSIONS_REQUEST = 101
    private val IMAGE_PICK_CODE = 101

    private var mAuth: FirebaseAuth? = null
    private var mStorage: FirebaseStorage? = null
    private var mDatabaseReference: DatabaseReference? = null

    private var user: User? = null
    private var imagePerson: ImageView? = null
    private var edtName: EditText? = null
    private var registerEmail: EditText? = null
    private var registerPass: EditText? = null
    private var registerConfirm: EditText? = null
    private var isSelected = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()
        mStorage = FirebaseStorage.getInstance()
        mDatabaseReference = FirebaseDatabase.getInstance().reference

        edtName = findViewById(R.id.edtName)
        registerEmail = findViewById(R.id.edtRegisterEmail)
        registerPass = findViewById(R.id.edtRegisterPass)
        registerConfirm = findViewById(R.id.edtRegisterConfirm)
        imagePerson = findViewById(R.id.imgPP)
    }

    fun register(view: View){
            attemptRegistration()
        Log.d("Register","Button clicked!")
    }

    fun loadImage(view: View){
        setupPermission()

        var intent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent,IMAGE_PICK_CODE)
    }

    //select a image from phone
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == IMAGE_PICK_CODE && data != null  ){
            //get image
            val selectedImage = data.data
            val filePath = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = contentResolver.query(selectedImage,filePath,null,null,null)
            cursor.moveToFirst()
            val pathIndex = cursor.getColumnIndex(filePath[0])
            val imagePath = cursor.getString(pathIndex)
            cursor.close()
            imagePerson!!.setImageBitmap(BitmapFactory.decodeFile(imagePath))
            isSelected = true

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSIONS_REQUEST ->{
                if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Log.i(TAG, "Permission has been denied by user")
                }else {
                    Log.i(TAG, "Permission has been granted by user")
                }
            }
        }
    }

    //upload image to FireBase
    private fun saveImageInFireBase(currentUser: FirebaseUser?){
        val storageRef = mStorage!!.getReferenceFromUrl("gs://twitter-demo-23ea1.appspot.com")
        //we gonna use time for create an id of image
        val dateFormat = SimpleDateFormat("ddMMyyHHmmss")
        val dataObj = Date()
        //created id of image
        val imagePath = currentUser!!.uid + dateFormat.format(dataObj) + ".jpg"
        val imageRef = storageRef.child("images/$imagePath")

            val drawable = imagePerson!!.drawable as BitmapDrawable
            val bitmap = drawable.bitmap
            val stream = ByteArrayOutputStream()
        //image quality
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
            val data = stream.toByteArray()


            val uploadTask = imageRef.putBytes(data)

            uploadTask.addOnSuccessListener {taskSnapshot ->

                val downloadUrl = taskSnapshot.downloadUrl!!.toString()
                val id = currentUser!!.uid
                val name: String? = edtName!!.text.toString()
                user = User(id,name,downloadUrl)
                mDatabaseReference!!.child("users").child(id).setValue(user)

            }


    }


    private fun makeRequest(){
        ActivityCompat.requestPermissions(this
                , arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                ,PERMISSIONS_REQUEST)

    }

    private fun setupPermission(){
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        if(permission != PackageManager.PERMISSION_GRANTED)
        {
            Log.i(TAG,"Permission Denied")
            makeRequest()
        }
    }


    private fun attemptRegistration(){
        registerEmail!!.error = null
        registerPass!!.error = null

        var email: String = registerEmail!!.text.toString()
        var password: String = registerPass!!.text.toString()
        var cancel = false
        var focusView: View? = null

        if (TextUtils.isEmpty(email)) {
            registerEmail?.error = getString(R.string.error_field_required)
            focusView = registerEmail
            cancel = true
        }
        if (!TextUtils.isEmpty(password) && !isValidPassword(password)) {
            registerPass?.error = getString(R.string.error_invalid_password)
            focusView = registerPass
            cancel = true

        }else if (!isEmailValid(email)) {
            registerEmail?.error = getString(R.string.error_invalid_email)
            focusView = registerEmail
            cancel = true
        }
        if (cancel) {
            focusView?.requestFocus()
        } else {
            createFireBaseUser()

        }
    }

    //password check
    private fun isValidPassword(password: String): Boolean {
        val confirmPassword = registerConfirm?.text!!.toString()
        return confirmPassword.equals(password) && password.length > 6
    }
    //email check
    private fun isEmailValid(email: String): Boolean {
        return email.contains("@")
    }

    private fun createFireBaseUser() {
        val email: String = registerEmail?.text!!.toString()
        val password: String = registerPass?.text!!.toString()

        mAuth!!.createUserWithEmailAndPassword(email,password)!!.addOnCompleteListener {
            task ->
            if(!task.isSuccessful){
                Log.d(TAG, "user creation failed")
                showErrorDialog("Registration attempt failed")
            } else {
                Toast.makeText(this, "Kayıt başarılı", Toast.LENGTH_SHORT).show()
            }
        }

        signInAndSignOut()
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this)
                .setTitle("Oops!")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
    }

    private fun signInAndSignOut(){
        val email: String = registerEmail!!.text.toString()
        val pass: String = registerPass!!.text.toString()

        mAuth!!.signInWithEmailAndPassword(email,pass).addOnCompleteListener {
           saveImageInFireBase(mAuth!!.currentUser)
        }

    }
}
