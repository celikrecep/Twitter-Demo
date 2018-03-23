package com.loyer.twitterdemo


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText

import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.loyer.twitterdemo.model.Post
import com.loyer.twitterdemo.model.PostInfo
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val IMAGE_PICK_CODE = 101
    private var list = ArrayList<Post>()
    private var mTweetList: ListView? = null
    private var adapter: TweetListAdapter? = null
    private var TAG = "Main"
    private var downloadURL:String?=""

    private var mEditThink: EditText? = null
    private var mPostInfo: PostInfo? = null
    private var mAuth: FirebaseAuth? = null
    private var mDatabaseReference: DatabaseReference? = null
    private var mStorage: FirebaseStorage? = null
    private var imagePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()
        mStorage = FirebaseStorage.getInstance()
        mDatabaseReference = FirebaseDatabase.getInstance().reference

        mTweetList = findViewById(R.id.lstTweets)
        mEditThink = findViewById(R.id.edtThink)

        //for adapter testing
        list.add(Post("0","him","url","loyer"))
        list.add(Post("0","him","url","nLoyer"))

        adapter = TweetListAdapter(list,this)
        mTweetList!!.adapter = adapter

    }

    //when the send button  pressed
    fun sendPost(view:View){
        Log.d(TAG,"send button clicked!")
        val think: String = mEditThink!!.text.toString()
        saveImageInFireBase(BitmapFactory.decodeFile(imagePath),think)
        Log.d(TAG,downloadURL)
        mEditThink!!.setText("")
    }

    //when the select button pressed
    fun selectImage(view: View){
        val intent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent,IMAGE_PICK_CODE)

    }

    //select a image from phone
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == IMAGE_PICK_CODE && data != null && resultCode == Activity.RESULT_OK  ){
            //get image
            val selectedImage = data.data
            val filePath = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = contentResolver.query(selectedImage,filePath,null,null,null)
            cursor.moveToFirst()
            val pathIndex = cursor.getColumnIndex(filePath[0])
            val imgPath = cursor.getString(pathIndex)
            cursor.close()
            imagePath = imgPath

        }
    }

    //upload image to FireBase
    private fun saveImageInFireBase(bitmap: Bitmap?, think: String){
        val storageRef = mStorage!!.getReferenceFromUrl("gs://twitter-demo-23ea1.appspot.com")


        val imagePath = generateId(mAuth!!.currentUser,true)
        val imageRef = storageRef.child("imagePosts/$imagePath")

        val stream = ByteArrayOutputStream()
        //image quality
        bitmap!!.compress(Bitmap.CompressFormat.JPEG,100,stream)
        val data = stream.toByteArray()


        val uploadTask = imageRef.putBytes(data)

        uploadTask.addOnSuccessListener {taskSnapshot ->
            //image url
            downloadURL = taskSnapshot.downloadUrl!!.toString()

            val uID: String = mAuth!!.currentUser!!.uid
            mPostInfo = PostInfo(uID,think,downloadURL)
            //push post to Database
            mDatabaseReference!!.child("posts").child(uID)
                    .child(generateId(mAuth!!.currentUser,false))
                    .setValue(mPostInfo)
        }

    }
    //created id of image
    @SuppressLint("SimpleDateFormat")
    private fun generateId(currentUser: FirebaseUser?,isImage: Boolean): String{
        //we gonna use time for create an id of image
        val dateFormat = SimpleDateFormat("ddMMyyHHmmss")
        val dataObj = Date()

        return if(isImage){
            currentUser!!.email + dateFormat.format(dataObj) + ".jpg"
        }else{ //we only get email address of before @ mark
            val split = currentUser!!.email!!.split("@")[0]
            Log.d(TAG,split)
            val uID : String = split + dateFormat.format(dataObj)
            uID
        }
    }


}
