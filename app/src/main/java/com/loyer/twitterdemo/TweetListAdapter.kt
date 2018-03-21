package com.loyer.twitterdemo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.loyer.twitterdemo.model.Post

/**
 * Created by loyer on 21.03.2018.
 */
/**
 * we gonna edit it visual
 */
class TweetListAdapter(private var listTweet: ArrayList<Post>,
                       private var context: Context): BaseAdapter() {

    //TODO update with database references

    @SuppressLint("InflateParams")
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View? {

        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val tweet = listTweet[p0]
            // will add add_post layout to list according to incoming ID
        return if(tweet.tweetPersonUID!! == "add"){
            val view = layoutInflater.inflate(R.layout.add_post,null)
            view
        }else { //will add tweets layout to list according to incoming ID
            val view = layoutInflater.inflate(R.layout.tweets,null)
            view
        }

    }

    override fun getItem(p0: Int): Any {
        return  listTweet[0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }
    //size
    override fun getCount(): Int {
        return listTweet.size
    }

}