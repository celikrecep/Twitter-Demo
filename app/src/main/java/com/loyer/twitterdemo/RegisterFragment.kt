package com.loyer.twitterdemo

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by loyer on 11.03.2018.
 */
class RegisterFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        var view: View = inflater!!.inflate(R.layout.fragment_register,container,false)

        return view
    }
}