package com.loyer.twitterdemo

/**
 * Created by loyer on 13.03.2018.
 */
class User {
    constructor(id: String? , name: String?, image: String?):this(){
        this.id = id
        this.name = name
        this.image = image
    }
    constructor()

    private var id: String? = null
    private var name: String? = null
    private var image: String? = null

    fun getId(): String?{
        return id
    }
    fun getName(): String?{
        return name
    }
    fun getImage(): String?{
        return image
    }
}