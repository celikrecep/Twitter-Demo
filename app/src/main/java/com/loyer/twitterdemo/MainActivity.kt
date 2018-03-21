package com.loyer.twitterdemo


import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import android.widget.ListView
import com.loyer.twitterdemo.model.Post

class MainActivity : AppCompatActivity() {

    private var list = ArrayList<Post>()
    private var mTweetList : ListView? = null
    private var adapter: TweetListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mTweetList = findViewById(R.id.lstTweets)

        //for adapter testing
        list.add(Post("0","him","url","add"))
        list.add(Post("0","him","url","loyer"))
        list.add(Post("0","him","url","nLoyer"))

         adapter = TweetListAdapter(list,this)
         mTweetList!!.adapter = adapter


    }

}
