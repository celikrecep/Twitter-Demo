package com.loyer.twitterdemo

import android.annotation.SuppressLint
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

    //TODO update with database references and viewHolder pattern

    @SuppressLint("InflateParams")
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View? {

        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


         //will add tweets layout to list
        return layoutInflater.inflate(R.layout.tweets,null)


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