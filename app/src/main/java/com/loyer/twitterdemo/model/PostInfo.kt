package com.loyer.twitterdemo.model

/**
* Created by loyer on 23.03.2018.
*/
class  PostInfo(UserUID: String, text: String?, postImage: String?) {
    private var UserUID:String?= UserUID
    private var text:String?= text
    private var postImage:String?= postImage

    fun getUserUID():String?{
        return UserUID
    }

    fun getText():String?{
        return text
    }

    fun getPostImage():String?{
        return postImage
    }
}
