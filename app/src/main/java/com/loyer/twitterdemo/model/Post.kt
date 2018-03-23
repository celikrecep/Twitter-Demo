package com.loyer.twitterdemo.model

/**
* Created by loyer on 21.03.2018.
*/
class Post {
    var tweetID: String? = null
    var tweetText: String? = null
    var tweetImageURL: String? = null
    var tweetPersonUID: String? = null

    constructor(tweetID: String?, tweetText: String?,
                tweetImageURL: String?, tweetPersonUID: String?){

        this.tweetID = tweetID
        this.tweetText = tweetText
        this.tweetImageURL = tweetImageURL
        this.tweetPersonUID = tweetPersonUID

    }
}