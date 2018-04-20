package com.loyer.twitterdemo.model

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.loyer.twitterdemo.MainActivity
import com.loyer.twitterdemo.R
import com.loyer.twitterdemo.inflate
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.tweets.view.*

/**
 * Created by loyer on 19.04.2018.
 */
class TweetListAdapter(private var context: Context,
                       private var mDatabaseReference: DatabaseReference):RecyclerView.Adapter<TweetListAdapter.ViewHolder>() {

    private lateinit var listTweet: ArrayList<DataSnapshot>


    private val mListener = object : ChildEventListener {
        override fun onCancelled(p0: DatabaseError?) {
        }

        override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
        }

        override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
        }

        override fun onChildAdded(p0: DataSnapshot, p1: String?) {
            listTweet.add(p0)
            notifyDataSetChanged()
            Log.d("deneme",listTweet.size.toString())

        }

        override fun onChildRemoved(p0: DataSnapshot?) {
        }

    }
    init {
        listTweet = ArrayList(mutableListOf())
        mDatabaseReference = mDatabaseReference.child("posts")
        mDatabaseReference.addChildEventListener(mListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.tweets))
    }

    override fun getItemCount(): Int = listTweet.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listTweet[position],context)
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(snapshot: DataSnapshot,context: Context) {

            val post = snapshot.value  as HashMap<*,*>
            itemView.txtPost.text =  post["text"] as String
            val uri = post["postImage"] as String
            Picasso.with(context).load(uri).into(itemView.imgTweetImage)
            Log.d("deneme",uri)

        }

    }
}