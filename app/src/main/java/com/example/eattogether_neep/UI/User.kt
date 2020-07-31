package com.example.eattogether_neep.UI

import android.content.Context
import android.provider.Settings
import java.util.*
import android.provider.Settings.Secure



object User {
    private const val USER_KEY = "user"

    private fun getUser(context: Context?): String {
        context?.getSharedPreferences(USER_KEY, Context.MODE_PRIVATE)
            .let {
                return it?.getString(USER_KEY, "") ?: ""
            }
    }

    private fun saveUser(context: Context?, user: String){
        context?.getSharedPreferences(USER_KEY, Context.MODE_PRIVATE)
            ?.edit()
            ?.putString(USER_KEY, user)
            ?.apply()
    }

    private fun makeUUID(context: Context?): String {
        return Secure.getString(context?.contentResolver, Secure.ANDROID_ID)
    }


    fun getUUID(context: Context?): String {
        getUser(context).let {
            when (it) {
                "" -> {
                    makeUUID(context).let { uuid ->
                        saveUser(
                            context,
                            uuid
                        )
                        return uuid
                    }
                }
                else -> return it
            }
        }
    }
}